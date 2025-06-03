#!/bin/bash

javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java worker/Worker.java
java -cp build Worker --mport 10000 --mserv MandelbrotServer