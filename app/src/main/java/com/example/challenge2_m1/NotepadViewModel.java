package com.example.challenge2_m1;

import androidx.lifecycle.ViewModel;

import com.example.challenge2_m1.Model.Note;

import java.util.ArrayList;

public class NotepadViewModel extends ViewModel {
    private static final int NOTE_INIT = 5;
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

    public void removeNote(int index){
        notes.remove(index);
        adapter.notifyDataSetChanged();
    }

    public void updateNote(int index, String newName){
        notes.get(index).setName(newName);
        adapter.notifyDataSetChanged();
    }

    public void updateContent(String content){
        notes.get(elementIndex).setContent(content);
    }

    public void setAdpter(MyArrayAdapter adapter) { this.adapter = adapter; }

    public Note getNote(int index){ return notes.get(index); }

    public ArrayList<Note> getNotes(){ return notes; }

    public int getElementIndex() { return elementIndex; }

    public void setElementIndex(int elementIndex) { this.elementIndex = elementIndex; }

    public void populate(){
        for(int i = 0; i < NOTE_INIT; i++){
            notes.add(new Note("Note " + i));
        }
    }
}
