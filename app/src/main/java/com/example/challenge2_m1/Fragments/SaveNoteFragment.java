package com.example.challenge2_m1.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.challenge2_m1.Model.Note;
import com.example.challenge2_m1.NotepadViewModel;
import com.example.challenge2_m1.R;

public class SaveNoteFragment extends DialogFragment {

    EditText noteName;
    Button confirm, cancel;
    NotepadViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(NotepadViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        noteName = view.findViewById(R.id.NoteName);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Save Note Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        noteName.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
/*
        cancel = view.findViewById(R.id.cancel_save_button);
        cancel.setOnClickListener(view1 -> {
            dismiss();
        });

        confirm = view.findViewById(R.id.confirm_save_button);
        confirm.setOnClickListener(view1 -> {
            viewModel.updateNote(viewModel.getElementIndex(), noteName.getText().toString());
            dismiss();
        });*/
    }



    public static SaveNoteFragment newInstance(String title) {
        SaveNoteFragment frag = new SaveNoteFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }
}