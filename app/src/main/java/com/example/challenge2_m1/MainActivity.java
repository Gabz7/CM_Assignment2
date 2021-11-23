package com.example.challenge2_m1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.challenge2_m1.Fragments.NotepadListView;
import com.example.challenge2_m1.Model.Note;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MyTaskManager.Callback {
    private static final int EMPTY = 0;
    private NotepadViewModel viewModel;
    private MyTaskManager taskManager;
    ArrayList<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskManager = new MyTaskManager(this);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(NotepadViewModel.class);

        readFromSharedPrefs();
        readFromTxt();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.MainActivityLayout, NotepadListView.class, null)
                .addToBackStack("First Fragment")
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeToSharedPrefs();
        writeToTxt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeToSharedPrefs();
        writeToTxt();
    }

    @Override
    public void onSaveToTxt() {
        System.out.println("Notes Successfully Saved to File System");
    }

    @Override
    public void onLoadFromTxt() { System.out.println("Notes Successfully Loaded from File System"); }

    public void readFromSharedPrefs(){
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String name;

        Map<String, ?> data = prefs.getAll();
        if(data.size() != EMPTY){
            for(int i = 0; i < data.size(); i++) {
                name = prefs.getString("Note" + i, "defaultValue");
                viewModel.addNote(name);
            }
        }
    }

    public void writeToSharedPrefs(){
        notes = viewModel.getNotes();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor.clear().apply();

        for(int i = 0; i < notes.size(); i++){
            prefsEditor.putString("Note" + i, notes.get(i).getName());
        }
        prefsEditor.apply(); // or commit();
    }

    public void readFromTxt(){ taskManager.executeLoad(viewModel.getNotes(), this); }

    public void writeToTxt(){
        taskManager.executeSave(viewModel.getNotes(), this);
    }
}