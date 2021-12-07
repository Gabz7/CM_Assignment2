package com.example.challenge2_m2.topics;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import com.example.challenge2_m2.MainActivity;
import com.example.challenge2_m2.Model.Note;
import com.example.challenge2_m2.Model.Topic;
import com.example.challenge2_m2.MyTaskManager;
import com.example.challenge2_m2.NotepadViewModel;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.ArrayList;

public class MQTTHelper implements MyTaskManager.Callback {
    public MqttAndroidClient mqttAndroidClient;
    private final String server = "tcp://broker.hivemq.com:1883";
    private final String TAG = "I AM HELPER";
    private final String name;
    private final NotepadViewModel viewModel;

    public MQTTHelper(Context context, String name, NotepadViewModel viewModel) {
        this.name = name;
        mqttAndroidClient = new MqttAndroidClient(context, server, name);
        this.viewModel = viewModel;
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    //Adjusting the set of options that govern the behaviour of Offline (or Disconnected) buffering of messages
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    System.out.println("Connection Successful! ID: " + mqttAndroidClient.getClientId());

                    ArrayList<Topic> topics = viewModel.getTopics();
                    for(int i = 0; i < topics.size(); i++){
                        subscribeToTopic(topics.get(i));
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w(TAG, "Failed to connect to: " + server + " " + exception.toString());
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        try {
            mqttAndroidClient.disconnect();
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void subscribeToTopic(Topic topic) {
        try {
            if(!topic.getName().contains("/"))
                topic.setName(topic.getName() + "/");

            mqttAndroidClient.subscribe(topic.getName(), 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w(TAG, "Subscribed to " + topic.getName() + "!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w(TAG, "Subscription to " + topic.getName() + "failed!");
                 }
            });

        } catch (MqttException ex) {
            System.err.println("Exception subscribing");
            ex.printStackTrace();
        }
    }

    public void unsubscribeFromTopic(Topic topic){
        try{
            mqttAndroidClient.unsubscribe(topic.getName(), null, new IMqttActionListener(){

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w(TAG, "Successfully Unsubscribed to " + topic.getName() + "!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w(TAG, "Subscription Removal to " + topic.getName() + "failed!");
                }
            });
        }catch (MqttException e){
            System.err.println("Exception Unsubscribing");
            e.printStackTrace();
        }
    }

    public void messageArrived(String topic, MqttMessage message, MainActivity context){
        AlertDialog newMessage = new AlertDialog.Builder(context)
                .setTitle("Subscribe to Topic")
                .setMessage("A new message from " + topic + " arrived. Would you like to accept it?")
                .setPositiveButton("Accept", (dialog, id) -> {
                    try {
                        JSONObject jsonObj = new JSONObject(message.toString());
                        Note newNote = new Note(jsonObj.get("Name") + " - " + topic, jsonObj.get("Content").toString());
                        viewModel.addNote(newNote, this);
                        dialog.dismiss();
                    }catch (Exception e){ e.printStackTrace();}
                })
                .setNegativeButton("Reject", (dialog, id) -> dialog.dismiss()).create();
        newMessage.show();
        System.out.println("Message Arrived from topic: " + topic);
        System.out.println("Message: " + message);
    }

    public String getName() { return name; }

    public MqttAndroidClient getClient(){ return mqttAndroidClient; }

    @Override
    public void onAddToDB() { System.out.println("Message Added to DB Successfully"); }

    @Override
    public void onLoadFromDB(ArrayList<Note> notes, ArrayList<Topic> topics) {}

    @Override
    public void onLoadToDB() {}

    @Override
    public void onUpdateDB() {}

    @Override
    public void onRemoveFromDB() {}
}
