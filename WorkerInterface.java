import java.rmi.Remote;
import java.rmi.RemoteException;
import java.math.BigDecimal;

public interface WorkerInterface extends Remote {
    int[][] bild_rechnen_worker(int workers_threads, int max_iter, BigDecimal max_betrag, int y_sta, int y_sto, int x_sta, int x_sto, int xpix, int ypix, BigDecimal xmin, BigDecimal xmax, BigDecimal ymin, BigDecimal ymax) throws RemoteException;
}