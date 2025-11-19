@echo off
echo ========================================
echo Payment Orders API - Detener Servicios
echo ========================================
echo.

echo Deteniendo servicios Docker...
docker-compose down

echo.
echo Servicios detenidos correctamente
echo.
pause
