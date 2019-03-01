package sample;

import javafx.collections.ObservableArray;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;

/**
 * Created by Dipto on 24/12/2015.
 */
public class Login {
    @FXML
    private TextField Username;
    @FXML
    private TextField Password;
    @FXML
    private Button Enter;

    @FXML
    void login() throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost",800);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        out.writeObject(Username.getText());
        out.writeObject(Password.getText());
        String reply = (String)in.readObject();
        if(reply.equals("YES")){
            ArrayList<Table> tableList = (ArrayList<Table>) in.readObject();
            Hashtable<String,String> clientPortList = (Hashtable<String,String>) in.readObject();
            Client add = new Client(tableList,clientPortList,socket,out,in,Username.getText());
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connecting error!");
            alert.setHeaderText("Your username and password do not match");
            alert.showAndWait();
        }

    }
}
