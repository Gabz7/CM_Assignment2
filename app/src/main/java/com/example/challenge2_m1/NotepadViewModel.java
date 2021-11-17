package com.example.challenge2_m1;

import androidx.lifecycle.ViewModel;

import com.example.challenge2_m1.Model.Note;

import java.util.ArrayList;

public class NotepadViewModel extends ViewModel {
    private static final int NOTE_INIT = 5;
    private ArrayList<Note> notes = new ArrayList<>();
    private int elementIndex;
    private MyArrayAdapter adapter;

    public void addNote(String name){
        Note note = new Note(name);
        notes.add(note);
    }

    public void setAdpter(MyArrayAdapter adapter) { this.adapter = adapter; }

    public MyArrayAdapter getAdapter() { return adapter; }

    public Note getNote(int index){ return notes.get(index); }

    public ArrayList<Note> getNotes(){ return notes; }

    public void updateNote(int index, String newName){
        notes.get(index).setName(newName);
        adapter.notifyDataSetChanged();
    }

    public int getElementIndex() { return elementIndex; }

    public void setElementIndex(int elementIndex) { this.elementIndex = elementIndex; }

    public void removeNote(int index){ notes.remove(index); }

    public void printNotes(){
        for(int i = 0; i < notes.size(); i++)
            System.out.println(notes.get(i).getName());
    }

    public void populate(){
        for(int i = 0; i < NOTE_INIT; i++){
            notes.add(new Note("Note " + i));
        }
    }
}
