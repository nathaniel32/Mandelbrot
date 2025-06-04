#!/bin/bash

javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java public/ClientInterface.java master/*.java
javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java public/ClientInterface.java worker/Worker.java
javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java public/ClientInterface.java client/*.java

java -cp build Master --laddr localhost --mport 10000 --mserv MandelbrotServer &

sleep 1

java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost &
java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost &
java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost &
java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost &

java -cp build Client --taddr localhost --mport 10000 --mserv MandelbrotServer &
