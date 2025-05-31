# Verteiltes System: Mandelbrot

## 1. Master starten

```bash
javac -d build public/MasterInterface.java public/WorkerInterface.java master/Master.java
java -cp build Master --maddr localhost --mport 10000 --mserv MandelbrotServer
```

```bash
java -cp build Master --maddr <Master Address> --mport <Master Port> --mserv <Master Service>
```

## 2. Worker starten

```bash
javac -d build public/MasterInterface.java public/WorkerInterface.java worker/Worker.java
java -cp build Worker --maddr localhost --mport 10000 --mserv MandelbrotServer --waddr localhost
```

```bash
java -cp build Worker --maddr <Master Address> --mport <Master Port> --mserv <Master Service> --waddr <Worker Address>
```

## 3. Starten des Clients

```bash
javac -d build public/MasterInterface.java public/WorkerInterface.java client/*.java
java -cp build Client --maddr localhost --mport 10000 --mserv MandelbrotServer
```

```bash
java -cp build Client --maddr <Master Address> --mport <Master Port> --mserv <Master Service>
```