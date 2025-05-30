import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Client extends UnicastRemoteObject {

    Client(MasterInterface master) throws RemoteException {
        ClientPresenter p = new ClientPresenter();
        ClientView v = new ClientView(p);
        ClientModel m = new ClientModel(p, master);
        p.init(m, v);
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String masterIP = null;
            int masterPort = -1;
            String masterService = null;
            
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--ip":
                        if (i + 1 < args.length) {
                            masterIP = args[i + 1];
                        }
                        break;
                    case "--port":
                        if (i + 1 < args.length) {
                            masterPort = Integer.parseInt(args[i + 1]);
                        }
                        break;
                    case "--service":
                        if (i + 1 < args.length) {
                            masterService = args[i + 1];
                        }
                        break;
                }
            }

            if (masterIP == null) {
                System.out.print("Master IP: ");
                masterIP = scanner.nextLine();
            }

            if (masterPort == -1) {
                System.out.print("Master Port: ");
                masterPort = Integer.parseInt(scanner.nextLine());
            }

            if (masterService == null) {
                System.out.print("Master Service: ");
                masterService = scanner.nextLine().replace(" ", "");
            }

            scanner.close();

            MasterInterface master = (MasterInterface) java.rmi.registry.LocateRegistry.getRegistry(masterIP, masterPort).lookup(masterService);
            new Client(master);

            System.out.println("\nClient hat Verbindung zum Master hergestellt\nIP\t: " + masterIP + "\nPort\t: " + masterPort + "\nService\t: " + masterService + "\n");
        } catch (Exception e) {
            System.err.println("Fehler beim Starten des Clients");
            e.printStackTrace();
        }
    }
}