package ave.tec.timer2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private LinearLayout timer_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer_container = findViewById(R.id.timer_container);

        ((Button) findViewById(R.id.btn_addtimer)).setOnClickListener(v ->{
            openDialog();
        });
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add timer");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.addtimer_layout, (ViewGroup) getWindow().getDecorView().getRootView(), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.et_timerInput);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int num = Integer.parseInt(input.getText().toString());
                addTimerView(num);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addTimerView(int numbersToAdd) {
        View view = LayoutInflater.from(this).inflate(R.layout.timer_layout, timer_container, false);
        TextView textView = (TextView) view.findViewById(R.id.tv_timer);
        Button btn_start = (Button) view.findViewById(R.id.btn_start);
        Button btn_stop = (Button) view.findViewById(R.id.btn_stop);
        Button btn_reset = (Button) view.findViewById(R.id.btn_reset);

        timer_container.addView(view);
        TimerThread timerThread = new TimerThread(numbersToAdd, textView, this);
        Thread thread = new Thread(timerThread);
        btn_start.setOnClickListener(v->{
            timerThread.start();
        });
        btn_stop.setOnClickListener(v->{
            timerThread.stop();
        });
        btn_reset.setOnClickListener(v->{
            timerThread.reset();
        });
    }
}

class TimerThread implements Runnable {

    private int startTime;
    private int numberOfSecondsRemaining;
    private boolean isRunning;
    private TextView view;
    private MainActivity activity;

    public TimerThread(int numberOfSecondsRemaining, TextView view, MainActivity activity) {
        this.startTime = numberOfSecondsRemaining;
        this.numberOfSecondsRemaining = numberOfSecondsRemaining;
        this.view = view;
        this.activity = activity;
        updateText();
    }

    @Override
    public void run() {
        while(numberOfSecondsRemaining > 0 && isRunning) {
            updateText();

            numberOfSecondsRemaining--;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void start(){
        isRunning = true;
        Thread thread = new Thread(this);
        thread.start();
    }
    public void stop(){
        isRunning = false;
    }
    public void reset(){
        this.numberOfSecondsRemaining = this.startTime;
        stop();
        updateText();
    }

    private void updateText(){
        int hours = (int) numberOfSecondsRemaining / 3600;
        int minutes = (int) (numberOfSecondsRemaining % 3600) / 60;
        int seconds = (int) (numberOfSecondsRemaining % 60);
        String time = String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);

        activity.runOnUiThread(()->{
            view.setText(time);
        });
    }
}