@echo off
echo ========================================
echo Payment Orders API - Inicio Rapido
echo ========================================
echo.
echo Selecciona el modo de inicio:
echo [1] Docker Compose (PostgreSQL + Aplicacion)
echo [2] Solo PostgreSQL (ejecutar app desde IDE)
echo.
set /p choice="Ingresa tu opcion (1 o 2): "

if "%choice%"=="1" goto docker_full
if "%choice%"=="2" goto docker_db_only

:docker_full
echo.
echo [1/2] Construyendo y levantando todos los servicios...
docker-compose up --build -d
if %errorlevel% neq 0 (
    echo ERROR: No se pudieron iniciar los servicios
    echo Verifica que Docker este corriendo
    pause
    exit /b 1
)
echo.
echo ========================================
echo Servicios iniciados correctamente!
echo ========================================
echo.
echo - API: http://localhost:8080
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo - PgAdmin: http://localhost:5050
echo - PostgreSQL: localhost:5432
echo.
echo Ver logs: docker-compose logs -f payment-orders-app
echo.
pause
exit /b 0

:docker_db_only
echo.
echo [1/3] Iniciando PostgreSQL con Docker...
docker-compose up -d postgres
if %errorlevel% neq 0 (
    echo ERROR: No se pudo iniciar PostgreSQL
    echo Verifica que Docker este corriendo
    pause
    exit /b 1
)

echo.
echo [2/3] Esperando a que PostgreSQL este listo...
timeout /t 10 /nobreak > nul

echo.
echo [3/3] Iniciando aplicacion Spring Boot...
echo.
call mvnw.cmd spring-boot:run

pause
