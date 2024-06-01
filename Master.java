import java.awt.Color;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Master extends UnicastRemoteObject implements MasterInterface {
    private List<WorkerInterface> worker_list = new ArrayList<>();
    private int indexverteilung_worker = 0;

    Master() throws RemoteException {

    }

    @Override
    public void worker_anmelden(WorkerInterface worker){
        worker_list.add(worker);
    }

    @Override
    public Color[][] bild_rechnen(int max_iter, double max_betrag, int y_sta, int y_sto, int xpix, int ypix, double xmin, double xmax, double ymin, double ymax) throws RemoteException {
        WorkerInterface worker = worker_list.get(indexverteilung_worker);
        indexverteilung_worker = (indexverteilung_worker + 1) % worker_list.size();
        return worker.bild_rechnen_worker(max_iter, max_betrag, y_sta, y_sto, xpix, ypix, xmin, xmax, ymin, ymax);
    }

    public static void main(String[] args) {
        if (args.length == 1){
            try {
                InetAddress localhost = InetAddress.getLocalHost();
                String currentIP = localhost.getHostAddress();
                int masterPort = Integer.parseInt(args[0]);

                String masterUrl = "rmi://" + currentIP + ":" + masterPort + "/MasterServer";

                Master master = new Master();
                LocateRegistry.createRegistry(masterPort);
                Naming.rebind(masterUrl, master);
                //java.rmi.registry.LocateRegistry.createRegistry(port).rebind("MasterServer", master);

                System.out.println("Master ist gestartet...\nURL: " + masterUrl + "\n\n");
            } catch (Exception e) {
                System.err.println("Master exception:");
                e.printStackTrace();
            }
        }else{
            System.out.println("Erforderliche Parameter: <Port>");
        }
    }
}