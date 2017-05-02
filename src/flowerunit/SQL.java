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
    
    public void addValue(Date date, float temp, float humidity, int iD){
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "username", "password");
            stmt = con.createStatement();
            rs = stmt.executeQuery("Insert * INTO FlowerUnit " + "Values(date, temp, humidity, iD)");
            
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
