package uk.org.chinkara.mytodolist.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import uk.org.chinkara.mytodolist.R;
import uk.org.chinkara.mytodolist.adapters.ItemSummaryAdapter;
import uk.org.chinkara.mytodolist.model.TodoItem;
import uk.org.chinkara.mytodolist.model.TodoItemDatasource;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemConfigureInteractionListener}
 * interface.
 */
public class TodoItemWidgetConfigureFragment extends ListFragment {

    private static String TAG = "<<TodoItemWidget config f>> :";

    public TodoItemWidgetConfigureFragment() {

        Log.d(TAG, "construct");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "begin onCreate");
        Log.d(TAG, "end onCreate");
    }

    @Override
    public void onAttach(Activity activity) {

        Log.d(TAG, "begin onAttach");
        super.onAttach(activity);

        _items = new TodoItemDatasource(activity);
        _items.open();
        _items.select_all_records();
        setListAdapter(new ItemSummaryAdapter(activity, _items));

        try {

            _listener = (OnItemConfigureInteractionListener) activity;
        }
        catch (ClassCastException e) {

            throw new ClassCastException(activity.toString()
                    + " must implement OnItemConfigureInteractionListener");
        }
        Log.d(TAG, "end onAttach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "begin onCreateView");
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "end onCreateView");
        return view;
    }

    @Override
    public void onStart() {

        Log.d(TAG, "begin onStart");
        super.onStart();
        setEmptyText(getResources().getString(R.string.no_items));
        Log.d(TAG, "end onStart");
    }

    @Override
    public void onDetach() {

        Log.d(TAG, "begin onDetach");
        super.onDetach();
        _items.close();
        _listener = null;
        Log.d(TAG, "end onDetach");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Log.d(TAG, "begin onClick");
        super.onListItemClick(l, v, position, id);

        if (null != _listener) {

            TodoItem item = _items.at(position);

            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            _listener.onItemConfigureInteraction(item);
        }
        Log.d(TAG, "end onClick");
    }


    public interface OnItemConfigureInteractionListener {

        void onItemConfigureInteraction(TodoItem item);
    }

    private TodoItemDatasource _items;
    private OnItemConfigureInteractionListener _listener;
}
