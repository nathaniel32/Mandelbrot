javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java client/*.java
java -cp build Client --mport 10000 --mserv MandelbrotServer
