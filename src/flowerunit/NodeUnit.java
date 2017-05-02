package flowerunit;

/**
 *
* @author Oscar Odelstav, Andre Freberg, Chrstoffer Emilsson
 */

// enhets klass
public class NodeUnit {
    
    // variablerna är Integers för tillfället, bör senare ändras till flyttal
    private int temp, humidity, soilMoisture;
    private int Id;
    
    // huvudkonstruktor för anslutna enheter
    public NodeUnit(int Id){
        this.Id = Id;
    }
    
    // Överskuggad konstruktor för tempvariabel till client uppdateringar
    public NodeUnit(){
        
    }
    
    public int getTemp() {
        return temp;
    }
    
    public void setTemp(int temp) {
        this.temp = temp;
    }
    
    public int getHumidity() {
        return humidity;
    }
    
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
    
    public int getSoilMoisture() {
        return soilMoisture;
    }
    
    public void setSoilMoisture(int soilMoisture) {
        this.soilMoisture = soilMoisture;
    }
}
