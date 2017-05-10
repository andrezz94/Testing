package flowerunit;

/**
 *
* @author Oscar Odelstav, Andre Freberg, Chrstoffer Emilsson
 */

// enhets klass
public class NodeUnit {
    
    // variablerna är Integers för tillfället, bör senare ändras till flyttal
    private float temp, humidity;
    private int Id, soilMoisture;
    
    // huvudkonstruktor för anslutna enheter
    public NodeUnit(int Id){
        this.Id = Id;
    }
    
    public float getTemp() {
        return temp;
    }
    
    public void setTemp(float temp) {
        this.temp = temp;
    }
    
    public float getHumidity() {
        return humidity;
    }
    
    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }
    
    public int getSoilMoisture() {
        return soilMoisture;
    }
    
    public void setSoilMoisture(int soilMoisture) {
        this.soilMoisture = soilMoisture;
    }
}
