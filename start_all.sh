#!/bin/bash

javac -d build public/MasterInterface.java public/WorkerInterface.java master/Master.java
javac -d build public/MasterInterface.java public/WorkerInterface.java worker/Worker.java
javac -d build public/MasterInterface.java public/WorkerInterface.java client/*.java

java -cp build Master --port 10000 --service MandelbrotServer &

sleep 1

java -cp build Worker --ip localhost --port 10000 --service MandelbrotServer &
java -cp build Worker --ip localhost --port 10000 --service MandelbrotServer &
java -cp build Worker --ip localhost --port 10000 --service MandelbrotServer &
java -cp build Worker --ip localhost --port 10000 --service MandelbrotServer &

java -cp build Client --ip localhost --port 10000 --service MandelbrotServer &
