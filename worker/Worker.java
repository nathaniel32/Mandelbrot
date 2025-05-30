import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Scanner;

public class Worker extends UnicastRemoteObject implements WorkerInterface {

    Worker() throws RemoteException {}

    @Override
    synchronized public int[][] calculateMandelbrotImage_worker(int workersThreads, int maxIterations, double maxBetrag, int yStart, int yStop, int xStart, int xStop, int xpix, int ypix, double xMinimum, double xMaximum, double yMinimum, double yMaximum) throws RemoteException {
        //System.out.println("xMinimum: " + xMinimum);
        //System.out.println("xMaximum: " + xMaximum);
        //System.out.println("yMinimum: " + yMinimum);
        //System.out.println("yMaximum: " + yMaximum);
        
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

    private int calculation(int maxIterations, double maxBetrag, double cr, double ci) {
        int iter = 0;
        double zr = 0, zi = 0, zr2 = 0, zi2 = 0;
        while (iter < maxIterations && (zr2 + zi2) <= maxBetrag) {
            zi = 2 * zr * zi + ci;
            zr = zr2 - zi2 + cr;
            zr2 = zr * zr;
            zi2 = zi * zi;
            iter++;
        }
        return iter;
    }

    private static String getHostIPv4Address() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netInterface = interfaces.nextElement();

                if (!netInterface.isUp() || netInterface.isLoopback() || netInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "localhost";
    }

    public static void main(String[] args) {
        String workerIP = getHostIPv4Address();
        System.setProperty("java.rmi.server.hostname", workerIP);
        System.out.println("Worker Host IP: " + workerIP);
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
            Worker worker = new Worker();

            master.workerLogin(worker);

            System.out.println("=> Worker hat Verbindung zum Master hergestellt\nMaster IP\t: " + masterIP + "\nMaster Port\t: " + masterPort + "\nMaster Service\t: " + masterService + "\n");
            System.out.println("Drücke Strg + C zum Trennen");
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