package com.example.chatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Button btSend;
    private Button btCerrar;
    private Button btListar;
    private EditText etText;
    private TextView tvText;
    private TextView tvLista;
    private Spinner spinner;

    private boolean run = true;
    private Socket client;
    private DataInputStream flujoE;
    private DataOutputStream flujoS;
    private Thread listeningThread;
    private String textoClientes;
    private Context context;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();

        init();
    }

    private void init(){
        btSend = findViewById(R.id.btSend);
        btCerrar = findViewById(R.id.btCerrar);
        btListar = findViewById(R.id.btListar);
        etText = findViewById(R.id.etTexto);
        tvText = findViewById(R.id.tvTexto);
        tvLista = findViewById(R.id.tvLista);
        spinner = findViewById(R.id.spinner);

        String[] inicio = {"Global"};
        ArrayAdapter adapterInicio = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, inicio);
        spinner.setAdapter(adapterInicio);

        btListar.setEnabled(false);
        btCerrar.setEnabled(false);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = etText.getText().toString();
                String seleccion = spinner.getSelectedItem().toString();
                new Thread(){
                    @Override
                    public void run() {
                        if(!text.isEmpty()) {
                            text = seleccion+";"+text;
                            sendText(text);
                        }
                    }
                }.start();
                etText.setText("");
                btListar.setEnabled(true);
                btCerrar.setEnabled(true);
            }
        });
        btCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        sendText("dhsfgsdhfgshdfgkhdsghsdhsfghfgsdhjg");
                        try {
                            flujoS.close();
                            flujoE.close();
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                    }
                }.start();
            }
        });
        btListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        sendText("cbvhjfbhvbydfyviyvvifbvhbdfhbvfdbvf");
                    }
                }.start();
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

    private String textClient ;
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
                            textClient = flujoE.readUTF();
                            if(!textClient.contains(";")) {
                                Log.v("xyz", flujoE.readUTF());
                                tvText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvText.append(textClient + "\n");
                                    }
                                });
                            }else{
                                textoClientes = textClient;
                                System.out.println(textoClientes);
                                tvLista.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvLista.append(textClient + "\n");
                                    }
                                });
                                String[] clientes = textoClientes.split(";");
                                ArrayAdapter adapter = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_spinner_dropdown_item, clientes);

                                spinner.postOnAnimation(new Runnable() {
                                    @Override
                                    public void run() {
                                        spinner.setAdapter(adapter);
                                    }
                                });
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