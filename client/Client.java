import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject {
    Client(MasterInterface master) throws RemoteException {
        ClientPresenter p = new ClientPresenter();
        ClientView v = new ClientView(p);
        ClientModel m = new ClientModel(p, master);
        p.init(m, v);
    }

    public static void main(String[] args) {
        try {
            NetworkConfig rmiconfig = new NetworkConfig(args);
            MasterInterface master = (MasterInterface) java.rmi.registry.LocateRegistry.getRegistry(rmiconfig.getTargetAddress(), rmiconfig.getMasterPort()).lookup(rmiconfig.getMasterService());
            new Client(master);

            System.out.println("\n\n=> Client hat Verbindung zum Master hergestellt\nMaster Address\t: " + rmiconfig.getTargetAddress() + "\nMaster Port\t: " + rmiconfig.getMasterPort() + "\nMaster Service\t: " + rmiconfig.getMasterService() + "\n");
        } catch (Exception e) {
            System.err.println("Fehler beim Starten des Clients");
            e.printStackTrace();
        }
    }
}