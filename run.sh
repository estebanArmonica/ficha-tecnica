#!/bin/bash
# run.sh
# Script para ejecutar la aplicación Spring Boot con Docker
# 
# DESCRIPCIÓN:
# Este script automatiza el despliegue de la aplicación Ficha Técnica Pacientes
# utilizando Docker y Docker Compose. Incluye validaciones, construcción de imágenes,
# gestión de contenedores y verificación de salud.
#
# USO:
#   ./run-app.sh [COMANDO] [PERFIL]
#   Ejemplos:
#     ./run-app.sh           # Inicia en producción (comando por defecto)
#     ./run-app.sh dev       # Inicia en modo desarrollo
#     ./run-app.sh stop      # Detiene todos los servicios
#     ./run-app.sh logs      # Muestra logs de la aplicación
#     ./run-app.sh help      # Muestra ayuda completa
#
# REQUISITOS PREVIOS:
#   1. Docker instalado y funcionando
#   2. Docker Compose instalado
#   3. Archivo .env configurado en la raíz del proyecto
#   4. Conexión a internet para descargar imágenes Docker
#
# INSTALACIÓN DE DOCKER (si no lo tienes):
#
#   PARA LINUX (Ubuntu/Debian):
#   ----------------------------
#   1. Actualizar paquetes:
#      sudo apt-get update
#   
#   2. Instalar dependencias:
#      sudo apt-get install -y \
#          apt-transport-https \
#          ca-certificates \
#          curl \
#          gnupg \
#          lsb-release
#   
#   3. Agregar clave GPG oficial de Docker:
#      curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
#   
#   4. Configurar repositorio estable:
#      echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
#      $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
#   
#   5. Instalar Docker Engine:
#      sudo apt-get update
#      sudo apt-get install -y docker-ce docker-ce-cli containerd.io
#   
#   6. Agregar usuario actual al grupo docker (para no usar sudo):
#      sudo usermod -aG docker $USER
#      newgrp docker  # O cerrar y reabrir la terminal
#
#   PARA LINUX (CentOS/RHEL/Fedora):
#   --------------------------------
#   1. Instalar dependencias:
#      sudo yum install -y yum-utils
#   
#   2. Agregar repositorio Docker:
#      sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
#   
#   3. Instalar Docker:
#      sudo yum install -y docker-ce docker-ce-cli containerd.io
#   
#   4. Iniciar y habilitar Docker:
#      sudo systemctl start docker
#      sudo systemctl enable docker
#   
#   5. Agregar usuario al grupo docker:
#      sudo usermod -aG docker $USER
#
#   PARA MAC:
#   ---------
#   1. Descargar Docker Desktop desde:
#      https://docs.docker.com/desktop/mac/install/
#   
#   2. Instalar siguiendo el asistente
#   
#   3. Arrancar Docker Desktop desde Aplicaciones
#
#   PARA WINDOWS:
#   -------------
#   1. Requisitos:
#      - Windows 10/11 Pro, Enterprise o Education (64-bit)
#      - Habilitar Hyper-V y Virtualización en BIOS
#   
#   2. Descargar Docker Desktop desde:
#      https://docs.docker.com/desktop/windows/install/
#   
#   3. Instalar y reiniciar
#   
#   4. En Windows Home, usar Docker Toolbox:
#      https://github.com/docker/toolbox/releases
#
# INSTALACIÓN DE DOCKER COMPOSE:
# ------------------------------
#
#   MÉTODO RECOMENDADO (instalar como plugin de Docker):
#   ----------------------------------------------------
#   1. Para Linux:
#      sudo apt-get update
#      sudo apt-get install -y docker-compose-plugin
#   
#   2. Verificar instalación:
#      docker compose version
#
#   MÉTODO ALTERNATIVO (binario standalone):
#   ----------------------------------------
#   1. Descargar binario:
#      sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" \
#          -o /usr/local/bin/docker-compose
#   
#   2. Dar permisos de ejecución:
#      sudo chmod +x /usr/local/bin/docker-compose
#   
#   3. Verificar instalación:
#      docker-compose --version
#
# VERIFICAR INSTALACIÓN:
# ----------------------
#   Ejecutar los siguientes comandos para verificar:
#     docker --version          # Debe mostrar versión de Docker
#     docker-compose --version  # Debe mostrar versión de Docker Compose
#     docker run hello-world    # Debe descargar y ejecutar contenedor de prueba
#
# AUTOR: Esteban Hernán Lobos Canales
# VERSIÓN: 1.0.0
# FECHA: $(date +%Y-%m-%d)
#

