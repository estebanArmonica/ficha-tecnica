#!/bin/bash
# start-app.bat (pero en Bash para Git Bash)
# Usar en Git Bash de Windows

set -e

echo "========================================"
echo "  FICHA TÉCNICA PACIENTES - GRUPOBIOS"
echo "  Inicio de aplicación Spring Boot"
echo "========================================"
echo ""

# Detectar sistema
if [[ "$OS" == "Windows_NT" ]]; then
    echo "🌐 Sistema: Windows"
    SEP=";"
else
    echo "🐧 Sistema: Linux/Mac"
    SEP=":"
fi

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no encontrado"
    echo ""
    echo "📦 Instalación rápida:"
    echo "1. Descarga Maven: https://maven.apache.org/download.cgi"
    echo "2. Extrae a C:\Program Files\Apache\Maven"
    echo "3. Agrega a PATH:"
    echo "   - Win + X → Sistema → Configuración avanzada"
    echo "   - Variables de entorno → Path → Nuevo"
    echo "   - Agrega: C:\Program Files\Apache\Maven\bin"
    echo "4. Reabre Git Bash"
    exit 1
fi

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "❌ Java no encontrado"
    echo ""
    echo "☕ Instala Java 17:"
    echo "1. Descarga: https://adoptium.net/temurin/releases/"
    echo "2. Ejecuta instalador"
    echo "3. Verifica instalación:"
    echo "   java -version"
    exit 1
fi

# Mostrar info
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
MVN_VERSION=$(mvn -version 2>&1 | head -1 | cut -d' ' -f3)

echo "✅ Java $JAVA_VERSION"
echo "✅ Maven $MVN_VERSION"
echo "📁 Directorio: $(pwd)"
echo ""

# Opciones
echo "Selecciona opción:"
echo "1) 🚀 Iniciar en modo desarrollo (default)"
echo "2) 🏭 Iniciar en modo producción"
echo "3) 🔧 Solo construir proyecto"
echo "4) 🧹 Limpiar y construir"
echo ""
read -p "Opción [1]: " OPTION
OPTION=${OPTION:-1}

case $OPTION in
    (1)
        echo "▶️  Iniciando modo desarrollo..."
        mvn spring-boot:run -Dspring-boot.run.profiles=dev
        ;;
    (2)
        echo "🏭 Iniciando modo producción..."
        mvn spring-boot:run -Dspring-boot.run.profiles=prod
        ;;
    (3)
        echo "🔨 Construyendo proyecto..."
        mvn clean package -DskipTests
        echo "✅ Construcción completada"
        echo "📦 JAR en: target/"
        ;;
    (4)
        echo "🧹 Limpiando y construyendo..."
        mvn clean package -DskipTests
        echo "✅ Limpieza y construcción completadas"
        ;;
    (*)
        echo "❌ Opción no válida"
        exit 1
        ;;
esac