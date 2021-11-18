package com.example.challenge2_m1.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import com.example.challenge2_m1.MyArrayAdapter;
import com.example.challenge2_m1.NotepadViewModel;
import com.example.challenge2_m1.R;

public class NotepadListView extends Fragment {

    private static final int NEW_NOTE = -1;
    private static final int CMD_EDIT = 0;
    private static final int CMD_DELETE = 1;
    private MyArrayAdapter adapter;
    private NotepadViewModel viewModel;
    private SearchView searchbar;
    private Dialog saveDialog;
    private EditText etNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(requireActivity()).get(NotepadViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notelist, container, false);
    }

    @SuppressLint({"InflateParams", "NonConstantResourceId"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new MyArrayAdapter(getContext(), R.layout.note_element, viewModel.getNotes(), viewModel);
        viewModel.setAdapter(adapter);

        searchbar = requireActivity().findViewById(R.id.searcbar);
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = manageString(query);
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                query = manageString(query);
                adapter.getFilter().filter(query);
                return true;
            }

            public String manageString(String query){
                return query.trim().replaceAll(" +", " ");
            }
        });

        Toolbar toolbar = requireActivity().findViewById(R.id.listToolbar);
        toolbar.inflateMenu(R.menu.notelist_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.add_action:
                    viewModel.setElementIndex(NEW_NOTE);
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.MainActivityLayout, NoteView.class, null)
                            .addToBackStack("NoteView Fragment")
                            .commit();
                    return true;
                case R.id.search_action:
                    searchbar.setFocusable(true);
                    searchbar.setIconified(false);
                    searchbar.requestFocusFromTouch();
                    return true;
            }

            return true;
        });

        ListView notesList = requireActivity().findViewById(R.id.notesList);
        registerForContextMenu(notesList);
        notesList.setAdapter(adapter);
        notesList.setOnItemLongClickListener((adapterView, view12, i, l) -> {
            viewModel.setElementIndex(i);
            return false;
        });
        notesList.setOnItemClickListener((adapterView, view1, i, l) -> {
            viewModel.setElementIndex(i);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.MainActivityLayout, NoteView.class, null)
                    .addToBackStack("Second Fragment")
                    .commit();
        });

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        saveDialog = new AlertDialog.Builder(getContext())
                .setTitle("Edit Note")
                .setView(inflater.inflate(R.layout.fragment_edit_dialog, null))
                .setPositiveButton("Edit", (dialog, id) -> {
                    saveNote();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss()).create();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int index = item.getItemId();

        switch (index) {
            case CMD_EDIT:
                saveDialog.show();
                etNote = saveDialog.findViewById(R.id.editNoteName);
                etNote.setFocusable(true);
                etNote.setText(viewModel.getCurrentNote().getName());
                etNote.requestFocusFromTouch();
                return true;
            case CMD_DELETE:
                new AlertDialog.Builder(getContext())
                        .setTitle("Edit Note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes", (dialog, id) -> {
                            deleteNote();
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss()).create().show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, CMD_EDIT, 0, "Edit");
        menu.add(0, CMD_DELETE, 0, "Delete");
    }

    private void saveNote(){ viewModel.updateCurrentName(etNote.getText().toString()); }

    private void deleteNote(){
        viewModel.removeNote(viewModel.getElementIndex());
    }
}