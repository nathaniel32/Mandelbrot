#!/bin/bash

javac -d build public/MasterInterface.java public/WorkerInterface.java master/Master.java
java -cp build Master --mport 10000 --mserv MandelbrotServer