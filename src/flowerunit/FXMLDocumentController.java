package flowerunit;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


/**
 *
 * @author Oscar Odelstav, Andre Freberg, Chrstoffer Emilsson
 */
public class FXMLDocumentController implements Initializable {
    
    private FlowerServer server;
    private String hostAdr, portNbr;
    
    @FXML
    private TextField Host, Port;
    
    @FXML
    protected TextArea textArea;
    
    @FXML
    private void handleButtonStart(ActionEvent event) {
        
        try{
            
        hostAdr = Host.getText();
        portNbr = Port.getText();
        
        if(!hostAdr.isEmpty() || !portNbr.isEmpty()){
            server.startServer(hostAdr, Integer.valueOf(portNbr));
        }else{
            server.startServer("127.0.0.1", 8000);
        }
        
        }catch(NullPointerException e){
            e.printStackTrace();
        }       
    }
    
    @FXML
    private void handleButtonStop(ActionEvent event) {
        server.stopServer();
    }
    
    @FXML
    private void handleButtonExit(ActionEvent event){
        System.exit(0);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        server = new FlowerServer("127.0.0.1", 8000);
        
        server.textField.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                textArea.appendText(newValue+"\n");
            }
        } );
    }
    
    
}
