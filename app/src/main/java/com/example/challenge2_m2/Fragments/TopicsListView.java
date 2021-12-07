package com.example.challenge2_m2.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import com.example.challenge2_m2.Model.Topic;
import com.example.challenge2_m2.NotepadViewModel;
import com.example.challenge2_m2.R;
import com.example.challenge2_m2.TopicArrayAdapter;

public class TopicsListView extends Fragment {

    private TopicArrayAdapter topicAdapter;
    private NotepadViewModel viewModel;
    private AlertDialog addDialog, unsubscribeDialog;
    private EditText et;

    public TopicsListView() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(NotepadViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topiclist_, container, false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topicAdapter = new TopicArrayAdapter(getContext(), R.layout.topic_element, viewModel.getTopics(), viewModel);
        viewModel.setTopicAdapter(topicAdapter);

        SearchView searchbar = requireActivity().findViewById(R.id.searchTopic);
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = manageString(query);
                topicAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                query = manageString(query);
                topicAdapter.getFilter().filter(query);
                return true;
            }

            public String manageString(String query){
                return query.trim().replaceAll(" +", " ");
            }
        });

        Toolbar toolbar = requireActivity().findViewById(R.id.topicToolbar);
        toolbar.inflateMenu(R.menu.topiclist_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.add_topic_action:
                    addDialog.show();
                    return true;
                case R.id.cancel_topic_action:
                    getParentFragmentManager().popBackStack();
                    return true;
            }

            return true;
        });

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        unsubscribeDialog = new AlertDialog.Builder(getContext())
                .setTitle("Unsubscribe")
                .setMessage("Are you sure you want to unsubscribe from this topic?")
                .setPositiveButton("Unsubscribe", (dialog, id) -> {
                    viewModel.unsubscribeFromTopic(viewModel.getElementIndex());
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss()).create();

        ListView notesList = requireActivity().findViewById(R.id.topicList);
        notesList.setAdapter(topicAdapter);
        notesList.setOnItemLongClickListener((adapterView, view12, i, l) -> {
            viewModel.setElementIndex((int) l);
            unsubscribeDialog.show();
            return true;
        });

        View layout = inflater.inflate(R.layout.fragment_topic_dialog, null);
        addDialog = new AlertDialog.Builder(getContext())
                .setTitle("Subscribe to Topic")
                .setView(layout)
                .setPositiveButton("Subscribe", (dialog, id) -> {
                    subscribeToTopic();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss()).create();

        et = layout.findViewById(R.id.addTopicName);
    }

    public void subscribeToTopic(){
        Topic newTopic = new Topic(et.getText().toString() + "/");
        viewModel.subscribeToTopic(newTopic);
        et.setText("");
    }
}