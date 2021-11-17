package com.example.challenge2_m1.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.challenge2_m1.MyArrayAdapter;
import com.example.challenge2_m1.NotepadViewModel;
import com.example.challenge2_m1.R;

public class NotepadList extends Fragment {

    private static final int CMD_EDIT = 0;
    private static final int CMD_DELETE = 1;
    private ListView notesList;
    private MyArrayAdapter adapter;
    private NotepadViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(NotepadViewModel.class);
        viewModel.populate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notelist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MyArrayAdapter(getContext(), R.layout.note_element, viewModel.getNotes(), viewModel);
        viewModel.setAdpter(adapter);

        notesList = getActivity().findViewById(R.id.notesList);
        registerForContextMenu(notesList);
        notesList.setAdapter(adapter);

        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setElementIndex(i);
                return false;
            }
        });

        notesList.setOnItemClickListener((adapterView, view1, i, l) -> {
            Toast.makeText(getContext(), viewModel.getNote(i).getName(), Toast.LENGTH_LONG).show();
            viewModel.setElementIndex(i);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.MainActivityLayout, NoteView.class, null)
                    .addToBackStack("Second Fragment")
                    .commit();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.printNotes();
        adapter.toString();
        adapter.notifyDataSetChanged();
    }
    /*
    @Override
    public void onPause() {
        super.onPause();
        System.out.println("I'M PAUSED");
    }
    */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Toast.makeText(getContext(), "Long Press Detected: " + viewModel.getElementIndex(), Toast.LENGTH_SHORT).show();
        menu.add(0, CMD_EDIT, 0, "Edit");
        menu.add(0, CMD_DELETE, 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int index = item.getItemId();
        switch (index) {
            case CMD_EDIT:
                System.out.println("Edit Detected");
                FragmentManager fm = getParentFragmentManager();
                EditDialogFragment editNameDialogFragment = EditDialogFragment.newInstance("Edit Note Name");
                editNameDialogFragment.show(fm, "fragment_edit_dialog");
                return true;
            case CMD_DELETE:
                //**confirmDelete**();
                viewModel.removeNote(viewModel.getElementIndex());
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), viewModel.getNote(viewModel.getElementIndex()).getName() + " Deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}