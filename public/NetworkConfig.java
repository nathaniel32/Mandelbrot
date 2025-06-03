import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;

public class NetworkConfig {
    Scanner scanner = new Scanner(System.in);
    String targetAddress = null;
    int masterPort = -1;
    String masterService = null;
    String localAddress = null;
    String[] args;

    private void getHostIPv4Address() {
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

    private void setArgs(){
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--taddr":
                    if (i + 1 < args.length) {
                        targetAddress = args[i + 1];
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
                case "--laddr":
                    if (i + 1 < args.length) {
                        localAddress = args[i + 1];
                    }
                    break;
            }
        }
    }

    public NetworkConfig(String[] args){
        this.args = args;
        setArgs();
    }

    public String getTargetAddress() {
        if (targetAddress == null) {
            System.out.print("Master IP/Hostname: ");
            targetAddress = scanner.nextLine().replace(" ", "");
        }
        return targetAddress;
    }

    public int getMasterPort() {
        if (masterPort == -1) {
            System.out.print("Master Port: ");
            String input = scanner.nextLine().replace(" ", "");
            masterPort = Integer.parseInt(input);
        }
        return masterPort;
    }

    public String getMasterService() {
        if (masterService == null) {
            System.out.print("Master Service: ");
            masterService = scanner.nextLine().replace(" ", "");
        }
        return masterService;
    }

    public String getLocalAddress() {
        if (localAddress == null) {
            getHostIPv4Address();
            System.out.print("Worker IP/Hostname: ");
            localAddress = scanner.nextLine().replace(" ", "");
        }
        return localAddress;
    }
}