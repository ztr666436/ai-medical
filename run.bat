@echo off
echo ==========================================
echo   AI Medical Care - Starting All Services
echo ==========================================
echo.

cd /d D:\workbuddy_pj\ai-medical-care-agents

echo [0/6] Starting Redis (6379)...
start "Redis-6379" cmd /k "cd /d D:\workbuddy_pj\ai-medical-care-agents\tools\redis && redis-server.exe --port 6379"
timeout /t 2 /nobreak >nul
echo        Done.
echo.

echo [1/6] Compiling project...
call mvn compile -q
echo        Build done.
echo.

echo [2/6] Starting Auth (8081)...
start "Auth-8081" cmd /k "cd /d D:\workbuddy_pj\ai-medical-care-agents && mvn spring-boot:run -pl ai-medical-care-auth -am -Dspring-boot.run.skip=false -Dspring-boot.run.arguments=--server.port=8081"
timeout /t 8 /nobreak >nul

echo [3/6] Starting Health Data (8083)...
start "Health-8083" cmd /k "cd /d D:\workbuddy_pj\ai-medical-care-agents && mvn spring-boot:run -pl ai-medical-care-health-data -am -Dspring-boot.run.skip=false -Dspring-boot.run.arguments=--server.port=8083"
timeout /t 6 /nobreak >nul

echo [4/6] Starting Goal (8084)...
start "Goal-8084" cmd /k "cd /d D:\workbuddy_pj\ai-medical-care-agents && mvn spring-boot:run -pl ai-medical-care-goal -am -Dspring-boot.run.skip=false -Dspring-boot.run.arguments=--server.port=8084"
timeout /t 6 /nobreak >nul

echo [5/6] Starting AI Agent (8085)...
start "Agent-8085" cmd /k "cd /d D:\workbuddy_pj\ai-medical-care-agents && mvn spring-boot:run -pl ai-medical-care-ai-agent -am -Dspring-boot.run.skip=false -Dspring-boot.run.arguments=--server.port=8085"
timeout /t 6 /nobreak >nul

echo [6/6] Starting Gateway (8080)...
start "Gateway-8080" cmd /k "cd /d D:\workbuddy_pj\ai-medical-care-agents && mvn spring-boot:run -pl ai-medical-care-gateway -am -Dspring-boot.run.skip=false -Dspring-boot.run.arguments=--server.port=8080"

echo.
echo Waiting for services to start (20 seconds)...
timeout /t 20 /nobreak >nul

echo.
echo Starting frontend dev server...
cd /d D:\workbuddy_pj\ai-medical-care-agents\ai-medical-care-web
start "Frontend-5173" cmd /k "cd /d D:\workbuddy_pj\ai-medical-care-agents\ai-medical-care-web && npm run dev"

echo.
echo ==========================================
echo   Done! Visit: http://localhost:5173
echo   Default login: demo / Demo1234
echo ==========================================
echo.
echo   Close individual windows or run
echo   stop-all.bat to shut down.
echo ==========================================
pause
