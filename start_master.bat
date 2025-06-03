javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java master/Master.java
java -cp build Master --mport 10000 --mserv MandelbrotServer