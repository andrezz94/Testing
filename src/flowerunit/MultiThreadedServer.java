package flowerunit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadedServer extends Thread {

    int serverPort = -1;
    public MultiThreadedServer(int serverPort){
        this.serverPort = serverPort;
        System.out.println(this.serverPort);
        
    }
    
    public void run() {
        ServerSocket serverSocket = null;
        try {
            System.out.println(serverPort);
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + serverPort);
            System.exit(-1);
        }

        Socket clientSocket = null;
        while (true) {
            try {
                System.out.println("Waiting for connect request...");
                clientSocket = serverSocket.accept();

                //If clientSocket accepts a connection
                if (clientSocket.isConnected()) {

                    String clientHost = clientSocket.getInetAddress().getHostAddress();
                    int clientPort = clientSocket.getPort();
                    System.out.println("Connect request is accepted from\n" + "Host address: " + clientHost + "\nClient port: " + clientPort);

                    TCPServer server = new TCPServer(clientSocket);
                    server.start();
                }
            } catch (IOException e) {
                System.out.println("Accept failed @ " + serverPort + "\n" + e);
                System.exit(-1);
                
            }
        }

    }
}
