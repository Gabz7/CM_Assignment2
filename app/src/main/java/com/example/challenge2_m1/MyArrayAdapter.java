package com.example.challenge2_m1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.example.challenge2_m1.Model.Note;
import java.util.ArrayList;
import java.util.List;

public class MyArrayAdapter extends ArrayAdapter<Note> {

    private final NotepadViewModel viewModel;
    private Filter filter;

    public MyArrayAdapter(Context context, int resource, ArrayList<Note> notes, NotepadViewModel viewModel) {
        super(context, resource,0, notes);
        this.viewModel = viewModel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note currentNote = viewModel.getNote(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_element, parent, false);
        }

        TextView tvNote = convertView.findViewById(R.id.noteLabel);
        tvNote.setText(currentNote.getName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AppFilter<>(viewModel.getNotes());
        }
        return filter;
    }

    /**
     * Class for filtering in Arraylist listview. Objects need a valid
     * 'toString()' method.
     *
     * @author Tobias Schürg inspired by Alxandr
     *         (http://stackoverflow.com/a/2726348/570168)
     *
     */
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
                    Note currentNote = (Note) object; // Edited to work with our Note class
                    if ((currentNote.getName().toLowerCase().contains(filterSeq)))
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
            ArrayList<T> filtered = (ArrayList<T>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++)
                add((Note) filtered.get(i));
            notifyDataSetInvalidated();
        }
    }

}
