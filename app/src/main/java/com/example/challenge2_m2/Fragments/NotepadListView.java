package com.example.challenge2_m2.Fragments;

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
import com.example.challenge2_m2.Model.Note;
import com.example.challenge2_m2.Model.Topic;
import com.example.challenge2_m2.MyArrayAdapter;
import com.example.challenge2_m2.MyTaskManager;
import com.example.challenge2_m2.NotepadViewModel;
import com.example.challenge2_m2.R;
import java.util.ArrayList;
import java.util.Arrays;

public class NotepadListView extends Fragment implements MyTaskManager.Callback {
    private static final int NEW_NOTE = -1;
    private static final int CMD_EDIT = 0;
    private static final int CMD_DELETE = 1;
    private static final int CMD_SHARE = 2;
    private MyArrayAdapter adapter;
    private NotepadViewModel viewModel;
    private Dialog saveDialog;
    private EditText etNote;
    private LayoutInflater inflater;
    private View shareView;

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

        SearchView searchbar = requireActivity().findViewById(R.id.searchbar);
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
                case R.id.go_topic_action:
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.MainActivityLayout, TopicsListView.class, null)
                            .addToBackStack("NoteListView Fragment")
                            .commit();
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

        inflater = requireActivity().getLayoutInflater();
        saveDialog = new AlertDialog.Builder(getContext())
                .setTitle("Edit Note")
                .setView(inflater.inflate(R.layout.fragment_edit_dialog, null))
                .setPositiveButton("Edit", (dialog, id) -> {
                    saveNote();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss()).create();
    }

    @SuppressLint("InflateParams")
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
            case CMD_SHARE:
                shareView = inflater.inflate(R.layout.fragment_edit_dialog, null);
                AlertDialog shareDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Share Note")
                        .setMessage("To what(which) topic(s) would you like to share this note to?")
                        .setView(shareView)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            shareNote();
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss()).create();

                shareDialog.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onAddToDB() {}

    @Override
    public void onLoadFromDB(ArrayList<Note> notes, ArrayList<Topic> topics) {}

    @Override
    public void onLoadToDB() {}

    @Override
    public void onUpdateDB() { System.out.println("Database updated Successfully :)"); }

    @Override
    public void onRemoveFromDB() { System.out.println("Note Successfully Removed:)"); }

    public void onCreateContextMenu(ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, CMD_EDIT, 0, "Edit");
        menu.add(0, CMD_DELETE, 0, "Delete");
        menu.add(0, CMD_SHARE, 0, "Share");
    }

    private void saveNote(){ viewModel.updateCurrentName(etNote.getText().toString(), this); }

    private void deleteNote(){
        viewModel.removeNote(viewModel.getElementIndex(), this);
    }

    private void shareNote(){
        EditText etTopics = shareView.findViewById(R.id.editNoteName);
        String topics = etTopics.getText().toString();
        if(topics.contains(",")){
            ArrayList<String> topicsList = new ArrayList<>(Arrays.asList(topics.split(",")));
            for(int i = 0; i < topicsList.size(); i++){
                viewModel.publishNote(viewModel.getElementIndex(), topicsList.get(i) + "/");
            }
        }else
            viewModel.publishNote(viewModel.getElementIndex(), topics + "/");
    }
}