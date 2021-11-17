package com.example.challenge2_m1.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.challenge2_m1.NotepadViewModel;
import com.example.challenge2_m1.R;

public class EditDialogFragment extends DialogFragment {

    private EditText noteName;
    private Button cancel, confirm;
    private NotepadViewModel viewModel;
    private EditText etNewName;

    public EditDialogFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(NotepadViewModel.class);
        return inflater.inflate(R.layout.fragment_edit_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        noteName = view.findViewById(R.id.NoteName);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Edit Note Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        noteName.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        etNewName = view.findViewById(R.id.NoteName);

        cancel = view.findViewById(R.id.cancel_confirm_button);
        cancel.setOnClickListener(view1 -> {
            dismiss();
        });

        confirm = view.findViewById(R.id.edit_confirm_button);
        confirm.setOnClickListener(view1 -> {
            viewModel.updateNote(viewModel.getElementIndex(), etNewName.getText().toString());
            dismiss();
        });
    }

    public static EditDialogFragment newInstance(String title) {
        EditDialogFragment frag = new EditDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

}