package com.example.challenge2_m2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import com.example.challenge2_m2.DB.MyDBHelper;
import com.example.challenge2_m2.Model.Note;
import com.example.challenge2_m2.Model.Topic;
import com.example.challenge2_m2.topics.MQTTHelper;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyTaskManager {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final String ADD = "Add";
    private static final String UPDATE_NAME = "UpdateName";
    private static final String UPDATE_CONTENT = "UpdateContent";
    private static final String REMOVE = "Remove";
    private static final String GET_ALL = "GetAll";
    private SQLiteDatabase db;
    private final MyDBHelper helper;
    private final MQTTHelper mqttHelper;
    private MqttCallbackExtended callbackExtended;
    private final MainActivity context;

    public interface Callback {
        void onAddToDB();
        void onLoadFromDB(ArrayList<Note> notes, ArrayList<Topic> topics);
        void onLoadToDB();
        void onUpdateDB();
        void onRemoveFromDB();
    }

    public MyTaskManager(MyDBHelper helper, MQTTHelper mqttHelper, MainActivity context){
        this.helper = helper;
        this.mqttHelper = mqttHelper;
        this.context = context;
    }

    public void executeTask(String task, Note note, Callback callback){
        executor.execute(() -> {
            ContentValues contentValues = new ContentValues();
            String updateQuery = " ID = ?";

            switch (task){
                case ADD: //adds a new note to the Database
                    db = helper.getWritableDatabase();

                    contentValues.put(MyDBHelper.KEY_ID, note.getID());
                    contentValues.put(MyDBHelper.KEY_NAME, note.getName());
                    contentValues.put(MyDBHelper.KEY_CONTENT, note.getContent());

                    db.insert(MyDBHelper.NOTES_TABLE, null, contentValues);
                    handler.post(callback::onAddToDB);
                    break;
                case GET_ALL: //loads all notes in DB to the ViewModel
                    db = helper.getReadableDatabase();
                    ArrayList<Note> notes = new ArrayList<>();
                    ArrayList<Topic> topics = new ArrayList<>();
                    Cursor cre = db.rawQuery("SELECT * FROM " + MyDBHelper.NOTES_TABLE, null);

                    if(cre.moveToFirst()) {
                        do{
                            int id = cre.getInt(cre.getColumnIndexOrThrow(MyDBHelper.KEY_ID));
                            String name = cre.getString(cre.getColumnIndexOrThrow(MyDBHelper.KEY_NAME));
                            String content = cre.getString(cre.getColumnIndexOrThrow(MyDBHelper.KEY_CONTENT));

                            Note n = new Note(name, content, id);
                            System.out.println(n);
                            notes.add(n);
                        }while (cre.moveToNext());
                    }
                    cre.close();

                    cre = db.rawQuery("SELECT * FROM " + MyDBHelper.TOPICS_TABLE, null);

                    if(cre.moveToFirst()) {
                        do{
                            int id = cre.getInt(cre.getColumnIndexOrThrow(MyDBHelper.KEY_ID));
                            String name = cre.getString(cre.getColumnIndexOrThrow(MyDBHelper.KEY_NAME));
                            Topic t = new Topic(name, id);
                            System.out.println(t);
                            topics.add(t);
                        }while (cre.moveToNext());
                    }
                    cre.close();
                    handler.post(() -> callback.onLoadFromDB(notes, topics));
                    break;
                case UPDATE_NAME: //updates the name belonging to note passed in the arguments
                    db = helper.getWritableDatabase();
                    contentValues.put(MyDBHelper.KEY_NAME, note.getName());

                    db.update(MyDBHelper.NOTES_TABLE, contentValues, updateQuery,
                            new String[]{String.valueOf(note.getID())});
                    handler.post(callback::onUpdateDB);
                    break;
                case UPDATE_CONTENT: //updates the content belonging to note in the arguments
                    db = helper.getWritableDatabase();
                    contentValues.put(MyDBHelper.KEY_CONTENT, note.getContent());

                    db.update(MyDBHelper.NOTES_TABLE, contentValues, updateQuery,
                            new String[]{String.valueOf(note.getID())});
                    handler.post(callback::onUpdateDB);
                    break;
                case REMOVE: //removes a specific note from the database
                    db = helper.getWritableDatabase();
                    db.delete(MyDBHelper.NOTES_TABLE, "ID = ?",
                            new String[]{String.valueOf(note.getID())});
                    handler.post(callback::onRemoveFromDB);
                    break;
            }
        });
    }

    public void executeTask(ArrayList<Note> notes, ArrayList<Topic> topics, Callback callback){ //loads notes to database
        executor.execute(() -> {
            ContentValues contentValues = new ContentValues();
            db = helper.getWritableDatabase();
            for(int i = 0; i < notes.size(); i++){
                contentValues.put(MyDBHelper.KEY_ID, notes.get(i).getID());
                contentValues.put(MyDBHelper.KEY_NAME, notes.get(i).getName());
                contentValues.put(MyDBHelper.KEY_CONTENT, notes.get(i).getContent());
                db.update(MyDBHelper.NOTES_TABLE, contentValues, "ID = ?",
                        new String[]{String.valueOf(notes.get(i).getID())});
            }

            contentValues.clear();

            for(int i = 0; i < topics.size(); i++){
                contentValues.put(MyDBHelper.KEY_ID, i);
                contentValues.put(MyDBHelper.KEY_NAME, topics.get(i).getName());
                db.update(MyDBHelper.NOTES_TABLE, contentValues, "ID = ?",
                        new String[]{String.valueOf(i)});
            }

            handler.post(callback::onLoadToDB);
        });
    }

    public void executeConnect(){
        executor.execute(() -> {
            createMQTTCallback();
            callbackExtended = createMQTTCallback();
            mqttHelper.setCallback(callbackExtended);
            mqttHelper.connect();
        });
    }

    public void executeSubscribe(Topic topic){
        executor.execute(() ->{
            ContentValues contentValues = new ContentValues();
            db = helper.getWritableDatabase();
            contentValues.put(MyDBHelper.KEY_ID, topic.getID());
            contentValues.put(MyDBHelper.KEY_NAME, topic.getName());
            db.insert(MyDBHelper.TOPICS_TABLE, null, contentValues);

            mqttHelper.subscribeToTopic(topic);
        });
    }

    public void executeUnsubscribe(Topic topic){
        executor.execute(() -> {
            db = helper.getWritableDatabase();
            db.delete(MyDBHelper.TOPICS_TABLE, "ID = ?",
                    new String[]{String.valueOf(topic.getID())});

            mqttHelper.unsubscribeFromTopic(topic);
        });
    }

    public void executePublish(MqttMessage message, String topic){
        executor.execute(() -> mqttHelper.publishNote(topic, message));
    }

    public void executeStop(){
        executor.execute(mqttHelper::stop);
    }

    public MqttCallbackExtended createMQTTCallback(){
        return new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection Lost!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                mqttHelper.messageArrived(topic, message, context);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Message Delivered");
            }
        };
    }
}
