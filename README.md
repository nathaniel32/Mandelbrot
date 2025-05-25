# Verteiltes System: Mandelbrot

## 1. Starten des Masters

```bash
javac Master.java
```

```bash
java Master <Master Port>
```

## 2. Starten des Workers

Es können beliebig viele Worker gestartet werden.

```bash
javac Worker.java
```

```bash
java Worker <Master IP> <Master Port>
```

## 3. Starten des Clients

Es können beliebig viele Clients gestartet werden.

```bash
javac Client.java
```

```bash
java Client <Master IP> <Master Port>
```