set -e  # Detener en el primer error

# ============ CONFIGURACIÓN ============
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ============ FUNCIONES ============
print_header() {
    echo -e "${BLUE}"
    echo "╔══════════════════════════════════════════════════════╗"
    echo "║      Ficha Técnica Pacientes - GrupoBios             ║"
    echo "║      Script de ejecución Spring Boot                 ║"
    echo "╚══════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

print_step() {
    echo -e "${YELLOW}[STEP] $1${NC}"
}

print_success() {
    echo -e "${GREEN}[SUCCESS] $1${NC}"
}

print_error() {
    echo -e "${RED}[ERROR] $1${NC}"
}

print_info() {
    echo -e "${BLUE}[INFO] $1${NC}"
}

check_requirements() {
    print_step "Verificando requisitos del sistema..."
    
    # # COMENTADO: Verificación de Docker
    # if ! command -v docker &> /dev/null; then
    #     print_error "Docker no está instalado"
    #     echo "Instala Docker desde: https://docs.docker.com/get-docker/"
    #     exit 1
    # fi
    
    # # COMENTADO: Verificar Docker Compose
    # if ! command -v docker-compose &> /dev/null; then
    #     print_error "Docker Compose no está instalado"
    #     echo "Instala Docker Compose desde: https://docs.docker.com/compose/install/"
    #     exit 1
    # fi
    
    # # COMENTADO: Verificar que Docker esté corriendo
    # if ! docker info &> /dev/null; then
    #     print_error "Docker daemon no está corriendo"
    #     echo "Inicia el servicio Docker e intenta nuevamente"
    #     exit 1
    # fi
    
    # Verificar Java (necesario para Spring Boot sin Docker)
    #if ! command -v java &> /dev/null; then
    #    print_error "Java no está instalado"
    #    echo "Instala Java 17 o superior:"
    #    echo "  Ubuntu/Debian: sudo apt install openjdk-17-jdk"
    #    echo "  CentOS/RHEL: sudo yum install java-17-openjdk"
    #    echo "  macOS: brew install openjdk@17"
    #    exit 1
    #fi
    
    # Verificar Maven (para construir el proyecto)
    #if ! command -v mvn &> /dev/null; then
    #    print_error "Maven no está instalado"
    #    echo "Instala Maven:"
    #    echo "  Ubuntu/Debian: sudo apt install maven"
    #    echo "  CentOS/RHEL: sudo yum install maven"
    #    echo "  macOS: brew install maven"
    #    exit 1
    #fi
    
    # Verificar archivos necesarios
    local required_files=("pom.xml")
    for file in "${required_files[@]}"; do
        if [[ ! -e "$PROJECT_ROOT/$file" ]]; then
            print_error "Archivo/directorio requerido no encontrado: $file"
            echo "Asegúrate de que el proyecto Maven esté correctamente estructurado"
            exit 1
        fi
    done
    
    print_success "Requisitos verificados correctamente"
}

load_environment() {
    local env_file="$PROJECT_ROOT/.env"
    
    if [[ -f "$env_file" ]]; then
        print_step "Cargando variables de entorno desde .env"
        # Cargar solo variables seguras, no todas
        # Usamos un método más seguro para cargar variables
        while IFS='=' read -r key value || [ -n "$key" ]; do
            # Ignorar comentarios y líneas vacías
            if [[ -n "$key" && "$key" != \#* ]]; then
                # Eliminar espacios y comillas
                key=$(echo "$key" | xargs)
                value=$(echo "$value" | xargs | sed -e 's/^"//' -e 's/"$//' -e "s/^'//" -e "s/'$//")
                export "$key"="$value"
            fi
        done < "$env_file"
        print_success "Variables de entorno cargadas"
    else
        print_info "Archivo .env no encontrado, usando valores por defecto"
        # Valores por defecto
        export APP_PORT=8080
        export SPRING_PROFILES_ACTIVE=prod
    fi
}

build_project() {
    print_step "Construyendo proyecto con Maven..."
    
    cd "$PROJECT_ROOT"
    
    if mvn clean package -DskipTests; then
        print_success "Proyecto construido correctamente"
    else
        print_error "Error construyendo el proyecto con Maven"
        exit 1
    fi
}

find_jar_file() {
    print_step "Buscando archivo JAR generado..."
    
    cd "$PROJECT_ROOT"
    
    # Buscar el archivo JAR en target
    local jar_file=$(find target -name "*.jar" -type f | head -1)
    
    if [[ -n "$jar_file" && -f "$jar_file" ]]; then
        print_success "JAR encontrado: $jar_file"
        echo "$jar_file"
    else
        print_error "No se encontró archivo JAR en target/"
        echo "Ejecuta primero: mvn clean package"
        exit 1
    fi
}

start_application() {
    local profile="${1:-prod}"
    local jar_file="$2"
    
    print_step "Iniciando aplicación Spring Boot con perfil: $profile"
    
    cd "$PROJECT_ROOT"
    
    # Configurar variables de entorno para la aplicación
    export SPRING_PROFILES_ACTIVE="$profile"
    
    # Determinar puerto
    local port="${APP_PORT:-8080}"
    
    # Verificar si el puerto está en uso
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        print_error "El puerto $port ya está en uso"
        echo "Detén la aplicación que está usando el puerto $port o cambia APP_PORT en .env"
        exit 1
    fi
    
    # Comando para ejecutar la aplicación
    local java_opts="${JAVA_OPTS:--Xmx512m -Xms256m}"
    
    print_info "Ejecutando: java $java_opts -jar $jar_file"
    print_info "Perfil activo: $profile"
    print_info "Puerto: $port"
    
    # Ejecutar en background y capturar PID
    java $java_opts -jar "$jar_file" > "$PROJECT_ROOT/app.log" 2>&1 &
    local app_pid=$!
    
    # Guardar PID en archivo
    echo "$app_pid" > "$PROJECT_ROOT/app.pid"
    
    print_success "Aplicación iniciada con PID: $app_pid"
    print_info "Logs guardados en: $PROJECT_ROOT/app.log"
}

stop_application() {
    print_step "Deteniendo aplicación..."
    
    local pid_file="$PROJECT_ROOT/app.pid"
    
    if [[ -f "$pid_file" ]]; then
        local pid=$(cat "$pid_file")
        
        if kill -0 "$pid" 2>/dev/null; then
            print_info "Deteniendo proceso con PID: $pid"
            kill "$pid"
            
            # Esperar a que termine
            local max_wait=30
            local count=0
            while kill -0 "$pid" 2>/dev/null && [[ $count -lt $max_wait ]]; do
                sleep 1
                count=$((count + 1))
            done
            
            if kill -0 "$pid" 2>/dev/null; then
                print_info "Forzando terminación..."
                kill -9 "$pid"
            fi
            
            rm "$pid_file"
            print_success "Aplicación detenida"
        else
            print_info "El proceso ya no está corriendo"
            rm "$pid_file"
        fi
    else
        print_info "No se encontró archivo PID, intentando detener por puerto..."
        
        local port="${APP_PORT:-8080}"
        local pid_on_port=$(lsof -ti:$port)
        
        if [[ -n "$pid_on_port" ]]; then
            print_info "Deteniendo proceso en puerto $port: $pid_on_port"
            kill $pid_on_port
            print_success "Aplicación detenida"
        else
            print_info "No se encontró aplicación corriendo en puerto $port"
        fi
    fi
}

check_application_health() {
    print_step "Verificando salud de la aplicación..."
    
    local max_attempts=30
    local attempt=1
    local port="${APP_PORT:-8080}"
    
    while [[ $attempt -le $max_attempts ]]; do
        print_info "Intento $attempt/$max_attempts..."
        
        # Verificar aplicación Spring Boot
        if curl -s -f "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
            print_success "✓ Aplicación Spring Boot está saludable"
            return 0
        else
            print_info "Esperando que la aplicación esté lista..."
            sleep 3
        fi
        
        attempt=$((attempt + 1))
    done
    
    print_error "Timeout esperando que la aplicación esté saludable"
    show_logs
    return 1
}

show_logs() {
    local lines="${1:-50}"
    
    print_step "Mostrando últimas $lines líneas de logs..."
    
    local log_file="$PROJECT_ROOT/app.log"
    
    if [[ -f "$log_file" ]]; then
        echo -e "${YELLOW}=== ÚLTIMAS $lines LÍNEAS DE LOGS ===${NC}"
        tail -n "$lines" "$log_file"
        echo -e "${YELLOW}=== FIN DE LOGS ===${NC}"
    else
        print_info "Archivo de logs no encontrado: $log_file"
    fi
}

show_status() {
    print_step "Estado de la aplicación:"
    
    local pid_file="$PROJECT_ROOT/app.pid"
    local port="${APP_PORT:-8080}"
    
    if [[ -f "$pid_file" ]]; then
        local pid=$(cat "$pid_file")
        if kill -0 "$pid" 2>/dev/null; then
            print_success "✓ Aplicación corriendo (PID: $pid)"
        else
            print_error "✗ Aplicación no está corriendo (PID obsoleto)"
            rm "$pid_file"
        fi
    else
        # Verificar por puerto
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
            local pid_on_port=$(lsof -ti:$port)
            print_success "✓ Aplicación corriendo en puerto $port (PID: $pid_on_port)"
        else
            print_info "✗ Aplicación no está corriendo"
        fi
    fi
    
    echo ""
    echo -e "${BLUE}=== URLs DE ACCESO ===${NC}"
    echo "Aplicación:      http://localhost:${APP_PORT:-8080}"
    echo "Swagger UI:      http://localhost:${APP_PORT:-8080}/swagger-ui.html"
    echo "Actuator Health: http://localhost:${APP_PORT:-8080}/actuator/health"
    echo ""
    echo -e "${BLUE}=== COMANDOS ÚTILES ===${NC}"
    echo "Ver logs:        ./run-app.sh logs [líneas]"
    echo "Detener:         ./run-app.sh stop"
    echo "Reiniciar:       ./run-app.sh restart"
    echo "Estado:          ./run-app.sh status"
    echo "Seguir logs:     tail -f app.log"
}

cleanup() {
    print_step "Limpiando archivos temporales..."
    
    # Limpiar archivos temporales de Maven
    if [[ -d "$PROJECT_ROOT/target" ]]; then
        print_info "Limpiando directorio target..."
        mvn clean > /dev/null 2>&1 || true
    fi
    
    # Eliminar archivos de logs y PID si existen
    rm -f "$PROJECT_ROOT/app.pid" "$PROJECT_ROOT/app.log" 2>/dev/null || true
    
    print_success "Limpieza completada"
}

# ============ MENÚ PRINCIPAL ============
case "${1:-start}" in
    (start|up)
        print_header
        check_requirements
        load_environment
        build_project
        jar_file=$(find_jar_file)
        stop_application  # Detener si ya está corriendo
        start_application "${2:-prod}" "$jar_file"
        check_application_health
        show_status
        ;;
        
    (stop|down)
        print_header
        stop_application
        ;;
        
    (restart)
        print_header
        check_requirements
        load_environment
        jar_file=$(find_jar_file)
        stop_application
        start_application "${2:-prod}" "$jar_file"
        check_application_health
        show_status
        ;;
        
    (logs)
        show_logs "${2:-50}"
        ;;
        
    (status|ps)
        print_header
        show_status
        ;;
        
    (build)
        print_header
        check_requirements
        build_project
        ;;
        
    (clean|cleanup)
        print_header
        stop_application
        cleanup
        ;;
        
    (dev)
        print_header
        check_requirements
        load_environment
        build_project
        jar_file=$(find_jar_file)
        stop_application
        start_application "dev" "$jar_file"
        # En desarrollo, mostrar logs en tiempo real
        print_info "Mostrando logs en tiempo real (Ctrl+C para salir)..."
        tail -f "$PROJECT_ROOT/app.log" &
        tail_pid=$!
        # Esperar señal para terminar
        trap "kill $tail_pid 2>/dev/null; exit 0" INT TERM
        check_application_health
        show_status
        wait $tail_pid
        ;;
        
    (prod)
        print_header
        check_requirements
        load_environment
        build_project
        jar_file=$(find_jar_file)
        stop_application
        start_application "prod" "$jar_file"
        check_application_health
        show_status
        ;;
        
    (test)
        print_header
        check_requirements
        load_environment
        print_step "Ejecutando tests..."
        cd "$PROJECT_ROOT"
        if mvn test; then
            print_success "Tests ejecutados correctamente"
        else
            print_error "Tests fallaron"
            exit 1
        fi
        ;;
        
    (help|--help|-h)
        print_header
        echo -e "${GREEN}Uso:${NC} ./run-app.sh [COMANDO] [PERFIL]"
        echo ""
        echo -e "${YELLOW}Comandos disponibles:${NC}"
        echo "  start, up     [dev|prod]     Iniciar aplicación (prod por defecto)"
        echo "  stop, down                   Detener aplicación"
        echo "  restart       [dev|prod]     Reiniciar aplicación"
        echo "  logs          [líneas]       Mostrar logs (50 líneas por defecto)"
        echo "  status, ps                   Mostrar estado de la aplicación"
        echo "  build                        Construir proyecto con Maven"
        echo "  clean, cleanup               Limpiar y detener aplicación"
        echo "  dev                          Iniciar en modo desarrollo (con logs en tiempo real)"
        echo "  prod                         Iniciar en modo producción"
        echo "  test                         Ejecutar tests"
        echo "  help, -h, --help             Mostrar esta ayuda"
        echo ""
        echo -e "${YELLOW}Ejemplos:${NC}"
        echo "  ./run-app.sh                 Iniciar en producción"
        echo "  ./run-app.sh dev             Iniciar en desarrollo"
        echo "  ./run-app.sh logs 100        Ver últimas 100 líneas de logs"
        echo "  ./run-app.sh stop            Detener la aplicación"
        echo "  ./run-app.sh status          Ver estado actual"
        echo ""
        echo -e "${YELLOW}Variables de entorno (.env):${NC}"
        echo "  APP_PORT=8080                Puerto de la aplicación"
        echo "  SPRING_PROFILES_ACTIVE=prod  Perfil Spring activo"
        echo "  JAVA_OPTS=-Xmx512m -Xms256m  Opciones de Java"
        echo "  DATABASE_URL=...             URL de base de datos"
        echo "  DATABASE_USER=...            Usuario de BD"
        echo "  DATABASE_PASSWORD=...        Contraseña de BD"
        ;;
        
    (*)
        print_error "Comando no reconocido: $1"
        echo "Usa: ./run-app.sh help para ver los comandos disponibles"
        exit 1
        ;;
esac