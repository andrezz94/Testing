package com.example.oscar.flowerclienttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    private static final int ServerPort = 8000;
    private static final String SERVER_IP = "192.168.1.5";
    private int unitId;
    private float temp, humidity;
    private static final String request = "getValue";
    private String msg;
    Intent intent = new Intent("event");
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("event"));


        //intent.putExtra("message", s);
        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


        Button btnConnect = (Button) findViewById(R.id.connectBtn);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread() {
                    public void run() {
                        int units;

                        try {

                            connect();
                            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
                            PrintStream pout = new PrintStream(out);

                            System.out.println("sending request to server");
                            pout.print("getValue" + "\r\n");
                            pout.flush();
                            System.out.println("request sent");
                            System.out.println("waiting for response");
                            units = Integer.valueOf(br.readLine());
                            System.out.println("units received");

                            String s = "Id    Temp\u00b0    Humidity\n";

                            for (int i = 0; i < units; i++) {
                                unitId = Integer.valueOf(br.readLine());
                                temp = Float.valueOf(br.readLine());
                                humidity = Float.valueOf(br.readLine());


                                s += "\n" + String.valueOf(unitId) + "      " + temp + "      " + humidity + "     \n";
                                System.out.println(s);

                            }
                            intent.putExtra("message", s);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                            br.close();
                            pout.close();
                            disConnect();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            currentThread().interrupt();
                        }
                    }
                }).start();
            }
        });


        Button btnHistory = (Button) findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new Thread() {
                    public void run() {

                        BufferedOutputStream out = null;
                        //TextView textView = (TextView) findViewById(R.id.viewDisplay);
                        try {
                            connect();
                            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            out = new BufferedOutputStream(socket.getOutputStream());
                            PrintStream pout = new PrintStream(out);
                            String s = "Date:             Time:           ID:  C\u00b0:     RH%\n";
                            String st;
                            //int iD, soilMoisture;
                            //float temp, humidity;

                            pout.print("getHistory" + "\r\n");
                            pout.flush();

                            while( (st = br.readLine()) != null){
                                int i = 0;

                                s += st + "   ";
                                i++;

                                if(i == 5){
                                    i=0;
                                    s += "\n\n";
                                }
                            }

                            pout.close();
                            br.close();
                            disConnect();
                            intent.putExtra("message", s);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            currentThread().interrupt();
                        }
                    }
                }).start();
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView textView = (TextView) findViewById(R.id.viewDisplay);
            textView.setMovementMethod(new ScrollingMovementMethod());
            msg = intent.getStringExtra("message");
            textView.setText(msg);
        }
    };

    private void connect() {

        try {
            socket = new Socket(SERVER_IP, 8000);
        } catch (IOException e) {
            throw new RuntimeException("unable to connect", e);
        }

    }

    private void disConnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}









