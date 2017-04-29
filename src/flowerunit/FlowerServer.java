/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowerunit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;

/**
 *
 * @author Oscar Odelstav, Andre Freberg, Chrstoffer Emilsson
 */
public class FlowerServer {

    private String host;
    private int port;
    private boolean running = false;
    private SQL sqlCon = new SQL();

    public FlowerServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void runServer() {

        System.out.println("Server starting...");
        ServerSocket serverSocket;
        String request = null;
        float temp, humidity;
        int iD;

        try {
            while (running) {
                serverSocket = new ServerSocket(port);
                Socket socket = serverSocket.accept();
                System.out.println("Accepting incoming connections on " + host + ": " + port);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {

                    request = br.readLine();

                    switch (request) {
                        case "addValue":
                            iD = br.read();
                            temp = Integer.parseInt(br.readLine());
                            humidity = Integer.parseInt(br.readLine());
                            addValueToDB(temp, humidity, iD);
                            break;
                        case "getValue":
                            sendLastValToUser();
                            break;
                        case "getHistory":
                            sendHistoryToUser();
                            break;
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addValueToDB(float temp, float humidity, int iD) {
        System.out.println("received value sent to DB");
        Date date = new Date(System.currentTimeMillis());
        sqlCon.addValue(date, temp, humidity, iD);
    }

    private void sendLastValToUser() {
        System.out.println("most recent value is sent to user");
    }

    private void sendHistoryToUser() {
        System.out.println("history of values is sent from DB to user");
    }

    public void setAddress(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void startServer(String host, int port) {
        this.host = host;
        this.port = port;

        if (running) {
            System.out.println("Server is already running, stop server before starting");
        } else {
            running = true;
            runServer();
        }
    }

    public void stopServer() {
        running = false;
        Thread.currentThread().interrupt();
    }

}
