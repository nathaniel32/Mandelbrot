@echo off
javac Master.java
javac Worker.java
javac Client.java

Start java Master 5000
timeout /t 1 /nobreak  >nul

Start java Worker localhost 5000
Start java Worker localhost 5000
Start java Worker localhost 5000
Start java Worker localhost 5000

Start java Client localhost 5000