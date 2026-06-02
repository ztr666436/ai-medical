@echo off
title AI Medical Care - Start All Services
echo ========================================
echo   Starting AI Medical Care Services...
echo ========================================
echo.

set JAVA_OPTS=-Dspring.cloud.nacos.discovery.enabled=false
set BASE=D:\workbuddy_pj\ai-medical-care-agents

echo [0/5] Compiling project...
cd /d %BASE%
call mvn compile -q
echo Build done.
echo.

echo [1/5] Starting auth-service (8081)...
start "Auth-8081" cmd /c "cd /d %BASE% && mvn spring-boot:run -pl ai-medical-care-auth -am -Dspring-boot.run.skip=false %JAVA_OPTS%"
timeout /t 12 /nobreak >nul

echo [2/5] Starting health-data-service (8083)...
start "Health-8083" cmd /c "cd /d %BASE% && mvn spring-boot:run -pl ai-medical-care-health-data -am -Dspring-boot.run.skip=false %JAVA_OPTS%"
timeout /t 10 /nobreak >nul

echo [3/5] Starting goal-service (8084)...
start "Goal-8084" cmd /c "cd /d %BASE% && mvn spring-boot:run -pl ai-medical-care-goal -am -Dspring-boot.run.skip=false %JAVA_OPTS%"
timeout /t 10 /nobreak >nul

echo [4/5] Starting ai-agent-service (8085)...
start "Agent-8085" cmd /c "cd /d %BASE% && mvn spring-boot:run -pl ai-medical-care-ai-agent -am -Dspring-boot.run.skip=false %JAVA_OPTS%"
timeout /t 10 /nobreak >nul

echo [5/5] Starting gateway (8080)...
start "Gateway-8080" cmd /c "cd /d %BASE% && mvn spring-boot:run -pl ai-medical-care-gateway -am -Dspring-boot.run.skip=false %JAVA_OPTS%"
timeout /t 10 /nobreak >nul

echo.
echo ========================================
echo  All services started!
echo  Gateway:    http://localhost:8080
echo  Auth:       http://localhost:8081
echo  HealthData: http://localhost:8083
echo  Goal:       http://localhost:8084
echo  AI Agent:   http://localhost:8085
echo.
echo  Frontend: cd %BASE%\ai-medical-care-web ^&^& npm run dev
echo  Visit: http://localhost:5173
echo ========================================
echo.
echo  Press any key to stop all services...
pause >nul
taskkill /f /fi "WINDOWTITLE eq Auth-8081*" >nul 2>&1
taskkill /f /fi "WINDOWTITLE eq Health-8083*" >nul 2>&1
taskkill /f /fi "WINDOWTITLE eq Goal-8084*" >nul 2>&1
taskkill /f /fi "WINDOWTITLE eq Agent-8085*" >nul 2>&1
taskkill /f /fi "WINDOWTITLE eq Gateway-8080*" >nul 2>&1
echo All services stopped.
