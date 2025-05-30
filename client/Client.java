import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Client extends UnicastRemoteObject {
    static String masterAddress = null;
    static int masterPort = -1;
    static String masterService = null;

    Client(MasterInterface master) throws RemoteException {
        ClientPresenter p = new ClientPresenter();
        ClientView v = new ClientView(p);
        ClientModel m = new ClientModel(p, master);
        p.init(m, v);
    }

    private static void setAddress(String[] args){
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--maddr":
                    if (i + 1 < args.length) {
                        masterAddress = args[i + 1];
                    }
                    break;
                case "--mport":
                    if (i + 1 < args.length) {
                        masterPort = Integer.parseInt(args[i + 1]);
                    }
                    break;
                case "--mserv":
                    if (i + 1 < args.length) {
                        masterService = args[i + 1];
                    }
                    break;
            }
        }

        if (masterAddress == null) {
            System.out.print("Master IP/Hostname: ");
            masterAddress = scanner.nextLine();
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
    }

    public static void main(String[] args) {
        try {
            setAddress(args);
            MasterInterface master = (MasterInterface) java.rmi.registry.LocateRegistry.getRegistry(masterAddress, masterPort).lookup(masterService);
            new Client(master);

            System.out.println("\n\n=> Client hat Verbindung zum Master hergestellt\nMaster Address\t: " + masterAddress + "\nMaster Port\t: " + masterPort + "\nMaster Service\t: " + masterService + "\n");
        } catch (Exception e) {
            System.err.println("Fehler beim Starten des Clients");
            e.printStackTrace();
        }
    }
}