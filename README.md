
# Ficha Tecnica API Simple

Este proyecto tiene un fin de ser una API simple de una ficha tecnica de pacientes, donde se utiliza diferentes tablas para dar y generar una ficha para una paciente, dentro de la información se generar un archivo PDF donde con simples datos se entrega la información del paciente como datos básico (nombre, rut, edad, etc) hasta el tipo de sangre del paciente.

## Documentación

Para este proyecto se utilizo diferentes herramientas a cabo, desde un programa para generar el archivo PDF con los datos correspondientes en formato `jrxml` hasta la creación de patrón de diseño:

- [JasperReports 6.21.5 Maven](https://mvnrepository.com/artifact/net.sf.jasperreports/jasperreports/6.21.5)
- [JasperReports 6.21.5 Studio](https://community.jaspersoft.com/files/file/113-jaspersoft-studio-community-edition/)
- [Java 17 OpenJDK Version Windows](https://learn.microsoft.com/es-es/java/openjdk/download)
- [Visual Studio Code](https://code.visualstudio.com/download)
- [o si usas eclipse](https://www.eclipse.org/downloads/)
- [Oracle XE 21c](https://www.oracle.com/latam/database/technologies/xe-downloads.html)
- [Spring Boot 3.5.9](https://start.spring.io/)
- [OpenAIP "Swagger"](https://springdoc-org.translate.goog/?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es&_x_tr_pto=tc#google_vignette)

En caso de usar SQL Developer puedes descargarlo desde la misma página de Oracle, pero si usas otro no existe problemas siempre y cuando sea compatible con XE de Oracle.

## Levantamiento del proyecto

Para levantar este proyecto se debe realizar las siguientes partes:


Primero descargamos el repositorio de Github usando comandos de git

```bash
  git clone https://github.com/estebanArmonica/ficha-tecnica.git
```

esto realizara la descarga del repositorio de github a su ordenador, siguiendo con lo siguientes lo que debemos hacer antes de levantar el proyecto es crear en nuestra base de datos Oracle el usuario en el cual se utilizara.

> [!NOTE]
> importante, este proyecto se creo usando Oracle XE, si usa otro no se preocupe ya que no es problema usar otro si es un wallet de Oracle Cloud o EM.

Cuando se haya realizado la creación del usuario en Oracle en el proyecto y en la raíz del proyecto se debe crear un archivo `.env` el cual contiene las variables de entorno correspondientes 

> [!NOTE]
> para correr el proyecto se usar el archivo .env si no quieres crear dicho archivo debes ir al `application.properties` y cambiar la configuración de la variables

cuando se cree el `.env` se debe crear estas variables

```bash
  # ============ APLICACIÓN ============
    APP_PORT=8080
    APP_MEMORY_LIMIT=1G
    APP_MEMORY_RESERVE=512M
    SPRING_PROFILES_ACTIVE=prod

    # ============ ORACLE DATABASE ============
    ORACLE_PORT=puerto donde corre
    DATABASE_URL=jdbc:oracle:thin:@host_donde_corre:puerto_donde_corre:XE
    DATABASE_USER=usuario
    DATABASE_PASSWORD=contraseña

    # ============ ORACLE CONTAINER ============
    ORACLE_SYS_PASSWORD=Oracle_2024
    ORACLE_CHARACTERSET=AL32UTF8
    ORACLE_NLS_CHARACTERSET=AL32UTF8

    # ============ JAVA CONFIG ============
    JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC -Duser.timezone=America/Santiago
```
Una vez listo solo queda ejecutar el proyecto, para esto puedes hacerlo de dos maneras, la primera es que existe un archivo llamada `run.sh` un ejecutable bash donde proyecto se levanta sin problemas o bien se puede ejecutar directamente desde el IDE que estes utilizando

```bash
  # en caso de usar run.sh
  # si tienes git bash busca el archivo run.sh y ejecutalo de esta forma

  ./run.sh

  # ahora si no lo vas a utilizar solo ejecutable desde un IDE
```

Cuando se ejecute cualquiera de las formas escojidas el proyecto mostrara por terminal que esta corriendo por el puerto `8080` el puerto por defecto de los servidores de Java "en este caso con spring boot se usa apache tomcat".

## Proyecto en ejecución

Una vez en ejecución para probar la API se tiene varias opciones, se puede utilizar alguna herramienta de test de API a gusto de uno como Postman, Insomnia o puedes realizarlo directamente desde OpenAPI usando Swagger desde el siguiente Link

```bash
    # recomendado con Documentación
    link Swagger: http://localhost:8080/swagger-ui/index.html
```

Lo recomiendo en personal para probar cada endpoints "sobre todo uno que tiene para realizar la generación de una ficha tecnica en formato PDF" desconozco si en alguna herramienta para realizar testing de las API se pueda usar ese endpoint.

Para usar cada endpoint primero debe borrar cada ID ya que los ID son `auto-incrementales` por creaciones de sequences.

> [!NOTE]
> Lo volvere a repetir los ID son Auto-incrementales.

para usar el JSON de Tipo de sangre use este

```bash
    # cambie string por el nombre del tipo de sangre
    # y ejecute el json
    {
      "nombreTipoSangre": "string"
    }
```

Para Genero use este JSON
```bash
    # cambie string por el nombre del genero
    # ingrese un sigla "M o F" y ejecute el json
    {
      "nombreGenero": "string",
      "sigla": "s"
    }
```
y para el paciente use este JSON es un poco más extenso

```bash
    # Cambie todos los datos pacientes 
    # en fecha de nacimiento use el formato que le muestra el JSON

    # un punto importante es que Nro de paciente no se agrega ya que este tiene un método el cual crea un nro de paciente unico y random para cada paciente que se registre
    {
      "nombrePaciente": "string",
      "rutPaciente": "string",
      "correoPaciente": "string",
      "fechaNacimiento": "2026-01-03",
      "activo": true,
      "genero": {
        "id": 9007199254740991
      },
      "tipoSangre": {
        "id": 9007199254740991
      }
    }
```

Estos mismo JSON uselos también para `POST y PUT` en PUT solo cambiar algunos datos que quiera cambiar en el sistema.

Como punto importante en `Delete` se utilizo otra logica `Soft Delete` borrado logico en el cual el dato que usted borre se reflejara en el JSON pero en la base de datos seguira existiendo.

> [!NOTE]
> Por logica lo deje de esa forma si por ejemplo en algún proyecto futuro se decide realizar consulta SQL.

## Adicional

Como adicional el proyecto antes de probar las endpoints por Swagger se creo Test unitarios usando JUnit5 y Mockito, el cual comparto la documentación de ambos:

- [JUnit5 Documentación](https://docs.junit.org/5.5.0/user-guide/index.html)
- [Mockito Documentación](https://site.mockito.org/)

También en este proyecto principalmente se uso el patron de diseño Command, a pesar que no era completamente necesario usar dicho patron de diseño y menos cualquiera lo utilice con el fin de poder enseñar y tener como un proyecto a fin, donde saber como integrar y como se utiliza un patrón de diseño y como este ayuda a los proyectos en diferentes ejecuciónes como levantamientos, mejoras en respuesta en creaciones de objetos hasta adaptaciones de tipos de formatos.

- [Más sobre patrones de diseño](https://refactoring.guru/es/design-patterns)

Existe también ademas un backup de la base de datos el cual tiene las tablas listas creadas por Hibernate y algunos datos creados a partir de los endpoints probados despues de usar Test Unitarios "eso si es un script SQL el backup".

## Autor

Puedes contactarme directamente al correo haciendo click en mi nombre.
- [Esteban Hernán Lobos Canales](esteban.hernan.lobos@gmail.com)
