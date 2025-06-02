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
    static String masterAddress = null;
    static int masterPort = -1;
    static String masterService = null;
    private int workerIdIndex = 1;
    private List<WorkerManager> worker_manager_list = new ArrayList<>();

    private class WorkerManager {
        private WorkerInterface worker;
        private String worker_id;
        private int aufgabe = 0;
        private int totalAufgabe = 0;
        private WorkerManager(WorkerInterface worker, String worker_id) {
            this.worker = worker;
            this.worker_id = worker_id;
        }
        private void worker_arbeit_start() {
            aufgabe++;
            totalAufgabe++;
        }
        private void worker_arbeit_end() {
            aufgabe--;
        }
        private WorkerInterface getWorker() {
            return worker;
        }
    }

    private WorkerManager searchWorker() throws RemoteException{
        WorkerManager selected_worker_manager = null;
        for (WorkerManager wm : worker_manager_list) {
            //System.out.println(this_worker_manager.worker + ", " + this_worker_manager.aufgabe);

            if (wm.aufgabe == 0) {
                return wm;
            }

            if (selected_worker_manager == null || wm.aufgabe < selected_worker_manager.aufgabe) {
                selected_worker_manager = wm;
            }
        }

        if (selected_worker_manager == null) {
            System.out.println("ERROR: No valid worker found!");
            throw new RemoteException("No valid worker found.");
        }
        return selected_worker_manager;
    }

    private void printActiveWorkers(){
        System.out.println("\nActive Worker: " + worker_manager_list.size());
        
        /* for (WorkerManager manager : worker_manager_list) {
            System.out.println("ID: " + manager.worker_id);
        } */
    }

    private void removeWorker(WorkerManager worker){
        if (worker_manager_list.remove(worker)) {
            System.out.println("Worker removed: " + worker.worker_id);
        } else {
            System.out.println("Worker not found: " + worker.worker_id);
        }
        printActiveWorkers();
    }

    Master() throws RemoteException {}

    @Override
    public void workerLogout(WorkerInterface worker){
        //worker_manager_list.removeIf(manager -> manager.getWorker().equals(worker));
        
        for (WorkerManager wm : worker_manager_list) {
            if (wm.getWorker().equals(worker)) {
                removeWorker(wm);
                break;
            }
        }
    }

    @Override
    synchronized public String workerLogin(WorkerInterface worker){
        String workerID = "worker_" + workerIdIndex;
        WorkerManager worker_manager = new WorkerManager(worker, workerID);
        worker_manager_list.add(worker_manager);
        workerIdIndex++;
        printActiveWorkers();
        return workerID;
    }

    @Override
    public String[] getSummary(int totalChunks) {
        List<WorkerManager> toRemove = new ArrayList<>();
        String[] result = new String[worker_manager_list.size()];
        int i = 0;
        for (WorkerManager wm : worker_manager_list) {
            double percentage = wm.totalAufgabe == 0 ? 0 : ((double) wm.totalAufgabe / totalChunks) * 100;
            result[i] = String.format("  - %s\t: %d\t(%.2f%%)", wm.worker_id, wm.totalAufgabe, percentage);
            wm.totalAufgabe = 0;
            i++;

            if (wm.aufgabe != 0){
                toRemove.add(wm);
            };
        }
        for (WorkerManager wm : toRemove) {
            System.out.println("\n" + wm.worker_id + " is error!\nActive Task: " + wm.aufgabe);
            removeWorker(wm);
        }
        return result;
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

    private static void getHostIPv4Address() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            System.out.println("\n=> Address:");
            while (interfaces.hasMoreElements()) {
                NetworkInterface netInterface = interfaces.nextElement();

                if (!netInterface.isUp() || netInterface.isLoopback() || netInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress() && !address.isLinkLocalAddress() && !address.isMulticastAddress()) {
                        System.out.println("Interface\t: " + netInterface.getDisplayName());
                        System.out.println("IP\t\t: " + address.getHostAddress());
                        System.out.println("Hostname\t: " + address.getHostName());
                        System.out.println("-------------------------------------");
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
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
            getHostIPv4Address();
            System.out.print("Master IP/Hostname: ");
            masterAddress = scanner.nextLine().replace(" ", "");
        }

        if (masterPort == -1) {
            System.out.print("Port: ");
            masterPort = Integer.parseInt(scanner.nextLine().replace(" ", ""));
        }

        if (masterService == null) {
            System.out.print("Service: ");
            masterService = scanner.nextLine().replace(" ", "");
        }

        scanner.close();
    }

    public static void main(String[] args) {
        try {
            setAddress(args);
            System.setProperty("java.rmi.server.hostname", masterAddress);
            Master master = new Master();
            LocateRegistry.createRegistry(masterPort);

            String masterUrl = "rmi://" + masterAddress + ":" + masterPort + "/" + masterService;
            Naming.rebind(masterUrl, master);

            System.out.println("\n\n=> Master gestartet...\nURL: " + masterUrl + "\nMaster Address\t: " + masterAddress + "\nMaster Port\t: " + masterPort + "\nMaster Service\t: " + masterService + "\n");
        } catch (Exception e) {
            System.err.println("Master exception:");
            e.printStackTrace();
        }
    }
}