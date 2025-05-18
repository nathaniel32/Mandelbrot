import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Worker extends UnicastRemoteObject implements WorkerInterface {
    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);
    Worker() throws RemoteException {}

    @Override
    synchronized public int[][] bild_rechnen_worker(int workers_threads, int max_iter, BigDecimal max_betrag, int y_sta, int y_sto, int x_sta, int x_sto, int xpix, int ypix, BigDecimal xmin, BigDecimal xmax, BigDecimal ymin, BigDecimal ymax) throws RemoteException {
        System.out.println("y: " + y_sta + " - " + y_sto + "\t| x: " + x_sta + " - " + x_sto);
        int current_y_length = y_sto - y_sta;
        int current_x_length = x_sto - x_sta;

        int[][] colors = new int[current_x_length][current_y_length];

        Thread[] threads = new Thread[workers_threads];
        int rowsPerThread = (current_y_length) / workers_threads;

        BigDecimal dx = xmax.subtract(xmin, MC).divide(BigDecimal.valueOf(xpix), MC);
        BigDecimal dy = ymax.subtract(ymin, MC).divide(BigDecimal.valueOf(ypix), MC);

        for (int i = 0; i < workers_threads; i++) {
            int y_start = i * rowsPerThread + y_sta;
            int y_end = (i == workers_threads - 1) ? y_sto : y_start + rowsPerThread;
            int current_y_start = i * rowsPerThread;

            threads[i] = new Thread(() -> {
                int current_y_start_index = current_y_start;

                for (int y = y_start; y < y_end; y++) {
                    //c_im = ymin + (ymax - ymin) * y / ypix;
                    BigDecimal c_im = ymin.add(dy.multiply(BigDecimal.valueOf(y), MC), MC);
    
                    for (int x = 0; x < current_x_length; x++) {
                        //c_re = xmin + (xmax - xmin) * (x_sta + x) / xpix;
                        BigDecimal c_re = xmin.add(dx.multiply(BigDecimal.valueOf(x_sta + x), MC), MC);
                        int iter = calc(max_iter, max_betrag, c_re, c_im);
                        colors[x][current_y_start_index] = iter;
                    }
                    current_y_start_index++;
                }
            });

            threads[i].start();
        }

        for (int i = 0; i < workers_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return colors;
    }

    private int calc(int max_iter, BigDecimal max_betrag, BigDecimal cr, BigDecimal ci) {
        int iter = 0;
        BigDecimal zr = BigDecimal.ZERO;
        BigDecimal zi = BigDecimal.ZERO;
        BigDecimal zr2 = BigDecimal.ZERO;
        BigDecimal zi2 = BigDecimal.ZERO;
        BigDecimal zri = BigDecimal.ZERO;
        BigDecimal betrag = BigDecimal.ZERO;
        
        while (iter < max_iter && betrag.compareTo(max_betrag) <= 0) {
            zr = zr2.subtract(zi2, MC).add(cr, MC);
            zi = zri.add(zri, MC).add(ci, MC);
            zr2 = zr.multiply(zr, MC);
            zi2 = zi.multiply(zi, MC);
            zri = zr.multiply(zi, MC);
            betrag = zr2.add(zi2, MC);
            iter++;
        }
        return iter;
    }

    public static void main(String[] args) {
        if (args.length == 2){
            try {
                String masterIP = args[0];
                int masterPort = Integer.parseInt(args[1]);

                MasterInterface master = (MasterInterface) java.rmi.registry.LocateRegistry.getRegistry(masterIP, masterPort).lookup("MasterServer");
                
                Worker worker = new Worker();

                master.worker_anmelden(worker);

                System.out.println("Worker hat eine Verbindung zum Master-Port: " + masterPort + " hergestellt\n\n");
            } catch (Exception e) {
                System.err.println("Worker exception:");
                e.printStackTrace();
            }
        }else{
            System.out.println("Erforderliche Parameter: <Master IP> <Master Port>");
        }
    }
}