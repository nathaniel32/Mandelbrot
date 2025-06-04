# Verteiltes System: Mandelbrot

## 1. Master starten

```bash
javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java public/ClientInterface.java master/*.java
java -cp build Master --mport 10000 --mserv MandelbrotServer --laddr localhost
```

```bash
java -cp build Master --mport <Master Port> --mserv <Master Service> --laddr <Master Address>
```

## 2. Worker starten

```bash
javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java public/ClientInterface.java worker/Worker.java
java -cp build Worker --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost
```

```bash
java -cp build Worker --taddr <Master Address> --mport <Master Port> --mserv <Master Service> --laddr <Worker Address>
```

## 3. Starten des Clients

```bash
javac -d build public/NetworkConfig.java public/MasterInterface.java public/WorkerInterface.java public/ClientInterface.java client/*.java
java -cp build Client --taddr localhost --mport 10000 --mserv MandelbrotServer --laddr localhost
```

```bash
java -cp build Client --taddr <Master Address> --mport <Master Port> --mserv <Master Service> --laddr <Master Address>
```