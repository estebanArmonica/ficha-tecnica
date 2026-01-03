#!/usr/bin/env bash
# Non-interactive runner for local use and CI (Render.com)

set -euo pipefail

echo "========================================"
echo "  FICHA TÉCNICA PACIENTES - GRUPOBIOS"
echo "  Creado por: Esteban Hernán Lobos Canales"
echo "  Fecha de creación: 31-12-2025"
echo "  Fecha de termino: 04-01-2026"
echo "  Inicio de aplicación Spring Boot"
echo "========================================"

# Prefer the Maven wrapper if present (better for reproducible builds)
MVN_CMD=""
if [ -x "./mvnw" ]; then
    MVN_CMD="./mvnw"
elif [ -x "./mvnw.cmd" ] && [[ "$OS" == "Windows_NT" ]]; then
    MVN_CMD="./mvnw.cmd"
else
    MVN_CMD="mvn"
fi

function check_command() {
    if ! command -v "$1" &> /dev/null; then
        echo "❌ $1 no encontrado. Instala $1 y vuelve a intentarlo."
        exit 1
    fi
}

# Ensure Java is available (required to run the built JAR)
check_command java

# Print versions (best-effort)
JAVA_VERSION=$(java -version 2>&1 | head -n1 || true)
MVN_VERSION=$($MVN_CMD -v 2>&1 | head -n1 || true)
echo "✅ Java: ${JAVA_VERSION}"
echo "✅ Maven: ${MVN_VERSION}"
echo "📁 Directorio: $(pwd)"

# Behavior is driven by environment variables for non-interactive use
# ACTION: run | build | clean_build
# SPRING_PROFILE: dev | prod
# SKIP_TESTS: true | false
ACTION=${ACTION:-run}
SPRING_PROFILE=${SPRING_PROFILE:-prod}
SKIP_TESTS=${SKIP_TESTS:-true}

echo "▶ Acción: ${ACTION}"
echo "▶ Perfil: ${SPRING_PROFILE}"

if [ "${ACTION}" = "build" ]; then
    echo "🔨 Construyendo proyecto..."
    ${MVN_CMD} clean package $( [ "${SKIP_TESTS}" = "true" ] && echo -DskipTests || echo )
    echo "✅ Construcción completada. JAR en target/"
    exit 0
fi

if [ "${ACTION}" = "clean_build" ]; then
    echo "🧹 Limpieza + construcción..."
    ${MVN_CMD} clean package $( [ "${SKIP_TESTS}" = "true" ] && echo -DskipTests || echo )
    echo "✅ Limpieza y construcción completadas. JAR en target/"
    exit 0
fi

# Default: run the application
if [ "${ACTION}" = "run" ]; then
    if [ "${SPRING_PROFILE}" = "dev" ]; then
        echo "▶️ Iniciando en modo desarrollo (mvn spring-boot:run)..."
        ${MVN_CMD} -Dspring-boot.run.profiles=dev spring-boot:run
        exit 0
    fi

    # Production-like startup: build artifact and run with java -jar
    echo "▶️ Iniciando en modo producción: build + java -jar"
    ${MVN_CMD} clean package $( [ "${SKIP_TESTS}" = "true" ] && echo -DskipTests || echo )

    # Find produced JAR (first match)
    shopt -s nullglob || true
    jars=(target/*.jar)
    if [ ${#jars[@]} -eq 0 ]; then
        echo "❌ No se encontró ningún JAR en target/"
        exit 1
    fi
    JAR=${jars[0]}
    echo "📦 Ejecutando: java -jar ${JAR}"
    exec java -jar "${JAR}"
fi

echo "❌ Acción desconocida: ${ACTION}. Valores soportados: run, build, clean_build"
exit 1