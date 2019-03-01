package sample;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;

/**
 * Created by Dipto on 25/12/2015.
 */
public class Table {
    private String Username;

    public Table(String Username) {
        this.Username = Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getUsername() {
        return Username;
    }

}
