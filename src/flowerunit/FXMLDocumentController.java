/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    
    
    @FXML
    private TextField Host, Port;
    
    @FXML
    protected TextArea textArea;
    
    @FXML
    private void handleButtonStart(ActionEvent event) {
            server.startServer("127.0.0.1", 8000);
    }
    
    @FXML
    private void handleButtonStop(ActionEvent event) {
        server.stopServer();
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
