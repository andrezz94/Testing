package flowerunit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Oscar Odelstav, Andre Freberg, Chrstoffer Emilsson
 */
public class SQL {

    private Statement stmt = null;
    private Connection con = null;

    // lägger till ett värde i databasen
    public void addValue(float temp, float humidity, int iD, int soilMoisture) {
        
        if(soilMoisture < 0){
            soilMoisture = 0;
        }else if (soilMoisture > 100){
            soilMoisture = 100;
        }
        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/flowerunits";
            String uName = "Test";
            String passWord = "testUnit";
            String insertString = "Insert INTO FlowerUnit (iD, temp, humidity, soilMoisture, time_created) "
                    + "VALUES ('" +iD + "', '" + temp + "', '" + humidity + "', '" + soilMoisture + "', now())";

            Class.forName(driver);
            con = DriverManager.getConnection(url, uName, passWord);
            PreparedStatement statement = con.prepareStatement(insertString);
            statement.execute();
            con.close();

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // här är det tänkt att returnera alla värden som finns i databasen för en enhet i en lista av något slag
    protected ResultSet getHistory() throws SQLException {
        //rs = stmt.executeQuery("Insert * INTO FlowerUnit (date, temp, humidity, iD) VALUE ('" + date + "', '" + temp + "', '" + humidity + "', '" + iD + "')");
        
        String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/flowerunits";
            String uName = "Test";
            String passWord = "testUnit";
            String insertString = "Select date, id, temp, humidity from " + 
                    "flowerunits .flowerunit";
            ResultSet rs = null;
            PreparedStatement statement = null;

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, uName, passWord);
            
            statement = con.prepareStatement(insertString);
            rs = statement.getResultSet();
            con.close();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(statement !=null){
                statement.close();
            }
        }
        
    return rs;
    }

}
