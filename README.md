### 1. Starten des Masters

```bash
javac Master.java
```

```bash
java Master <Master Port>
```

### 2. Starten des Workers

Sie können unendlich viele Workers starten.

```bash
javac Worker.java
```

```bash
java Worker <Master IP> <Master Port>
```

### 3. Starten des Clients

Sie können mehr als einen Client starten.

```bash
javac Client.java
```

```bash
java Client <Master IP> <Master Port>
```