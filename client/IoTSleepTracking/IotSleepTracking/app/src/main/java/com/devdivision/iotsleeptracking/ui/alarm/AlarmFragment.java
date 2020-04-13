package com.devdivision.iotsleeptracking.ui.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.devdivision.iotsleeptracking.AlertReceiver;
import com.devdivision.iotsleeptracking.R;
import com.devdivision.iotsleeptracking.TimePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AlarmFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    private AlarmViewModel alarmViewModel;
    private TextView textAlarmSet;
    private EditText intervalEditText;
    private Integer interval;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alarmViewModel =
                ViewModelProviders.of(this).get(AlarmViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_alarm, container, false);
        intervalEditText = root.findViewById(R.id.interval);
        textAlarmSet = root.findViewById(R.id.textAlarmSet);

        Button buttonTimePicker = root.findViewById(R.id.button_timepicker);
        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(intervalEditText.getText().toString())) {
                    intervalEditText.setError("You have to set an interval");
                    return;
                }
                interval = Integer.parseInt(intervalEditText.getText().toString());
                TimePickerFragment timePicker = new TimePickerFragment();
                timePicker.setCallBack(ontime);
                timePicker.show(getActivity().getSupportFragmentManager(), "time picker");
            }
        });

//        getAlarm(textAlarmSet);
        final Handler handler = new Handler();
        final int delay = 10000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                Log.d("STATE", ">>> GetAlarm RUN");
                getAlarm(textAlarmSet, intervalEditText);
                handler.postDelayed(this, delay);
            }
        }, delay);

        Button buttonCancelAlarm = root.findViewById(R.id.button_cancel);
        buttonCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

        return root;
    }

    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);

            updateTimeText(c);
            startAlarm(c);
        }
    };

    private void getAlarm(final TextView textAlarmSet, final EditText intervalEditText) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://iotsleeptracking.herokuapp.com/alarm";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("STATE", "GetAlarm request failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("STATE", "GetAlarm request success");
                    final String myResponse = response.body().string();
                    if (getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonarray = new JSONArray(myResponse);
                                String textTime = jsonarray.getJSONObject(jsonarray.length()-1).getString("alarm_date").toString();
                                String interval = jsonarray.getJSONObject(jsonarray.length()-1).getString("interval").toString();
                                String[] dataTime = textTime.split(":");
                                textAlarmSet.setText(textTime);
                                intervalEditText.setText(interval);

                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dataTime[0]));
                                c.set(Calendar.MINUTE, Integer.parseInt(dataTime[1]));
                                c.set(Calendar.SECOND, 0);
                                startAlarm(c);

                            } catch (JSONException e) {
                                Log.e("ERROR", e.toString());
                            }
                        }
                    });
                }
            }
        });
    }

    private void postAlarm(String time) {
        if (getActivity() != null)
            Toast.makeText(getActivity(), "Request sended...", Toast.LENGTH_SHORT).show();
        OkHttpClient client = new OkHttpClient();
        String url = "https://iotsleeptracking.herokuapp.com/alarm";
        RequestBody formBody = new FormBody.Builder()
                .add("alarm_date", time)
                .add("interval", interval.toString())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("STATE", "PostAlarm request failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("STATE", "PostAlarm request success");
                    if (getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), "Alarm successfully set !", Toast.LENGTH_LONG).show();
                        }
                    });
                    final String myResponse = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                Log.d("STATE", myResponse.toString());
                        }
                    });
                }
            }
        });
    }

    private void updateTimeText(Calendar c) {
        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        postAlarm(time);
        textAlarmSet.setText(time);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        intent.putExtra("extra", "yes");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        intent.putExtra("extra", "no");
        getActivity().sendBroadcast(intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        textAlarmSet.setText("Alarm canceled");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}