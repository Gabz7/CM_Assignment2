package com.example.challenge2_m1.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.challenge2_m1.Model.Note;
import com.example.challenge2_m1.NotepadViewModel;
import com.example.challenge2_m1.R;

public class NoteView extends Fragment {

    private static final int NEW_NOTE = -1;
    private Button saveButton, cancelButton;
    private EditText etNote;
    private Toolbar toolbar;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        saveButton = getActivity().findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        cancelButton = getActivity().findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        etNote = getActivity().findViewById(R.id.etNote);
        etNote.setFocusable(true);
        etNote.requestFocusFromTouch();

        if(viewModel.getElementIndex() != NEW_NOTE){
            etNote.setText(viewModel.getNote(viewModel.getElementIndex()).getContent());
        }

        toolbar = getActivity().findViewById(R.id.noteToolbar);
    }

    private void saveNote(){
        if(viewModel.getElementIndex() == NEW_NOTE) {
            EditText et = saveDialog.findViewById(R.id.saveNoteName);
            String name = et.getText().toString();
            Note note = new Note(name);

            note.setContent(etNote.getText().toString());
            viewModel.addNote(note);
        }else{
            viewModel.updateContent(etNote.getText().toString());
        }
    }
}