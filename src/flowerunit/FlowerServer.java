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

    // Server tråd med socket anslutningar och I/O stream
    private void runServer() {

        (new Thread() {

            @Override
            public void run() {

                System.out.println("Server starting...");
                
                int temp, humidity;
                int iD;
                String request;

                try {
                    serverSocket = new ServerSocket(port);
                    socket = serverSocket.accept();
                    System.out.println("Accepting incoming connections on " + host + ": " + port);
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    NodeUnit tempUnit = new NodeUnit();

                    while (running) {

                        request = br.readLine();

                        switch (request) {
                            case "addValue":
                                iD = br.read();
                                temp = Integer.parseInt(br.readLine());
                                humidity = Integer.parseInt(br.readLine());
                                
                                // temporär check för att kontrollera om enhet registrerad
                                if((nodeUnits.size())>=iD){
                                tempUnit = nodeUnits.get(iD);
                                tempUnit.setHumidity(humidity);
                                tempUnit.setTemp(temp);
                                addValueToDB(temp, humidity, iD);
                                }else{
                                    System.out.println("Unit not Registered");
                                }
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

    // Lägga till värden i databasen. db-anslutning ej färdigställd i SQL-klassen
    private void addValueToDB(int temp, int humidity, int iD) {
        System.out.println("received value sent to DB");
        sqlCon.addValue(new Date(System.currentTimeMillis()), temp, humidity, iD);
    }

    /* skicka senaste värdena till användare, endast exempelkod
       inparameter ska kompletteras med lämplig stream för att skicka variabler till 
       användargui när client protokoll definierats
       lokala variabler används till detta då användare kan vara konstant uppkopplad
       och servern ej ska behöva belasta systemet/databasen för att skicka endast senaste värdet
    
       get-metoderna ska loopas då samtliga enheters värden ska skickas till användare. 
       en bra metod för detta ska arbetas fram, hur gör man då servern har flera användare?
       hashmap som anger vilka enheter som tillhör respektive användare?
    */ 
    private void sendLastValToUser() {
        int temp, humidity, soilMoisture;
        NodeUnit tempUnit = nodeUnits.get(0);
        temp = tempUnit.getTemp();
        humidity = tempUnit.getHumidity();
        System.out.println("most recent value is sent to user");
    }

    // Här ska databasvärden som returneras dirigeras i lämplig stream till användare
    private void sendHistoryToUser() {
        System.out.println("history of values is sent from DB to user");
    }

    public void setAddress(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // åtkomst till att starta server med servercheck
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

    // stoppa servern
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
