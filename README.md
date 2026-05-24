# PROYECTO
## Como iniciar el proyecto 

1. Descomprimir el archivo .RAR
2. Abrir Visual Studio u otro IDE que admita el formato JAVA
3. Iniciar el programa desde la ruta de acceso en donde se descomprimio el archivo .RAR


## Estructura del proyecto

```text
SIGEH_Test_System/
├── Source Packages/
│
├── Test/
│   ├── Cita.java
│   ├── CitasPanel.java
│   ├── DashboardPanel.java
│   ├── DataRepository.java
│   ├── Factura.java
│   ├── FacturacionPanel.java
│   ├── HistoriaClinica.java
│   ├── HistoriaClinicaPanel.java
│   ├── MenuIcon.java
│   ├── Paciente.java
│   ├── PacientesPanel.java
│   ├── ReportesPanel.java
│   ├── SIGEH_Modern_UI.java
│   └── UIUtils.java
│
├── resources/
│   └── logo.png
│
├── Test Packages/
├── Libraries/
└── Test Libraries/
```
## Arquitectura del sistema

El sistema SIGEH fue desarrollado utilizando Java Swing bajo una arquitectura modular orientada a objetos.

La aplicación separa:

Modelos de datos
Paneles gráficos
Utilidades visuales
Persistencia temporal
Lógica de negocio

Esto facilita el mantenimiento y escalabilidad del sistema.


Descripción de clases y módulos
| Clase | Responsabilidad |
|---|---|
| SIGEH_Modern_UI.java | Clase principal del sistema y ventana principal |
| DashboardPanel.java | Panel principal del dashboard |
| PacientesPanel.java | Gestión visual de pacientes |
| Paciente.java | Modelo de datos de pacientes |
| CitasPanel.java | Gestión de citas médicas |
| Cita.java | Modelo de citas |
| FacturacionPanel.java | Gestión de facturación |
| Factura.java | Modelo de facturas |
| HistoriaClinicaPanel.java | Gestión visual de historias clínicas |
| HistoriaClinica.java | Modelo de historias clínicas |
| ReportesPanel.java | Visualización de reportes |
| DataRepository.java | Simulación de persistencia de datos |
| UIUtils.java | Utilidades reutilizables para interfaz |
| MenuIcon.java | Iconografía y elementos visuales |
| logo.png | Recurso gráfico institucional |

Aplicación de principios SOLID
S — Single Responsibility Principle
Cada clase tiene una única responsabilidad específica dentro del sistema.
Responsabilidades por clase
| Clase | Responsabilidad |
|---|---|
| Paciente.java | Representar pacientes |
| Cita.java | Representar citas médicas |
| Factura.java | Representar facturas |
| HistoriaClinica.java | Representar historias clínicas |
| PacientesPanel.java | Interfaz de pacientes |
| CitasPanel.java | Interfaz de citas |
| FacturacionPanel.java | Interfaz de facturación |
| HistoriaClinicaPanel.java | Interfaz clínica |
| UIUtils.java | Utilidades visuales |
| DataRepository.java | Almacenamiento temporal |
