package flowerunit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;

/**
 *
 * @author Oscar Odelstav, Andre Freberg, Chrstoffer Emilsson
 */
public class FlowerServer {

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
    float test;
    int testing;

    public FlowerServer(String host, int port) {
        running = false;
        this.host = host;
        this.port = port;
        textField = new TextField();
        textField.setText("init");
        nodeUnits = new ArrayList<>();
        sqlCon = new SQL();
        addUnit();
        addUnit();
    }

    // Server tråd med socket anslutningar och I/O stream
    private void runServer() {

        (new Thread() {

            @Override
            public void run() throws NullPointerException {

                System.out.println("Server starting...");
                while (running) {

                    float temp, humidity;
                    int iD;
                    String request;

                    try {
                        while (!running) {
                            Thread.sleep(1000);
                        }
                        serverSocket = new ServerSocket(port);
                        while (running) {
                            textField.setText("Accepting incoming connections on " + host + ": " + port);
                            System.out.println("Accepting incoming connections on " + host + ": " + port);
                            socket = serverSocket.accept();

                            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            request = br.readLine();

                            if (request != null) {
                                switch (request) {
                                    case "addValue":
                                        iD = Integer.valueOf(br.readLine());
                                        textField.setText("User " + iD + " connected");
                                        temp = Float.valueOf(br.readLine());
                                        System.out.println(temp);
                                        humidity = Float.valueOf(br.readLine());

                                        NodeUnit tempUnit = nodeUnits.get(iD);
                                        tempUnit.setHumidity(humidity);
                                        tempUnit.setTemp(temp);
                                        addValueToDB(temp, humidity, iD);
                                        
                                        socket.close();
                                        textField.setText("User " + iD + " disconnected");
                                        //sendLastValToUser();
                                        break;
                                    case "getValue":
                                        sendLastValToUser();
                                        break;
                                    case "getHistory":
                                        sendHistoryToUser();
                                        break;
                                    default:
                                        System.out.println("Invalid request");
                                        break;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FlowerServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }

    // Lägga till värden i databasen. db-anslutning ej färdigställd i SQL-klassen
    private void addValueToDB(float temp, float humidity, int iD) {
        System.out.println("received value sent to DB");
        sqlCon.addValue(temp, humidity, iD);
        textField.setText("Values received and stored i DB");
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
        float temp, humidity, soilMoisture;
        
        for (NodeUnit unit : nodeUnits) {
            temp = unit.getTemp();
            humidity = unit.getHumidity();
            System.out.println("\nUnit: " + iD + "\nTemp: " + temp + "\nHumidity: " + humidity);
        }
        System.out.println("most recent value is sent to user");
    }

    // Här ska databasvärden som returneras dirigeras i lämplig stream till användare
    private void sendHistoryToUser() {
        for (NodeUnit unit : nodeUnits) {
            // send to user

            unit.getTemp();
            unit.getHumidity();
        }
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
            textField.setText("Server is already running, stop server before starting");
        } else {
            textField.setText("Server is starting");
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
    protected void addUnit() {
        NodeUnit nodeUnit = new NodeUnit(iD);
        nodeUnits.add(nodeUnit);
        iD++;
    }
}
