# Incidencia detectada y corrección de Git LFS

- Ejecución que mostró el problema: http://localhost:8090/job/Unidad2-Cliente/3/
- Etapa: Checkout, antes de compilar o ejecutar pruebas.
- Comando inicial: `git lfs install --local`.
- Salida observada: `mkdir /dev/null: not a directory`; el comando terminó con código 2 y Jenkins marcó FAILURE.
- Causa: el entorno de checkout de Jenkins deshabilita los hooks mediante una ruta que Git LFS no puede usar como directorio de hooks.
- Corrección: inicializar únicamente los filtros con `git lfs install --local --skip-repo` y descargar después con `git lfs pull`. No se modifica la política de hooks de Jenkins.
- Commit: https://github.com/agustn134/ProyectoGestionProcesosSoftware/commit/00f74e2
- Verificación: consultar la ejecución final identificada en validacion.md y lfs-cliente.txt. En `git lfs ls-files`, un asterisco indica que el archivo del directorio de trabajo contiene el recurso completo; un guion indica que sigue siendo una referencia.

Este fallo no se oculta ni se cuenta como ejecución aprobada. Se conserva para explicar el problema y su corrección en el video.
