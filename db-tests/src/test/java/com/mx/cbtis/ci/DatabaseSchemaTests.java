package com.mx.cbtis.ci;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Real JDBC checks restricted to a disposable CI database. This class deliberately
 * refuses the application's MariaDB endpoint before opening a connection.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseSchemaTests {
    private static final String CI_URL =
        "jdbc:mariadb://admin-proyectos-db-ci:3306/ci_gestor";
    private static final String CI_DATABASE = "ci_gestor";
    private static final Set<String> TABLES = Set.of(
        "asignaciones", "comentarios", "hibernate_sequence", "proyectos", "tareas", "usuario");
    private static Connection connection;

    @BeforeAll
    static void openAndPrepareOnlyCiDatabase() throws Exception {
        String url = requiredEnvironment("CI_DB_URL");
        requireCiEndpoint(url);
        String user = requiredEnvironment("CI_DB_USER");
        if (!"ci_runner".equals(user)) {
            throw new IllegalStateException("Use CI_DB_USER=ci_runner; never use a root or application account.");
        }
        Properties credentials = new Properties();
        credentials.setProperty("user", user);
        credentials.setProperty("password", requiredEnvironment("CI_DB_PASSWORD"));
        credentials.setProperty("connectTimeout", "10000");
        credentials.setProperty("socketTimeout", "30000");
        connection = DriverManager.getConnection(url, credentials);
        assertEquals(CI_DATABASE, connection.getCatalog(), "Unexpected database: refusing schema changes.");
        resetCiSchema();
        System.out.println("CI database guard passed: admin-proyectos-db-ci / ci_gestor; schema imported.");
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }
            connection.close();
        }
    }

    private static String requiredEnvironment(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing " + name
                + ". Run the isolated database pipeline; see db-tests/README.md."
                + " These tests must not use the application's database.");
        }
        return value;
    }

    private static void requireCiEndpoint(String url) {
        if (!CI_URL.equals(url)) {
            throw new IllegalStateException("Unsafe database URL rejected. The only allowed target is " + CI_URL);
        }
    }

    private static void resetCiSchema() throws SQLException, IOException {
        // Recheck the allowlist immediately before any DDL, not only during setup.
        requireCiEndpoint(requiredEnvironment("CI_DB_URL"));
        assertEquals(CI_DATABASE, connection.getCatalog());
        assertTrue(connection.getAutoCommit(), "Schema initialization must run outside test transactions.");
        try (Statement ddl = connection.createStatement()) {
            for (String table : new String[] {
                "asignaciones", "comentarios", "tareas", "proyectos", "usuario", "hibernate_sequence"
            }) {
                ddl.execute("DROP TABLE IF EXISTS `" + table + "`");
            }
            String script;
            try (InputStream resource = DatabaseSchemaTests.class.getResourceAsStream("/schema.sql")) {
                if (resource == null) {
                    throw new IOException("schema.sql was not copied into the test resources.");
                }
                script = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
            }
            script = script.replace("\uFEFF", "").replaceAll("(?m)^\\s*--.*$", "");
            // The checked-in schema has simple CREATE TABLE / INSERT statements;
            // this importer is not a parser for routines or arbitrary SQL dumps.
            for (String statement : script.split(";")) {
                if (!statement.isBlank()) {
                    ddl.execute(statement.strip());
                }
            }
        }
    }

    @Test
    @Order(1)
    @DisplayName("BD-01: safety guard rejects production, alternate hosts and URL parameters")
    void unsafeTargetsAreRejectedBeforeConnecting() {
        for (String url : new String[] {
            "jdbc:mariadb://localhost:3297/dbgestor",
            "jdbc:mariadb://admin-proyectos-mariadb:3306/dbgestor",
            "jdbc:mariadb://admin-proyectos-db-ci:3306/dbgestor",
            CI_URL + "?allowMultiQueries=true"
        }) {
            assertThrows(IllegalStateException.class, () -> requireCiEndpoint(url));
        }
        assertDoesNotThrow(() -> requireCiEndpoint(CI_URL));
    }

    @Test
    @Order(2)
    @DisplayName("BD-02: clean schema import is repeatable, including its technical seed")
    void initializationCanRunTwice() throws Exception {
        resetCiSchema();
        assertEquals(TABLES, actualTables());
        assertEquals(1L, sequenceValue());
        resetCiSchema();
        assertEquals(TABLES, actualTables());
        assertEquals(1L, sequenceValue());
    }

    @Test
    @Order(3)
    @DisplayName("BD-03: the CI database contains exactly the six expected tables")
    void expectedTablesExistWithoutUnrelatedTables() throws SQLException {
        assertEquals(TABLES, actualTables());
    }

    private static Set<String> actualTables() throws SQLException {
        Set<String> names = new LinkedHashSet<>();
        try (PreparedStatement query = connection.prepareStatement(
            "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE'")) {
            query.setString(1, CI_DATABASE);
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    names.add(rows.getString(1));
                }
            }
        }
        return names;
    }

    @Test
    @Order(4)
    @DisplayName("BD-04: every expected column has the SQL type defined in the schema")
    void columnsAndJdbcTypesMatchSchema() throws SQLException {
        Map<String, Map<String, Integer>> expected = Map.of(
            "asignaciones", columns(
                "id_asignacion", Types.INTEGER, "id_usuario", Types.INTEGER,
                "id_tarea", Types.INTEGER, "fecha_asignacion", Types.DATE),
            "comentarios", columns(
                "id_comentario", Types.INTEGER, "texto_comentario", Types.VARCHAR,
                "fecha_hora", Types.TIMESTAMP, "id_usuario", Types.VARCHAR, "id_tarea", Types.INTEGER),
            "hibernate_sequence", columns("next_val", Types.BIGINT),
            "proyectos", columns(
                "id_proyecto", Types.INTEGER, "nombre", Types.VARCHAR, "descripcion", Types.VARCHAR,
                "fecha_inicio", Types.DATE, "fecha_finalizacion", Types.DATE,
                "estado_proyecto", Types.VARCHAR, "fondo", Types.VARCHAR),
            "tareas", columns(
                "id_tarea", Types.INTEGER, "nombre", Types.VARCHAR, "descripcion", Types.VARCHAR,
                "fecha_vencimiento", Types.DATE, "estado", Types.VARCHAR,
                "lista", Types.VARCHAR, "id_proyecto", Types.INTEGER),
            "usuario", columns(
                "id_usuario", Types.INTEGER, "nombre", Types.VARCHAR,
                "correo_electronico", Types.VARCHAR, "contrasena", Types.VARCHAR,
                "rol", Types.VARCHAR, "imagen_usuario", Types.VARCHAR));
        DatabaseMetaData metadata = connection.getMetaData();
        for (Map.Entry<String, Map<String, Integer>> table : expected.entrySet()) {
            Map<String, Integer> actual = new LinkedHashMap<>();
            try (ResultSet rows = metadata.getColumns(CI_DATABASE, null, table.getKey(), "%")) {
                while (rows.next()) {
                    actual.put(rows.getString("COLUMN_NAME"), rows.getInt("DATA_TYPE"));
                }
            }
            assertEquals(table.getValue(), actual, "Column mismatch for " + table.getKey());
        }
    }

    private static Map<String, Integer> columns(Object... definitions) {
        Map<String, Integer> columns = new LinkedHashMap<>();
        for (int i = 0; i < definitions.length; i += 2) {
            columns.put((String) definitions[i], (Integer) definitions[i + 1]);
        }
        return columns;
    }

    @Test
    @Order(5)
    @DisplayName("BD-05: hibernate_sequence contains exactly its initial technical value")
    void hibernateSequenceHasOneInitialValue() throws SQLException {
        assertEquals(1L, sequenceValue());
    }

    private static long sequenceValue() throws SQLException {
        try (Statement query = connection.createStatement();
             ResultSet rows = query.executeQuery("SELECT next_val FROM hibernate_sequence")) {
            assertTrue(rows.next(), "Missing initial sequence row.");
            long value = rows.getLong(1);
            assertFalse(rows.wasNull());
            assertFalse(rows.next(), "Only one technical sequence row is expected.");
            return value;
        }
    }

    @Test
    @Order(6)
    @DisplayName("BD-06: prepared statements preserve quoted names through project CRUD")
    void projectCrudHandlesQuotedNames() throws SQLException {
        connection.setAutoCommit(false);
        try {
            long id = insertProject("Proyecto O'Connor CI");
            try (PreparedStatement read = connection.prepareStatement(
                "SELECT nombre, fecha_inicio FROM proyectos WHERE id_proyecto = ?")) {
                read.setLong(1, id);
                try (ResultSet row = read.executeQuery()) {
                    assertTrue(row.next());
                    assertEquals("Proyecto O'Connor CI", row.getString("nombre"));
                    assertEquals(LocalDate.of(2026, 1, 15), row.getDate("fecha_inicio").toLocalDate());
                    assertFalse(row.next());
                }
            }
            try (PreparedStatement update = connection.prepareStatement(
                "UPDATE proyectos SET nombre = ? WHERE id_proyecto = ?")) {
                update.setString(1, "Actualizado O'Connor CI");
                update.setLong(2, id);
                assertEquals(1, update.executeUpdate());
            }
            try (PreparedStatement read = connection.prepareStatement(
                "SELECT nombre FROM proyectos WHERE id_proyecto = ?")) {
                read.setLong(1, id);
                try (ResultSet row = read.executeQuery()) {
                    assertTrue(row.next());
                    assertEquals("Actualizado O'Connor CI", row.getString(1));
                }
            }
            try (PreparedStatement delete = connection.prepareStatement(
                "DELETE FROM proyectos WHERE id_proyecto = ?")) {
                delete.setLong(1, id);
                assertEquals(1, delete.executeUpdate());
            }
            assertEquals(0L, countProject(id));
        } finally {
            rollbackAndRestoreAutoCommit();
        }
    }

    @Test
    @Order(7)
    @DisplayName("BD-07: rollback removes inserted test records")
    void transactionRollbackLeavesNoTestRecords() throws SQLException {
        long id;
        connection.setAutoCommit(false);
        try {
            id = insertProject("CI_ROLLBACK_ONLY");
            assertEquals(1L, countProject(id));
        } finally {
            rollbackAndRestoreAutoCommit();
        }
        assertEquals(0L, countProject(id));
    }

    @Test
    @Order(8)
    @DisplayName("BD-08: the project primary key rejects duplicate IDs")
    void primaryKeyRejectsDuplicateId() throws SQLException {
        connection.setAutoCommit(false);
        try (PreparedStatement insert = connection.prepareStatement(
            "INSERT INTO proyectos (id_proyecto, nombre) VALUES (?, ?)")) {
            insert.setInt(1, 710001);
            insert.setString(2, "CI_PK_ONLY");
            assertEquals(1, insert.executeUpdate());
            SQLException duplicate = assertThrows(SQLException.class, insert::executeUpdate);
            assertEquals("23000", duplicate.getSQLState());
            assertEquals(1062, duplicate.getErrorCode());
            assertEquals(1L, countProject(710001));
        } finally {
            rollbackAndRestoreAutoCommit();
        }
        assertEquals(0L, countProject(710001));
    }

    private static long insertProject(String name) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
            "INSERT INTO proyectos (nombre, descripcion, fecha_inicio, fecha_finalizacion, estado_proyecto, fondo)"
                + " VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, name);
            insert.setString(2, "Datos exclusivos de prueba CI");
            insert.setDate(3, java.sql.Date.valueOf(LocalDate.of(2026, 1, 15)));
            insert.setDate(4, java.sql.Date.valueOf(LocalDate.of(2026, 1, 31)));
            insert.setString(5, "En proceso");
            insert.setString(6, "/ci-only.gif");
            assertEquals(1, insert.executeUpdate());
            try (ResultSet keys = insert.getGeneratedKeys()) {
                assertTrue(keys.next(), "Missing generated project ID.");
                return keys.getLong(1);
            }
        }
    }

    private static long countProject(long id) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement(
            "SELECT COUNT(*) FROM proyectos WHERE id_proyecto = ?")) {
            query.setLong(1, id);
            try (ResultSet rows = query.executeQuery()) {
                assertTrue(rows.next());
                return rows.getLong(1);
            }
        }
    }

    private static void rollbackAndRestoreAutoCommit() throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
    }
}
