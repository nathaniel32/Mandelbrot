javac -d build public/MasterInterface.java public/WorkerInterface.java master/Master.java
javac -d build public/MasterInterface.java public/WorkerInterface.java worker/Worker.java
javac -d build public/MasterInterface.java public/WorkerInterface.java client/*.java

start java -cp build Master --maddr localhost --mport 10000 --mserv MandelbrotServer

timeout /t 1 /nobreak >nul

start java -cp build Worker --maddr localhost --mport 10000 --mserv MandelbrotServer --waddr localhost
start java -cp build Worker --maddr localhost --mport 10000 --mserv MandelbrotServer --waddr localhost
start java -cp build Worker --maddr localhost --mport 10000 --mserv MandelbrotServer --waddr localhost
start java -cp build Worker --maddr localhost --mport 10000 --mserv MandelbrotServer --waddr localhost

start java -cp build Client --maddr localhost --mport 10000 --mserv MandelbrotServer