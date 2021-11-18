package com.example.challenge2_m1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.example.challenge2_m1.Fragments.NotepadListView;
import com.example.challenge2_m1.Model.Note;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int EMPTY = 0;
    private static final String FILENAME = ".txt";
    private NotepadViewModel viewModel;
    ArrayList<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void readFromTxt(){
        String ret;
        ArrayList<Note> notes = viewModel.getNotes();

        try {
            for(int i = 0; i < notes.size(); i++) {
                InputStream inputStream = this.openFileInput("" + notes.get(i).getName() + ".txt");

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString;
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                        stringBuilder.append("\n");
                    }

                    stringBuilder.deleteCharAt(stringBuilder.length() -1);

                    inputStream.close();
                    ret = stringBuilder.toString();
                    notes.get(i).setContent(ret);

                    System.out.println(inputStream.toString());
                }
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
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
    public void writeToTxt(){
        FileOutputStream fos;
        Note currentNote;
        notes = viewModel.getNotes();


        try {
            for (int i = 0; i < notes.size(); i++) {
                currentNote = notes.get(i);
                fos = openFileOutput(currentNote.getName() + FILENAME, MODE_PRIVATE);
                fos.write(currentNote.getContent().getBytes());
                System.out.println(getFilesDir());
                fos.close();
            }
        }catch(Exception e){ e.printStackTrace(); }
    }
}