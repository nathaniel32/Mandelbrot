import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterInterface extends Remote {
    void worker_anmelden(WorkerInterface worker) throws RemoteException;
    int[][] bild_rechnen(boolean show_layer_line, float farbe_number, int workers_threads, int max_iter, double max_betrag, int y_sta, int y_sto, int xpix, int ypix, double xmin, double xmax, double ymin, double ymax) throws RemoteException;
}