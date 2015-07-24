package uk.org.chinkara.mytodolist.ctl;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import uk.org.chinkara.mytodolist.R;
import uk.org.chinkara.mytodolist.model.TodoItem;

/**
 * Created by g_rogers on 07/07/2015.
 */
public class SwitchableDueDateCtl extends LinearLayout {

    public interface OnDateInteractionListener {

        void onDateChanged(boolean on, GregorianCalendar date);
    }

    public SwitchableDueDateCtl(Context context, AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.switchable_duedate_ctl, this, true);

        View duedate = v.findViewById(R.id.swHasDueDate);
        duedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onHasDueDateClick(v);
            }
        });

        View date = v.findViewById(R.id.tvDueDate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDueDateClick(v);
            }
        });
    }

    public void attach(Activity activity, OnDateInteractionListener listener) {

        _activity = activity;
        _listener = listener;
    }

    public void onHasDueDateClick(View ctl) {

        TextView tvDueDate = (TextView)findViewById(R.id.tvDueDate);
        Switch sw = (Switch)findViewById(R.id.swHasDueDate);
        tvDueDate.setVisibility(sw.isChecked() ? View.VISIBLE : View.GONE);
        _listener.onDateChanged(sw.isChecked(), TodoItem.date_as_cal(tvDueDate.getText().toString()));
    }

    // date picker
    public void onDueDateClick(View v) {

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                GregorianCalendar dt = new GregorianCalendar(year, month, day);
                set(true, dt);
                _listener.onDateChanged(true, dt);
            }
        };

        DialogFragment newFragment = DatePickerFragment.newInstance(get(), listener);
        newFragment.show(_activity.getFragmentManager(), "datePicker");
    }

    public void set(boolean show, GregorianCalendar date) {

        Switch sw = (Switch)findViewById(R.id.swHasDueDate);
        sw.setChecked(show);
        onHasDueDateClick(sw);

        TextView tvDueDate = (TextView)findViewById(R.id.tvDueDate);
        tvDueDate.setText(TodoItem.date_as_string(date));
    }

    public boolean is_visible() {

        Switch cbShow = (Switch) findViewById(R.id.swHasDueDate);
        return cbShow.isChecked();
    }

    public GregorianCalendar get() {

        TextView tvDueDate = (TextView)findViewById(R.id.tvDueDate);
        return TodoItem.date_as_cal(tvDueDate.getText().toString());
    }

    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener _listener;
        private Calendar _cal;

        static DatePickerFragment newInstance(Calendar cal, DatePickerDialog.OnDateSetListener listener) {

            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment._listener = listener;
            newFragment._cal = cal;
            return newFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            int year = _cal.get(Calendar.YEAR);
            int month = _cal.get(Calendar.MONTH);
            int day = _cal.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), _listener, year, month, day);
        }
    }

    private OnDateInteractionListener _listener;
    private Activity _activity;
}
