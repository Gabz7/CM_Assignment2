package com.example.challenge2_m2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import com.example.challenge2_m2.DB.MyDBHelper;
import com.example.challenge2_m2.Fragments.NotepadListView;
import com.example.challenge2_m2.Model.Note;
import com.example.challenge2_m2.Model.Topic;
import com.example.challenge2_m2.topics.MQTTHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyTaskManager.Callback {
    private static final String GET_ALL = "GetAll";
    private static final String DEFAULT_ID = "Gabz";
    private NotepadViewModel viewModel;
    private MyTaskManager taskManager;
    private MyDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = createOrGetDB();
        viewModel = new ViewModelProvider(this).get(NotepadViewModel.class);
        MQTTHelper helper = new MQTTHelper(this, DEFAULT_ID, viewModel);
        taskManager = new MyTaskManager(db, helper, this);

        viewModel.setTaskManager(taskManager);

        taskManager.executeConnect();
        taskManager.executeTask(GET_ALL, null,this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.MainActivityLayout, NotepadListView.class, null)
                .addToBackStack("First Fragment")
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        taskManager.executeTask(viewModel.getNotes(), viewModel.getTopics(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskManager.executeStop();
    }

    @Override
    public void onLoadFromDB(ArrayList<Note> notes, ArrayList<Topic> topics) {
        viewModel.setNotes(notes);
        int highest = 0;
        for(int i = 0; i < notes.size(); i++){
            if(highest < notes.get(i).getID())
                highest = notes.get(i).getID();
        }

        Note.ID_COUNTER = ++highest;
        viewModel.setTopics(topics);

        highest = 0;
        for(int i = 0; i < topics.size(); i++){
            if(highest < topics.get(i).getID())
                highest = topics.get(i).getID();
        }
        Topic.ID_COUNTER = ++highest;
        System.out.println("Database loaded successfully");
    }

    @Override
    public void onLoadToDB() { System.out.println("Information Loaded to Database successfully"); }

    @Override
    public void onUpdateDB() {}

    @Override
    public void onAddToDB() {}

    @Override
    public void onRemoveFromDB() {}

    public MyDBHelper createOrGetDB(){ return new MyDBHelper(this); }
}