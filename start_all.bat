javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java public/ClientInterface.java master/*.java
javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java public/ClientInterface.java worker/Worker.java
javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java public/ClientInterface.java client/*.java

start java -cp build Master --mport 10000 --mserv MandelbrotServer --laddr localhost

timeout /t 1 /nobreak >nul

start java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost
start java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost
start java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost
start java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost

start java -cp build Client --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost