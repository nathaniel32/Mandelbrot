import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class Master extends UnicastRemoteObject implements MasterInterface {
    private List<WorkerManager> worker_manager_list = new ArrayList<>();

    private class WorkerManager {
        public WorkerInterface worker;
        public int aufgabe = 0;
        public WorkerManager(WorkerInterface worker) {
            this.worker = worker;
        }
        public void worker_arbeit_start() {
            aufgabe++;
        }
        public void worker_arbeit_end() {
            aufgabe--;
        }
        public WorkerInterface getWorker() {
            return worker;
        }
    }

    private WorkerManager searchWorker() throws RemoteException{
        WorkerManager selected_worker_manager = null;
        for (WorkerManager this_worker_manager : worker_manager_list) {
            //System.out.println(this_worker_manager.worker + ", " + this_worker_manager.aufgabe);
            if (this_worker_manager.aufgabe == 0) {
                return this_worker_manager;
            }else if (selected_worker_manager == null) {
                selected_worker_manager = this_worker_manager;
            }else if (this_worker_manager.aufgabe < selected_worker_manager.aufgabe) {
                selected_worker_manager = this_worker_manager;
            }
        }
        return selected_worker_manager;
    }

    Master() throws RemoteException {}

    @Override
    public void workerLogout(WorkerInterface worker){
        worker_manager_list.removeIf(manager -> manager.getWorker().equals(worker));
        System.out.println("Active Worker: " + worker_manager_list.size());
    }

    @Override
    public void workerLogin(WorkerInterface worker){
        WorkerManager worker_manager = new WorkerManager(worker);
        worker_manager_list.add(worker_manager);
        System.out.println("Active Worker: " + worker_manager_list.size());
    }

    @Override
    public int[][] calculateMandelbrotImage(int workersThreads, int maxIterations, double maxBetrag, int yStart, int yStop, int xStart, int xStop, int xpix, int ypix, double xMinimum, double xMaximum, double yMinimum, double yMaximum) throws RemoteException {
        WorkerManager worker_manager;
        synchronized (this) {
            worker_manager = searchWorker();
            worker_manager.worker_arbeit_start();
        }
        int[][] colors = worker_manager.worker.calculateMandelbrotImage_worker(workersThreads, maxIterations, maxBetrag, yStart, yStop, xStart, xStop, xpix, ypix, xMinimum, xMaximum, yMinimum, yMaximum);
        worker_manager.worker_arbeit_end();
        return colors;
    }

    public static String getHostIPv4Address() {
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
        String masterIP = getHostIPv4Address();
        System.setProperty("java.rmi.server.hostname", masterIP);
        try {
            Scanner scanner = new Scanner(System.in);
            int masterPort = -1;
            String masterService = null;
            
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
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

            if (masterPort == -1) {
                System.out.print("Port: ");
                masterPort = Integer.parseInt(scanner.nextLine());
            }

            if (masterService == null) {
                System.out.print("Service: ");
                masterService = scanner.nextLine().replace(" ", "");
            }

            scanner.close();
            
            Master master = new Master();
            LocateRegistry.createRegistry(masterPort);

            String masterUrl = "rmi://" + masterIP + ":" + masterPort + "/" + masterService;
            Naming.rebind(masterUrl, master);

            System.out.println("=> Master gestartet...\nURL: " + masterUrl + "\nIP\t: " + masterIP + "\nPort\t: " + masterPort + "\nService\t: " + masterService + "\n");
        } catch (Exception e) {
            System.err.println("Master exception:");
            e.printStackTrace();
        }
    }
}