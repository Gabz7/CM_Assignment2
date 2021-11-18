package com.example.challenge2_m1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.challenge2_m1.Fragments.NotepadListView;
import com.example.challenge2_m1.Model.Note;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int EMPTY = 0;
    private FragmentManager fragmentManager;
    private NotepadViewModel viewModel;
    ArrayList<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = "";
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(NotepadViewModel.class);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        prefs = getPreferences(MODE_PRIVATE);
        Map<String, ?> data = prefs.getAll();
        if(data.size() != EMPTY){
            for(int i = 0; i < data.size(); i++) {
                name = prefs.getString("Note" + i, "defaultValue");
                viewModel.addNote(name);
            }
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.MainActivityLayout, NotepadListView.class, null)
                .addToBackStack("First Fragment")
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();

        notes = viewModel.getNotes();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor.clear().apply();

        for(int i = 0; i < notes.size(); i++){
            prefsEditor.putString("Note" + i, notes.get(i).getName());
        }
        prefsEditor.apply(); // or commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        notes = viewModel.getNotes();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        //prefsEditor.clear().apply();

        for(int i = 0; i < notes.size(); i++){
            prefsEditor.putString("Note" + i, notes.get(i).getName());
        }
        prefsEditor.apply(); // or commit();
    }
}