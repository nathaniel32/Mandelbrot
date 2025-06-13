import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Worker extends UnicastRemoteObject implements WorkerInterface {
    Worker() throws RemoteException {}

    @Override
    synchronized public int[][] calculateMandelbrotImage_worker(int workersThreads, int maxIterations, double maxBetrag, int yStart, int yStop, int xStart, int xStop, int xpix, int ypix, double xMinimum, double xMaximum, double yMinimum, double yMaximum) throws RemoteException {        
        int total_y_length = yStop - yStart;
        int total_x_length = xStop - xStart;

        int[][] colors = new int[total_x_length][total_y_length];
        Thread[] threads = new Thread[workersThreads];
        
        int y_pixs_length = (total_y_length) / workersThreads;

        double dx = xMaximum - xMinimum;
        double dy = yMaximum - yMinimum;

        for (int i = 0; i < workersThreads; i++) {
            int thisYStartIndex = i * y_pixs_length;
            int thisYStart = thisYStartIndex + yStart;
            int thisYEnd = (i == workersThreads - 1) ? yStop : thisYStart + y_pixs_length;

            threads[i] = new Thread(() -> {
                double c_re, c_im;
                int current_yStart_index = thisYStartIndex;

                for (int y = thisYStart; y < thisYEnd; y++) {
                    c_im = yMinimum + dy * y / ypix;
                    for (int x = 0; x < total_x_length; x++) {
                        c_re = xMinimum + dx * (xStart + x) / xpix;
                        int iter = calculation(maxIterations, maxBetrag, c_re, c_im);
                        colors[x][current_yStart_index] = iter;
                    }
                    current_yStart_index++;
                }
            });

            threads[i].start();
        }

        for (int i = 0; i < workersThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return colors;
    }

    //Z_n+1 = Zn^2 + c

    //Zn = a + b * i
    //Zn^2 = a^2 - b^2 + 2ab*i
    //C = re + im * i

    //Zn+1 = a^2 - b^2 + 2ab*i + re + im * i
    //Zn+1 = a^2 - b^2 + re + (2ab + im) * i
    //Zn+1 = a_new + b_new * i
    //a_new = a^2 - b^2 + re
    //b_new = 2ab + im

    private int calculation(int maxIterations, double maxBetrag, double cr, double ci) {
        int iter = 0;
        double zr = 0, zi = 0, zr2 = 0, zi2 = 0;
        while (iter < maxIterations && (zr2 + zi2) <= maxBetrag) {
            zi = 2 * zr * zi + ci; //b_new = 2ab + im
            zr = zr2 - zi2 + cr; //a_new = a^2 - b^2 + re
            zr2 = zr * zr; //a^2
            zi2 = zi * zi; //b^2
            iter++;
        }
        return iter;
    }

    public static void main(String[] args) {
        try {
            NetworkConfig rmiconfig = new NetworkConfig(args);
            
            System.setProperty("java.rmi.server.hostname", rmiconfig.getLocalAddress());

            MasterInterface master = (MasterInterface) java.rmi.registry.LocateRegistry.getRegistry(rmiconfig.getTargetAddress(), rmiconfig.getMasterPort()).lookup(rmiconfig.getMasterService());
            //Worker worker = new Worker();
            WorkerInterface worker = new Worker();

            String worker_id = master.workerLogin(worker);

            System.out.println("\n\n=> Worker hat Verbindung zum Master hergestellt\nMaster Address\t: " + rmiconfig.getTargetAddress() + "\nMaster Port\t: " + rmiconfig.getMasterPort() + "\nMaster Service\t: " + rmiconfig.getMasterService() + "\n");
            System.out.println("Drücke Strg + C zum Trennen");
            System.out.println("ID: " + worker_id);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    master.workerLogout(worker);
                } catch (Exception e) {
                    System.out.println("Das Trennsignal konnte nicht gesendet werden.");
                }
            }));
        } catch (Exception e) {
            System.err.println("Worker exception:");
            e.printStackTrace();
        }
    }
}