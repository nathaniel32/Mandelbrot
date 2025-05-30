#!/bin/bash

javac -d build public/MasterInterface.java public/WorkerInterface.java client/*.java
java -cp build Client --mport 10000 --mserv MandelbrotServer
