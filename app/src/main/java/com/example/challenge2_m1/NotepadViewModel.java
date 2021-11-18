package com.example.challenge2_m1;

import androidx.lifecycle.ViewModel;
import com.example.challenge2_m1.Model.Note;
import java.util.ArrayList;

public class NotepadViewModel extends ViewModel {
    private final ArrayList<Note> notes = new ArrayList<>();
    private int elementIndex;
    private MyArrayAdapter adapter;

    public void addNote(String name){
        Note note = new Note(name);
        notes.add(note);
    }

    public void addNote(Note note){
        notes.add(note);
        adapter.notifyDataSetChanged();
    }

    public void updateCurrentName(String name){
        notes.get(elementIndex).setName(name);
        adapter.notifyDataSetChanged();
    }

    public void updateContent(String content){
        notes.get(elementIndex).setContent(content);
    }

    public Note getNote(int index){ return notes.get(index); }

    public Note getCurrentNote(){
        if(elementIndex != -1)
            return notes.get(elementIndex);
        else
            return null;
    }

    public ArrayList<Note> getNotes(){ return notes; }

    public int getElementIndex() { return elementIndex; }

    public void setElementIndex(int elementIndex) { this.elementIndex = elementIndex; }

    public void setAdapter(MyArrayAdapter adapter) { this.adapter = adapter; }

    public void removeNote(int index){
        notes.remove(index);
        adapter.notifyDataSetChanged();
    }
}
