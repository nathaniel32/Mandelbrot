#!/bin/bash

javac -d build public/MasterInterface.java public/WorkerInterface.java master/Master.java
javac -d build public/MasterInterface.java public/WorkerInterface.java worker/Worker.java
javac -d build public/MasterInterface.java public/WorkerInterface.java client/*.java

java -cp build Master --maddr localhost --mport 10000 --mserv MandelbrotServer &

sleep 1

java -cp build Worker --maddr localhost --mport 10000 --mserv MandelbrotServer --waddr localhost &
java -cp build Worker --maddr localhost --mport 10000 --mserv MandelbrotServer --waddr localhost &
java -cp build Worker --maddr localhost --mport 10000 --mserv MandelbrotServer --waddr localhost &
java -cp build Worker --maddr localhost --mport 10000 --mserv MandelbrotServer --waddr localhost &

java -cp build Client --maddr localhost --mport 10000 --mserv MandelbrotServer &
