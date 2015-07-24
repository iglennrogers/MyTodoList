package uk.org.chinkara.mytodolist.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import uk.org.chinkara.mytodolist.R;
import uk.org.chinkara.mytodolist.model.TodoItem;
import uk.org.chinkara.mytodolist.model.TodoItemDatasource;


/**
 * Created by g_rogers on 09/06/2015.
 */
public class ItemViewAdapter extends ArrayAdapter<TodoItem> {

    private static String TAG = "<<Todo adapter>>: ";

    public ItemViewAdapter(Context context, TodoItemDatasource source) {

        super(context, 0, source.raw_items());
        _source = source;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        TodoItem item = getItem(position);
        Log.d(TAG, "position " + String.valueOf(position) + " : id " + String.valueOf(item._id));
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_view_item, parent, false);
        }

        // Lookup view for data population
        populate_from_item(convertView, item);

        // Return the completed view to render on screen
        return convertView;
    }

    public void onIsCompleteClick(final View itemview, final TodoItem item, boolean update_db) {

        TextView edTitle = (TextView)itemview.findViewById(R.id.edTitle);
        CheckBox button = (CheckBox)itemview.findViewById(R.id.cbIsCompleteList);
        if (button.isChecked()) {

            edTitle.setPaintFlags(edTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            item._is_complete = true;
        }
        else {

            edTitle.setPaintFlags(edTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            item._is_complete = false;
        }

        if (update_db) {

            _source.update_record(item);
        }
    }

    private void populate_from_item(final View convertView, final TodoItem item) {

        ImageView ivLabel = (ImageView)convertView.findViewById(R.id.ivLabel);
        TextView edTitle = (TextView)convertView.findViewById(R.id.edTitle);
        TextView edDetails = (TextView) convertView.findViewById(R.id.edDetails);
        CheckBox cbComplete = (CheckBox) convertView.findViewById(R.id.cbIsCompleteList);
        cbComplete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onIsCompleteClick(convertView, item, true);
            }
        });

        LinearLayout lyProgress = (LinearLayout)convertView.findViewById(R.id.lyProgress);
        ProgressBar prog = (ProgressBar) convertView.findViewById(R.id.sbProgress);

        LinearLayout lyDueDate = (LinearLayout)convertView.findViewById(R.id.lyDueDate);
        TextView tvDueDate = (TextView) convertView.findViewById(R.id.tvDueDate);

        // Populate the data into the template view using the data object
        if (!convertView.isInEditMode()) {

            ivLabel.setBackgroundColor(Color.parseColor(item._label));
            edTitle.setText(item._title);
            edDetails.setText(item._details);
            edDetails.setVisibility((item._details.length() > 0) ? View.VISIBLE : View.GONE);

            cbComplete.setChecked(item._is_complete);
            onIsCompleteClick(convertView, item, false);

            if (item._has_progress) {

                prog.setProgress(item._progress);
            }
            lyProgress.setVisibility(item._has_progress ? View.VISIBLE : View.GONE);

            if (item._has_duedate) {

                tvDueDate.setText(item.get_duedate());
            }
            lyDueDate.setVisibility(item._has_duedate ? View.VISIBLE : View.GONE);

            if (item._to_be_deleted) {

                edTitle.setTextColor(Color.RED);
            }
        }
        else {

            edTitle.setText("Title");
            edDetails.setText("Details");
            cbComplete.setSelected(false);

            prog.setProgress(40);
            lyProgress.setVisibility(View.VISIBLE);

            tvDueDate.setText("31/12/2015");
            lyDueDate.setVisibility(View.VISIBLE);
        }
    }

    private TodoItemDatasource _source;
}
