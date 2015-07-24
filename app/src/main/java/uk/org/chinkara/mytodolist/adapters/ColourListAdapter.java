package uk.org.chinkara.mytodolist.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import uk.org.chinkara.mytodolist.R;

public class ColourListAdapter extends ArrayAdapter<String> {

    public ColourListAdapter (Context context, ArrayList<String> items) {

        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        String item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_colour_item, parent, false);
        }

        // Lookup view for data population
        convertView.setBackgroundColor(Color.parseColor(item));

        // Return the completed view to render on screen
        return convertView;
    }
}
