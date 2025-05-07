@echo off
javac Worker.java
javac Client.java

if %errorlevel% neq 0 (
    echo Error!
    pause
    exit /b
)

Start java Client 5000
timeout /t 2 /nobreak  >nul

rem Beispiel 4 Workers
Start java Worker localhost 5000
Start java Worker localhost 5000
Start java Worker localhost 5000
Start java Worker localhost 5000