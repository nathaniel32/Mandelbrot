javac -d build public/MasterInterface.java public/WorkerInterface.java worker/Worker.java
java -cp build Worker --port 10000 --service MandelbrotServer