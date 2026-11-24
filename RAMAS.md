# Organización de ramas

El flujo de integración del repositorio es:

```text
main
└── develop
    ├── featured/parte-1
    └── featured/parte-2
        └── featured/despliegue
```

| Rama | Propósito | Estado de referencia |
| --- | --- | --- |
| `main` | Entrega estable publicada | Parte 1 verificada, commit `cf004e1` |
| `develop` | Integración antes de publicar una nueva entrega estable | Se creó desde `main` en `cf004e1` |
| `featured/parte-1` | Conservar los POM y evidencias de la primera parte | Referencia al estado entregado `cf004e1` |
| `featured/parte-2` | Pruebas ampliadas, seguridad y tres pipelines | Rama de trabajo de esta entrega |
| `featured/despliegue` | Código que usa Render | Incluye la Parte 2 y las correcciones de proyectos y tareas |

El árbol describe el flujo de trabajo. La actualización a Java 21 se conserva en el historial de `main` y `featured/parte-1`. Se retiró `featured/upgrade-java21` porque apuntaba al mismo commit.

La Parte 2 se publica en su propia rama. Para integrarla posteriormente, revisar primero `featured/parte-2` hacia `develop`; después revisar `develop` hacia `main`. No se han fusionado automáticamente esos cambios con las ramas estables.

## Consultar la Parte 2

```bash
git fetch origin
git switch featured/parte-2
git pull --ff-only origin featured/parte-2
git lfs install --local
git lfs pull
```

Código y evidencias: https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/featured/parte-2

Los trabajos `Unidad2-Cliente` y `Unidad2-API` conservan la referencia a `main`. Los tres trabajos `Practica3-Parte2-*` utilizan exclusivamente `featured/parte-2`.
