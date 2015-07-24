package uk.org.chinkara.mytodolist.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import uk.org.chinkara.mytodolist.R;
import uk.org.chinkara.mytodolist.model.TodoItem;
import uk.org.chinkara.mytodolist.model.TodoItemDatasource;


/**
 * Created by g_rogers on 09/06/2015.
 */
public class ItemSummaryAdapter extends ArrayAdapter<TodoItem> {

    private static String TAG = "<<Todo item summary adapter>>: ";

    public ItemSummaryAdapter(Context context, TodoItemDatasource source) {

        super(context, 0, source.raw_items());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        TodoItem item = getItem(position);
        Log.d(TAG, "position " + String.valueOf(position) + " : id " + String.valueOf(item._id));
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_item_summary, parent, false);
        }

        // Lookup view for data population
        populate_from_item(convertView, item);

        // Return the completed view to render on screen
        return convertView;
    }

    private void populate_from_item(final View convertView, final TodoItem item) {

        ImageView ivLabel = (ImageView)convertView.findViewById(R.id.ivSummaryLabel);
        TextView edTitle = (TextView)convertView.findViewById(R.id.edSummaryTitle);
        TextView edDetails = (TextView) convertView.findViewById(R.id.edSummaryDetails);

        // Populate the data into the template view using the data object
        ivLabel.setBackgroundColor(Color.parseColor(item._label));

        edTitle.setText(item._title);
        if (item._is_complete) {

            edTitle.setPaintFlags(edTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {

            edTitle.setPaintFlags(edTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        edDetails.setText(item._details);
        edDetails.setVisibility((item._details.length() > 0) ? View.VISIBLE : View.GONE);
    }
}
