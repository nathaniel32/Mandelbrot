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

Starte beliebig viele Worker, die sich bei dem Master registrieren. Jeder Worker benötigt die IP-Adresse und den Port des Masters.

```bash
javac -d build public/MasterInterface.java public/WorkerInterface.java worker/Worker.java
java -cp build Worker --ip localhost --port 10000 --service MandelbrotServer
```

```bash
java -cp build Worker --ip <Master IP> --port <Master Port> --service <Master Service>
```

## 3. Starten des Clients

Es können mehrere Clients gestartet werden. Der Client verbindet sich mit dem Master, öffnet die Benutzeroberfläche und startet die Berechnung.

```bash
javac -d build public/MasterInterface.java public/WorkerInterface.java client/*.java
java -cp build Client --ip localhost --port 10000 --service MandelbrotServer
```

```bash
java -cp build Client --ip <Master IP> --port <Master Port> --service <Master Service>
```