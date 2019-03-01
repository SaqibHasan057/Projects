package com.example.dipto.posturecorrector;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.LogRecord;

public class FixandAlert extends AppCompatActivity {

    Button btnThreshold;
    TextView alert;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ConnectBT connectBT = null;
    Handler alertHandler = new Handler();
    Thread alertThread;
    boolean buttonActive = true;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixand_alert);

        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);

        connectBT = new ConnectBT();
        connectBT.execute();

//call the widgtes
        btnThreshold = (Button)findViewById(R.id.button2);
        alert = (TextView)findViewById(R.id.textView);


        btnThreshold.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                threshold();      //method to turn on
                startInputReading();
                btnThreshold.setEnabled(false);
            }
        });

        alertHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int)msg.arg1;
                int end = (int)msg.arg2;
                String writeMessage="";
                switch(msg.what) {
                    case 1:
                        writeMessage = new String(writeBuf);
                        writeMessage = writeMessage.substring(begin, end);
                        break;
                }

                Toast.makeText(getApplicationContext(),writeMessage,Toast.LENGTH_LONG).show();
                if(writeMessage.equals("alert")){
                    AlertDialog alertDialog = new AlertDialog.Builder(FixandAlert.this).create();
                    alertDialog.setTitle("Alert:");
                    alertDialog.setMessage("PLEASE FIX YOUR BACK!!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    alert.setText("Last alert on "+dateFormat.format(date));
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(FixandAlert.this).create();
                    alertDialog.setTitle("Your threshold value(out of 1023):");
                    alertDialog.setMessage(writeMessage);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

            }



        };

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(FixandAlert.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    private void threshold()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("T".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void startInputReading(){
        alertThread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream bluetoothInputStream=null;
                try {
                    bluetoothInputStream = btSocket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                byte[] buffer = new byte[1024];
                int begin = 0;
                int bytes = 0;
                while (true) {
                    try {
                        bytes += bluetoothInputStream.read(buffer, bytes, buffer.length - bytes);
                        for(int i = begin; i < bytes; i++) {
                            if(buffer[i] == "#".getBytes()[0]) {
                                alertHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                                begin = i + 1;
                                if(i == bytes - 1) {
                                    bytes = 0;
                                    begin = 0;
                                }
                            }
                        }
                    } catch (IOException e) {
                        break;
                    }
                }

            }
        });

        alertThread.start();

    }


}
