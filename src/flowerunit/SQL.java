package flowerunit;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
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
    private ResultSet rs = null;
    private Connection con = null;

    // lägger till ett värde i databasen
    public void addValue(Date date, int temp, int humidity, int iD) {

        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/mysql";
            String uName = "username";
            String passWord = "password";

            Class.forName(driver);
            con = DriverManager.getConnection(url, uName, passWord);
            stmt = con.createStatement();

            //stmt.executeUpdate("Insert * INTO FlowerUnit (date, temp, humidity, iD) VALUE ('"+date+"', '"+temp+"', '"+humidity+"', '"+iD+"')");
            
            // inte testat än men misstänker att executeupdate är för insert och resultset är för select
            
            rs = stmt.executeQuery("Insert * INTO FlowerUnit (date, temp, humidity, iD) VALUE ('" + date + "', '" + temp + "', '" + humidity + "', '" + iD + "')");
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
    protected void getHistory() {

    }

}
