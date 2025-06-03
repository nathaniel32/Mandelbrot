javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java master/Master.java
javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java worker/Worker.java
javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java client/*.java

start java -cp build Master --laddr localhost --mport 10000 --mserv MandelbrotServer

timeout /t 1 /nobreak >nul

start java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost
start java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost
start java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost
start java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost

start java -cp build Client --taddr localhost --mport 10000 --mserv MandelbrotServer