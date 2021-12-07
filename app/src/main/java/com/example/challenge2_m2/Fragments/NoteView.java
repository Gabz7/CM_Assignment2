package com.example.challenge2_m2.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.challenge2_m2.Model.Note;
import com.example.challenge2_m2.Model.Topic;
import com.example.challenge2_m2.MyTaskManager;
import com.example.challenge2_m2.NotepadViewModel;
import com.example.challenge2_m2.R;
import java.util.ArrayList;

public class NoteView extends Fragment implements MyTaskManager.Callback {

    private static final int NEW_NOTE = -1;
    private EditText etNote;
    private NotepadViewModel viewModel;
    private Dialog saveDialog;

    public NoteView() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(NotepadViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_view, container, false);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        Button saveButton = requireActivity().findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            if(viewModel.getElementIndex() == NEW_NOTE) {
                saveDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Save Note")
                        .setView(inflater.inflate(R.layout.fragment_save_note, null))
                        .setPositiveButton("Save", (dialog, id) -> {
                            saveNote();
                            getParentFragmentManager().popBackStack();
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                        .create();
                saveDialog.show();
            }else{
                saveNote();
                getParentFragmentManager().popBackStack();
            }
        });

        Button cancelButton = requireActivity().findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        etNote = requireActivity().findViewById(R.id.etNote);
        etNote.setFocusable(true);
        etNote.requestFocusFromTouch();

        if(viewModel.getElementIndex() != NEW_NOTE){
            etNote.setText(viewModel.getNote(viewModel.getElementIndex()).getContent());
        }
    }

    private void saveNote(){
        if(viewModel.getElementIndex() == NEW_NOTE) {
            EditText et = saveDialog.findViewById(R.id.saveNoteName);
            String name = et.getText().toString();
            Note note = new Note(name, etNote.getText().toString());
            viewModel.addNote(note, this);
        }else{
            viewModel.updateContent(etNote.getText().toString(), this);
        }
    }

    @Override
    public void onAddToDB() {}

    @Override
    public void onLoadFromDB(ArrayList<Note> notes, ArrayList<Topic> topics) {}

    @Override
    public void onLoadToDB() {}

    @Override
    public void onRemoveFromDB() {}

    @Override
    public void onUpdateDB() { System.out.println("Database updated Successfully :)"); }
}