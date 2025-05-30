javac -d build public/MasterInterface.java public/WorkerInterface.java master/Master.java
java -cp build Master --port 10000 --service MandelbrotServer