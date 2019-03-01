package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by Dipto on 24/12/2015.
 */
public class Client implements Runnable,Initializable {
    private Socket connectionToServer;
    private ArrayList<Table> tableList;
    private Hashtable<String,String> clienPortlist;
    private ObjectOutputStream outToServer;
    private ObjectInputStream inToServer;
    private String myName;
    private Thread thread;


    @FXML
    private TableView<Table> tableView;
    @FXML
    private TableColumn<Table,String> clientName;
    @FXML
    private TableColumn<Table,Button> chatButton;

    public Client(ArrayList<Table> tableList, Hashtable<String, String> clientPortList, Socket socket, ObjectOutputStream out, ObjectInputStream in,String myName){
        this.tableList=tableList;
        this.clienPortlist=clientPortList;
        this.connectionToServer=socket;
        this.outToServer=out;
        this.inToServer=in;
        this.thread = new Thread(this);
        thread.start();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<Table, String> clientName = new TableColumn<>("ClientName");
        clientName.setCellValueFactory(new PropertyValueFactory("Username"));
        Callback<TableColumn<Table, String>, TableCell<Table, String>> cellFactory =
                new Callback<TableColumn<Table, String>, TableCell<Table, String>>()
                {
                    @Override
                    public TableCell call( final TableColumn<Table, String> param )
                    {
                        final TableCell<Table, String> cell = new TableCell<Table, String>()
                        {

                            final Button btn = new Button( "Chat" );

                            @Override
                            public void updateItem( String item, boolean empty )
                            {
                                super.updateItem( item, empty );
                                if ( empty )
                                {
                                    setGraphic( null );
                                    setText( null );
                                }
                                else
                                {
                                    btn.setOnAction( ( ActionEvent event ) ->
                                    {
                                        Table person = getTableView().getItems().get( getIndex() );
                                        System.out.println(person.getUsername());
                                    } );
                                    setGraphic( btn );
                                    setText( null );
                                }
                            }
                        };
                        return cell;
                    }
                };
        TableColumn chatButton = new TableColumn();
        chatButton.setCellValueFactory( new PropertyValueFactory<>( "DUMMY" ) );


    }

    @Override
    public void run() {

    }
}
