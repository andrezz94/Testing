package flowerunit;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;

/**
 *
 * @author Oscar Odelstav, Andre Freberg, Chrstoffer Emilsson
 */
public class FlowerServer implements Serializable {

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
                    int iD, soilMoisture;
                    String request;

                    try {
                        while (!running) {
                            Thread.sleep(1000);
                        }
                        serverSocket = new ServerSocket(port);
                        while (running) {
                            //textField.setText("Accepting incoming connections on " + host + ": " + port);
                            System.out.println("Accepting incoming connections on " + host + ": " + port);
                            socket = serverSocket.accept();

                            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            request = br.readLine();

                            if (request != null) {
                                switch (request) {
                                    case "addValue":
                                        iD = Integer.valueOf(br.readLine());
                                        //textField.setText("User " + iD + " connected");
                                        temp = Float.valueOf(br.readLine());
                                        System.out.println("Temp: " + temp);
                                        humidity = Float.valueOf(br.readLine());
                                        System.out.println("Humidity" + humidity);
                                        soilMoisture = Integer.valueOf(br.readLine());
                                        System.out.println("SoilMoisture: " + soilMoisture);

                                        NodeUnit tempUnit = nodeUnits.get(iD);
                                        tempUnit.setHumidity(humidity);
                                        tempUnit.setTemp(temp);
                                        tempUnit.setSoilMoisture(soilMoisture);
                                        addValueToDB(temp, humidity, iD, soilMoisture);

                                        
                                        //textField.setText("User " + iD + " disconnected");
                                        //sendLastValToUser();
                                        break;
                                    case "getValue":
                                        System.out.println("Client connected to server");
                                        sendLastValToUser();
                                        
                                        break;
                                    case "getHistory":
                                        sendHistoryToUser();
                                        break;
                                    default:
                                        System.out.println("Invalid request");
                                        break;
                                }
                                socket.close();
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
    private void addValueToDB(float temp, float humidity, int iD, int soilMoisture) {
        System.out.println("received value sent to DB");
        sqlCon.addValue(temp, humidity, iD, soilMoisture);
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
    private void sendLastValToUser() throws IOException {
        float temp, humidity;
        int id = 0;
        int soilMoisture;
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        PrintStream pout = new PrintStream(out);
        ObjectOutputStream oOut = new ObjectOutputStream(socket.getOutputStream());
        
        pout.print(nodeUnits.size());

        for (NodeUnit unit : nodeUnits) {
            
            temp = unit.getTemp();
            humidity = unit.getHumidity();
            soilMoisture = unit.getSoilMoisture();
            pout.print(id+"\r\n" + temp+"\r\n" + humidity+"\r\n");
            System.out.println("\nUnit: " + iD + "\nTemp: " + temp + "\nHumidity: " + humidity);
            id++;
            
        }
        System.out.println("most recent value is sent to user");
    }

    // Här ska databasvärden som returneras dirigeras i lämplig stream till användare
    private void sendHistoryToUser() {
        
        ResultSet set = null;
        float temp, humidity;
        int id;
        String date;
        BufferedOutputStream out;
        
        
        try {
            out  = new BufferedOutputStream(socket.getOutputStream());
            PrintStream pout = new PrintStream(out);
            set = sqlCon.getHistory();
            
            while(set.next()){
                date = set.getString(String.valueOf("time_created"));
                id = set.getInt("iD");
                temp = set.getFloat("temp");
                humidity = set.getFloat("humidity");
                
                pout.print(date + "\r\n" + id + "\r\n" + temp + "\r\n" + humidity + "\r\n");
                
            }
            out.close();
        } catch (SQLException ex) {
            Logger.getLogger(FlowerServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FlowerServer.class.getName()).log(Level.SEVERE, null, ex);
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
