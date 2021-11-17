package com.example.challenge2_m1.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.example.challenge2_m1.MyArrayAdapter;
import com.example.challenge2_m1.NotepadViewModel;
import com.example.challenge2_m1.R;

public class NotepadListView extends Fragment {

    private static final int NEW_NOTE = -1;
    private static final int CMD_EDIT = 0;
    private static final int CMD_DELETE = 1;
    private ListView notesList;
    private MyArrayAdapter adapter;
    private NotepadViewModel viewModel;
    private Toolbar toolbar;
    private SearchView searchbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(requireActivity()).get(NotepadViewModel.class);
        //viewModel.populate();
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

        searchbar = getActivity().findViewById(R.id.searcbar);
        /*searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });*/

        toolbar = getActivity().findViewById(R.id.listToolbar);
        toolbar.inflateMenu(R.menu.notelist_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.add_action:
                    viewModel.setElementIndex(NEW_NOTE);
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.MainActivityLayout, NoteView.class, null)
                            .addToBackStack("Noteview Fragment")
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

        notesList = getActivity().findViewById(R.id.notesList);
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
                viewModel.removeNote(viewModel.getElementIndex());
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(this.getContext(), "Note Saved", Toast.LENGTH_SHORT);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, CMD_EDIT, 0, "Edit");
        menu.add(0, CMD_DELETE, 0, "Delete");
    }

}