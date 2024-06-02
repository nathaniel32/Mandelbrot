@echo off
javac Master.java
javac Worker.java
javac Client.java

if %errorlevel% neq 0 (
    echo Error!
    pause
    exit /b
)

Start java Master 5000
timeout /t 1 /nobreak  >nul

rem Beispiel 3 Workers
Start java Worker localhost 5000
Start java Worker localhost 5000
Start java Worker localhost 5000
timeout /t 1 /nobreak  >nul

rem Beispiel 1 Client. "Mehr als 1 Client geht auch."
Start java Client localhost 5000