@echo off
chcp 65001 >nul
echo ==========================================
echo   AI Medical Care - Shutting Down All
echo ==========================================
echo.

setlocal enabledelayedexpansion

echo [5173] Stopping Frontend (Vite) ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5173 ^| findstr LISTENING') do set PID=%%a
if defined PID (taskkill /F /PID !PID! >nul 2>&1 & echo       Done.) else (echo       Not running.)
set PID=

echo [8080] Stopping Gateway ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do set PID=%%a
if defined PID (taskkill /F /PID !PID! >nul 2>&1 & echo       Done.) else (echo       Not running.)
set PID=

echo [8081] Stopping Auth ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081 ^| findstr LISTENING') do set PID=%%a
if defined PID (taskkill /F /PID !PID! >nul 2>&1 & echo       Done.) else (echo       Not running.)
set PID=

echo [8083] Stopping Health Data ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8083 ^| findstr LISTENING') do set PID=%%a
if defined PID (taskkill /F /PID !PID! >nul 2>&1 & echo       Done.) else (echo       Not running.)
set PID=

echo [8084] Stopping Goal ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8084 ^| findstr LISTENING') do set PID=%%a
if defined PID (taskkill /F /PID !PID! >nul 2>&1 & echo       Done.) else (echo       Not running.)
set PID=

echo [8085] Stopping AI Agent ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8085 ^| findstr LISTENING') do set PID=%%a
if defined PID (taskkill /F /PID !PID! >nul 2>&1 & echo       Done.) else (echo       Not running.)
set PID=

echo [6379] Stopping Redis ...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :6379 ^| findstr LISTENING') do set PID=%%a
if defined PID (taskkill /F /PID !PID! >nul 2>&1 & echo       Done.) else (echo       Not running.)

echo.
echo ==========================================
echo   All services stopped.
echo ==========================================
pause
