# Organización de ramas

El flujo de integración del repositorio es:

```text
main
└── develop
    ├── featured/upgrade-java21
    ├── featured/parte-1
    └── featured/parte-2
```

| Rama | Propósito | Estado de referencia |
| --- | --- | --- |
| `main` | Entrega estable publicada | Parte 1 verificada, commit `cf004e1` |
| `develop` | Integración antes de publicar una nueva entrega estable | Se creó desde `main` en `cf004e1` |
| `featured/upgrade-java21` | Conservar el trabajo del upgrade de ambos servicios | Referencia al estado entregado; incluye el upgrade inicial `e523ff4` en su historial |
| `featured/parte-1` | Conservar los POM y evidencias de la primera parte | Referencia al estado entregado `cf004e1` |
| `featured/parte-2` | Pruebas ampliadas, seguridad y tres pipelines | Rama de trabajo de esta entrega |

Git no almacena una relación padre-hijo entre nombres de ramas. El árbol anterior describe el flujo de integración y revisión: las tres ramas de trabajo se crearon desde el mismo punto de `develop`. No se reescribió el historial anterior.

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
