import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Worker extends UnicastRemoteObject implements WorkerInterface {

    Worker() throws RemoteException {}

    @Override
    synchronized public int[][] bild_rechnen_worker(int workers_threads, int max_iter, double max_betrag, int y_sta, int y_sto, int x_sta, int x_sto, int xpix, int ypix, double xmin, double xmax, double ymin, double ymax) throws RemoteException {
        
        int current_y_length = y_sto - y_sta;
        int current_x_length = x_sto - x_sta;

        int[][] colors = new int[current_x_length][current_y_length];

        Thread[] threads = new Thread[workers_threads];
        int rowsPerThread = (current_y_length) / workers_threads;

        double dx = xmax - xmin;
        double dy = ymax - ymin;

        for (int i = 0; i < workers_threads; i++) {
            int current_y_start = i * rowsPerThread;
            int y_start = current_y_start + y_sta;
            int y_end = (i == workers_threads - 1) ? y_sto : y_start + rowsPerThread;

            threads[i] = new Thread(() -> {
                double c_re, c_im;
                int current_y_start_index = current_y_start;

                for (int y = y_start; y < y_end; y++) {
                    c_im = ymin + dy * y / ypix;
    
                    for (int x = 0; x < current_x_length; x++) {
                        c_re = xmin + dx * (x_sta + x) / xpix;
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

    private int calc(int max_iter, double max_betrag, double cr, double ci) {
        int iter;
        double zr = 0, zi = 0, zr2 = 0, zi2 = 0, zri = 0, betrag = 0;
        for (iter = 0; iter < max_iter && betrag <= max_betrag; iter++) {
            zr = zr2 - zi2 + cr;
            zi = zri + zri + ci;

            zr2 = zr * zr;
            zi2 = zi * zi;
            zri = zr * zi;
            betrag = zr2 + zi2;
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