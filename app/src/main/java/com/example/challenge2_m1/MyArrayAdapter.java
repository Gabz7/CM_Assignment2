package com.example.challenge2_m1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.challenge2_m1.Model.Note;

import java.util.ArrayList;

public class MyArrayAdapter extends ArrayAdapter<Note> {

    NotepadViewModel viewModel;

    public MyArrayAdapter(Context context, int resource, ArrayList<Note> notes, NotepadViewModel viewModel) {
        super(context, resource,0, notes);
        this.viewModel = viewModel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note currentNote = viewModel.getNote(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_element, parent, false);
        }

        TextView tvNote = convertView.findViewById(R.id.noteLabel);
        tvNote.setText(currentNote.getName());
        return convertView;
    }
}
