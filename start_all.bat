javac -d build public/MasterInterface.java public/WorkerInterface.java master/Master.java
javac -d build public/MasterInterface.java public/WorkerInterface.java worker/Worker.java
javac -d build public/MasterInterface.java public/WorkerInterface.java client/*.java

start java -cp build Master --port 10000 --service MandelbrotServer

timeout /t 1 /nobreak >nul

start java -cp build Worker --ip localhost --port 10000 --service MandelbrotServer
start java -cp build Worker --ip localhost --port 10000 --service MandelbrotServer
start java -cp build Worker --ip localhost --port 10000 --service MandelbrotServer
start java -cp build Worker --ip localhost --port 10000 --service MandelbrotServer

start java -cp build Client --ip localhost --port 10000 --service MandelbrotServer