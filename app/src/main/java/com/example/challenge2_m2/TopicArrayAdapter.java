package com.example.challenge2_m2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.example.challenge2_m2.Model.Topic;
import java.util.ArrayList;
import java.util.List;

public class TopicArrayAdapter extends ArrayAdapter<Topic> {
    private final NotepadViewModel viewModel;
    private Filter filter;

    public TopicArrayAdapter(Context context, int resource, ArrayList<Topic> topics, NotepadViewModel viewModel) {
        super(context, resource, R.id.topicLabel, topics);
        this.viewModel = viewModel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Topic currentTopic = viewModel.getTopic(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.topic_element, parent, false);
        }

        TextView tvTopic = convertView.findViewById(R.id.topicLabel);
        tvTopic.setText(currentTopic.getName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AppFilter<>(viewModel.getTopics());
        }
        return filter;
    }

    private class AppFilter<T> extends Filter {

        private final ArrayList<T> sourceObjects;

        public AppFilter(List<T> objects) {
            sourceObjects = new ArrayList<>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            String filterSeq = chars.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq.length() > 0) {
                ArrayList<T> filter = new ArrayList<>();

                for (T object : sourceObjects) {
                    Topic currentTopic = (Topic) object;
                    if ((currentTopic.getName().toLowerCase().contains(filterSeq)))
                        filter.add(object);
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            ArrayList<Topic> filtered = (ArrayList<Topic>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++)
                add(filtered.get(i));
            notifyDataSetInvalidated();
        }
    }
}
