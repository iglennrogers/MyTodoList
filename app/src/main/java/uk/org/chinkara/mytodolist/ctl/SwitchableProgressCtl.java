package uk.org.chinkara.mytodolist.ctl;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import uk.org.chinkara.mytodolist.R;

/**
 * Created by g_rogers on 06/07/2015.
 */
public class SwitchableProgressCtl extends LinearLayout implements SeekBar.OnSeekBarChangeListener {

    public interface OnProgressInteractionListener {

        void onProgressChanged(boolean on, int progress);
    }

    public SwitchableProgressCtl(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.switchable_progress_ctl, this, true);

        SeekBar prog = (SeekBar)v.findViewById(R.id.sbProgress);
        prog.setOnSeekBarChangeListener(this);

        View progress = v.findViewById(R.id.swHasProgress);
        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onHasProgressClick();
            }
        });
    }

    public void attach(OnProgressInteractionListener listener) {

        _listener = listener;
    }

    public void set(boolean show, int progress) {

        Switch cbShow = (Switch) findViewById(R.id.swHasProgress);
        cbShow.setChecked(show);
        onHasProgressClick();

        SeekBar prog = (SeekBar)findViewById(R.id.sbProgress);
        prog.setProgress(progress);
        onProgressChanged(prog, progress, false);

    }

    public boolean is_visible() {

        Switch cbShow = (Switch) findViewById(R.id.swHasProgress);
        return cbShow.isChecked();
    }

    public int get() {

        SeekBar prog = (SeekBar)findViewById(R.id.sbProgress);
        return prog.getProgress();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        TextView tv = (TextView)findViewById(R.id.tvProgress);
        progress = (progress/5)*5;
        tv.setText(((Integer) progress).toString() + "%");
    }

    public void onHasProgressClick() {

        Switch sw = (Switch)findViewById(R.id.swHasProgress);
        RelativeLayout prog = (RelativeLayout) findViewById(R.id.lyProgress);

        prog.setVisibility(sw.isChecked() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        TextView tv = (TextView)findViewById(R.id.tvProgress);
        tv.setTypeface(null, Typeface.ITALIC);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        TextView tv = (TextView)findViewById(R.id.tvProgress);
        tv.setTypeface(null, Typeface.NORMAL);

        Switch cbShow = (Switch) findViewById(R.id.swHasProgress);
        boolean show = cbShow.isChecked();

        SeekBar prog = (SeekBar) findViewById(R.id.sbProgress);
        int progress = (prog.getProgress()/5)*5;

        _listener.onProgressChanged(show, progress);
    }

    private OnProgressInteractionListener _listener;
}
