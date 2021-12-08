package com.example.challenge2_m2;

import androidx.lifecycle.ViewModel;
import com.example.challenge2_m2.Model.Note;
import com.example.challenge2_m2.Model.Topic;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NotepadViewModel extends ViewModel{
    private final ArrayList<Note> notes = new ArrayList<>();
    private final ArrayList<Topic> topics = new ArrayList<>();
    private int elementIndex;
    private static final String ADD = "Add";
    private static final String UPDATE_NAME = "UpdateName";
    private static final String UPDATE_CONTENT = "UpdateContent";
    private static final String REMOVE = "Remove";
    private MyTaskManager taskManager;
    private MyArrayAdapter adapter;
    private TopicArrayAdapter topicAdapter;

    public void addNote(Note note, MyTaskManager.Callback callback){
        taskManager.executeTask(ADD, note, callback);
        notes.add(note);
        adapter.notifyDataSetChanged();
    }

    public void publishNote(int index, String topic){
        try {
            String message;
            JSONObject json = new JSONObject();

            if(notes.get(index).getName().equalsIgnoreCase(""))
                json.isNull("Name");
            else
                json.put("Name", notes.get(index).getName());

            if(notes.get(index).getContent().equalsIgnoreCase(""))
                json.isNull("Content");
            else
                json.put("Content", notes.get(index).getContent());

            message = json.toString();
            MqttMessage mqttMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
            taskManager.executePublish(mqttMessage, topic);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removeNote(int index, MyTaskManager.Callback callback){
        taskManager.executeTask(REMOVE, notes.get(index), callback);
        notes.remove(index);
        adapter.notifyDataSetChanged();
    }

    public void updateCurrentName(String name, MyTaskManager.Callback callback){
        notes.get(elementIndex).setName(name);
        taskManager.executeTask(UPDATE_NAME, notes.get(elementIndex), callback);
        adapter.notifyDataSetChanged();
    }

    public void setTaskManager(MyTaskManager taskManager){ this.taskManager = taskManager; }

    public void updateContent(String content, MyTaskManager.Callback callback){
        notes.get(elementIndex).setContent(content);
        taskManager.executeTask(UPDATE_CONTENT, notes.get(elementIndex), callback);
    }

    public Note getNote(int index){ return notes.get(index); }

    public ArrayList<Topic> getTopics(){ return topics; }

    public ArrayList<Note> getNotes(){ return notes; }

    public Topic getTopic(int index){ return topics.get(index); }

    public Note getCurrentNote(){
        if(elementIndex != -1)
            return notes.get(elementIndex);
        else
            return null;
    }

    public int getElementIndex() { return elementIndex; }

    public void setNotes(ArrayList<Note> notes){
        for(int i = 0; i < notes.size(); i++){
            this.notes.add(notes.get(i));
            adapter.notifyDataSetChanged();
        }
    }

    public void setTopics(ArrayList<Topic> topics){
        for(int i = 0; i < topics.size(); i++){
            this.topics.add(topics.get(i));
            adapter.notifyDataSetChanged();
        }
    }

    public void setElementIndex(int elementIndex) { this.elementIndex = elementIndex; }

    public void setAdapter(MyArrayAdapter adapter) { this.adapter = adapter; }

    public void setTopicAdapter(TopicArrayAdapter adapter) { this.topicAdapter = adapter; }

    public void subscribeToTopic(Topic topic){
        taskManager.executeSubscribe(topic);
        topics.add(topic);
        topicAdapter.notifyDataSetChanged();
    }

    public void unsubscribeFromTopic(int index){
        taskManager.executeUnsubscribe(topics.get(index));
        topics.remove(index);
        topicAdapter.notifyDataSetChanged();
    }
}
