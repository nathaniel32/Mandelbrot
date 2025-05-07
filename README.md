# Verteiltes System: Mandelbrot

## 1. Starten des Clients

```bash
javac Client.java
```

```bash
java Client <Client Port>
```

## 2. Starten des Workers

Es können beliebig viele Worker gestartet werden.

```bash
javac Worker.java
```

```bash
java Worker <Client IP> <Client Port>
```

## Hinweise
- Die Anzahl der Client-Threads sollte mindestens so hoch sein wie die Anzahl der gestarteten Worker.
- Das Ergebnis des `Ypixel/workerthread` sollte in Ganzzahlen vorliegen, um eine bessere Arbeitsverteilung und zu gewährleisten.