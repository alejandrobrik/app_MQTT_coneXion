package com.example.app_mqtt_conexion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Build;//para obtener el nombre del dispositivo
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
/*    String nombre_Dispositivo;   //string para obtener el nombre del dispositivo
    private TextView tvNombreDispositivo;//TexView para monitorear

    //parametros del broker la siguiente variable con el broker de shiftr.io
    static String MQTTHOST = "tcp://broker.shiftr.io"; //el broker
    static String USERNAME = "accesobroker";//el token de acceso
    static String PASSWORD = "zxcvbnmz";    //la contraceña del token
    MqttAndroidClient client;  //  clienteMQTT este dispositivo*/


    String nombre_Dispositivo;                  //string para obtener el nombre del
    String publicaste;                         //string para mostras el mensaje apublicar
    boolean permiso_publicar=false;          //para permitir o no hacer publicaciones
    boolean intento_publicar=false;           //para saber si intento publicar
    private TextView tvNombreDispositivo;      //TexView para monitorear

    //parametros del broker la siguiente variable con el broker de shiftr.io
/*    static String MQTTHOST = "tcp://broker.shiftr.io"; //el broker
    static String USERNAME = "bristlescar733";          //el token de acceso
    static String PASSWORD = "";             //la contraceña del token*/

    static String MQTTHOST = "tcp://bristlescar733.cloud.shiftr.io"; // La dirección del broker
    static String USERNAME = "bristlescar733";          // El nombre de usuario
    static String PASSWORD = "x7FkiRfnIDcph8Dg";       // La contraseña


    MqttAndroidClient client;              //  clienteMQTT este dispositivo
    MqttConnectOptions options;            // para meter parametros a la conexion


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       //obtenemos el nombre del Dispositivo
       obtener_nombre_Dispositivo();
       //para conextar al broker   //generamos un clienteMQTT
       String clientId = nombre_Dispositivo;//MqttClient.generateClientId();//noombre del celular
       client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
       //para agregar los parametros
       options = new MqttConnectOptions();
       options.setUserName(USERNAME);
       options.setPassword(PASSWORD.toCharArray());
       checar_conexion();//revisamos la conexion

/*        //obtenemos el nombre del Dispositivo
        obtener_nombre_Dispositivo();

        conexionBroker();///Conexion.//para conextar al broker*/
    }
    private void obtener_nombre_Dispositivo() {
        String fabricante = Build.MANUFACTURER;
        String modelo = Build.MODEL;
        nombre_Dispositivo=fabricante+" "+modelo;
        tvNombreDispositivo =(TextView) findViewById(R.id.tv_g);//para mostrar el modelo del celular
        tvNombreDispositivo.setText(nombre_Dispositivo);//para mostrar en el tv_g e modelo del celular

    }

/*    public void conexionBroker() {

        //para conextar al broker   //generamos un clienteMQTT
        String clientId = nombre_Dispositivo; //MqttClient.generateClientId();//noombre del celular
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        //para agregar los parametros
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        try {
            IMqttToken token = client.connect(options);//intenta la conexion
            token.setActionCallback(new IMqttActionListener() {

                @Override//metodo de conectado con exito
                public void onSuccess(IMqttToken asyncActionToken) {
                    // mensaje de conectado
                    Toast.makeText(getBaseContext(), "Conectado ", Toast.LENGTH_SHORT).show();
                }

                @Override//si falla la conexion
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // mensaje de que no se conecto
                    Toast.makeText(getBaseContext(), "NO Conectado ", Toast.LENGTH_SHORT).show();
                }


            });


        } catch (MqttException e) {
            e.printStackTrace();
        }

    }*/


    public void conexionBroker() {

        try {
///sIFALLA  EL PROBLEMA RADICA EN CORRER EL CLIENT SIN OPTIONS Y LUEGO PONEMOS LAS OPTIONS
            IMqttToken token = client.connect(options);//
            token.setActionCallback(new IMqttActionListener() {

                @Override//metodo de conectado con exito
                public void onSuccess(IMqttToken asyncActionToken) {
                    // mensaje de conectado
                    //si no intento publicar Digo conectado
                    if(intento_publicar==false){Toast.makeText(getBaseContext(), "Conectado ", Toast.LENGTH_SHORT).show();}
                    //pero si intento publicar digo
                    else{ Toast.makeText(getBaseContext(), "Conectado, intentar nuevamente ", Toast.LENGTH_SHORT).show();}

                }

                @Override//si falla la conexion
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // mensaje de que no se conecto
                    Toast.makeText(getBaseContext(), "NO Conectado ", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public  void checar_conexion(){
//si el cliente esta desconectado se conecta falso=no conectado
        if(client.isConnected()==false) {
            permiso_publicar=false;// no tienes permiso para publiar
            conexionBroker();//intenta conectarce

        }else{permiso_publicar=true;}//si puedes publicar


    }
    public  void  publicarD1 (View v) {
        String tema="LED";///corrrespode al tema de LED
        String  menssage="encender";
        publicaste= "publicaras " + tema + " " + menssage; //concatenamos el dato a publicar
        intento_publicar=true;//si intento publicar
        checar_conexion();//revisamos la conexion

        if (permiso_publicar==true){

            try {
                int qos=0;//indica la prioridad del mensaje.
                // 0:envio una vez,
                // 1:se envia hasta garantizar la entrega en caso de fallo resive duplicados
                //2: se  garantiza que se entrege al subcribtor unicamente una vez
                //retenid=false;//true es que el mensaje se quede guardado en el broker asta su actualizacion
                client.publish(tema, menssage.getBytes(),qos, false);
                Toast.makeText(getBaseContext(), publicaste, Toast.LENGTH_SHORT).show();
            }catch (Exception e){e.printStackTrace();}
        }

    }


}