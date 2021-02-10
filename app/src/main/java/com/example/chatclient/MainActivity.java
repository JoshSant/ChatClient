package com.example.chatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Button btSend;
    private EditText etText;
    private TextView tvText;

    private boolean run = true;
    private Socket client;
    private DataInputStream flujoE;
    private DataOutputStream flujoS;
    private Thread listeningThread;
    private String textoClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        btSend = findViewById(R.id.btSend);
        etText = findViewById(R.id.etTexto);
        tvText = findViewById(R.id.tvTexto);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etText.getText().toString();
                new Thread(){
                    @Override
                    public void run() {
                        sendText(text);
                    }
                }.start();
                etText.setText("");
            }
        });
        Thread thread = new Thread(){
            @Override
            public void run() {
                startClient("10.0.2.2", 5000);
            }
        };
        thread.start();
    }



    private void sendText(String text){
        try {
            flujoS.writeUTF(text);
            flujoS.flush();
        } catch (IOException ex) {
            System.out.println("btsend: " + ex.getLocalizedMessage());
            run = false;
        }
    }

    private String text;
    public void startClient(String host, int port){
        try{
            client = new Socket(host, port);
            flujoE = new DataInputStream(client.getInputStream());
            flujoS = new DataOutputStream(client.getOutputStream());
            listeningThread = new Thread() {
                @Override
                public void run() {
                    while(run) {
                        try {
                            if(!text.contains(";")) {
                                Log.v("xyz", flujoE.readUTF());
                                text = flujoE.readUTF();
                                tvText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvText.append(text + "\n");
                                    }
                                });
                            }else{
                                textoClientes = text;
                                System.out.println(textoClientes);
                                jTextArea2.append(textoClientes + "\n");
                                String[] clientes = textoClientes.split(";");
                                for(int i = 0; i<clientes.length; i++){
                                    cbClientes.addItem(clientes[i]);
                                }
                                textoClientes = "";
                            }
                        }catch (IOException e){
                            System.out.println("run: " + e.getLocalizedMessage());
                        }
                    }
                }
            };
            listeningThread.start();
        }catch (IOException e){
            System.out.println("start client: " + e.getMessage());
            finish();
        }
    }
}