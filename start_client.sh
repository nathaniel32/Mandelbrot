#!/bin/bash

javac -d build public/MasterInterface.java public/WorkerInterface.java client/*.java
java -cp build Client --port 10000 --service MandelbrotServer
