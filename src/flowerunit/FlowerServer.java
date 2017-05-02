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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;

/**
 *
 * @author Oscar Odelstav, Andre Freberg, Chrstoffer Emilsson
 */
public class FlowerServer{

    private String host;
    private int port;
    private boolean running;
    private SQL sqlCon;
    protected TextField textField;
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader br;
    private ArrayList<NodeUnit> nodeUnits;
    private static int iD = 0;

    public FlowerServer(String host, int port) {
        running = false;
        this.host = host;
        this.port = port;
        textField = new TextField();
        textField.setText("init");
        nodeUnits = new ArrayList<>();
        sqlCon  = new SQL();
        addUnit();
    }

    private void runServer() {

        (new Thread() {

            @Override
            public void run() {

                System.out.println("Server starting...");
                
                int temp, humidity;
                int iD;

                try {
                    serverSocket = new ServerSocket(port);
                    socket = serverSocket.accept();
                    System.out.println("Accepting incoming connections on " + host + ": " + port);
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    NodeUnit tempUnit = new NodeUnit();

                    while (running) {

                        String request = br.readLine();

                        switch (request) {
                            case "addValue":
                                iD = br.read();
                                temp = Integer.parseInt(br.readLine());
                                humidity = Integer.parseInt(br.readLine());
                                tempUnit = nodeUnits.get(iD);
                                tempUnit.setHumidity(humidity);
                                tempUnit.setTemp(temp);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    protected void startServer(String host, int port) {
        this.host = host;
        this.port = port;

        if (running) {
            System.out.println("Server is already running, stop server before starting");
            textField.setText("\nServer is already running, stop server before starting");
        } else {
            textField.setText("\nServer is starting");
            running = true;
            runServer();
        }
    }

    protected void stopServer() {
        textField.setText("\nServer is stopping");
        running = false;
        
        System.out.println("Stopping server");
        try {
            serverSocket.close();
            Thread.currentThread().interrupt();
        } catch (IOException ex) {
            Logger.getLogger(FlowerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // metod för att lägga till fler enehter
    // För tillfället skapas enheter manuellt, tanken är att ha en knapp i gui för att lägga till enheter
    protected void addUnit(){
        NodeUnit nodeUnit = new NodeUnit(iD);
        nodeUnits.add(nodeUnit);
        iD++;
    }
}
