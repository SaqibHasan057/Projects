package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.zone.ZoneOffsetTransitionRule;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    private WebEngine webEngine;
    @FXML
    WebView webView;
    @FXML
    TextField addressField;
    @FXML
    Button searchButton;

    Calendar calendar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load("http://google.com");
        calendar = Calendar.getInstance();
    }

    @FXML
    public void setOnActionSearchButton() throws IOException {
        String address = addressField.getText();
        if(!(address.contains("http://") || address.contains("https://")))address="http://"+address;
        if(!address.contains(".com"))address=address+".com";
        addressField.setText(address);
        FileWriter fileWriter = new FileWriter("src\\sample\\history.txt",true);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        SimpleDateFormat date = new SimpleDateFormat("dd//MM//yyyy HH:mm:ss");
        writer.write(date.format(calendar.getTime())+" ");
        writer.write(address);
        writer.newLine();
        writer.close();
        webEngine.load(address);
    }

}
