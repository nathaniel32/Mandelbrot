# Verteiltes System: Mandelbrot

## 1. Master starten

```bash
javac -d build public/MasterInterface.java public/WorkerInterface.java master/Master.java
java -cp build Master --port 10000 --service MandelbrotServer
```

```bash
java -cp build Master --port <Master Port> --service <Master Service>
```

## 2. Worker starten

```bash
javac -d build public/MasterInterface.java public/WorkerInterface.java worker/Worker.java
java -cp build Worker --ip localhost --port 10000 --service MandelbrotServer
```

```bash
java -cp build Worker --ip <Master IP> --port <Master Port> --service <Master Service>
```

## 3. Starten des Clients

```bash
javac -d build public/MasterInterface.java public/WorkerInterface.java client/*.java
java -cp build Client --ip localhost --port 10000 --service MandelbrotServer
```

```bash
java -cp build Client --ip <Master IP> --port <Master Port> --service <Master Service>
```