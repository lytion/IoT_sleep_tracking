package com.devdivision.iotsleeptracking.ui.bedroom;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.devdivision.iotsleeptracking.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BedroomFragment extends Fragment {

    private BedroomViewModel roomViewModel;
    private TextView currentTemperature;
    private LineChart temperatureChart;

    private TextView currentHumidity;
    private LineChart humidityChart;

    private TextView currentLight;
    private LineChart lightChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        roomViewModel =
                ViewModelProviders.of(this).get(BedroomViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_bedroom, container, false);


        final Handler handler = new Handler();
        final int delay = 60000; //milliseconds

        getRoomEnvironment(root);
        handler.postDelayed(new Runnable(){
            public void run(){
                Log.d("STATE", ">>> GetRoomEnvironment RUN");
                getRoomEnvironment(root);
                handler.postDelayed(this, delay);
            }
        }, delay);

        return root;
    }

    private void getRoomEnvironment(View root)
    {
        currentTemperature = root.findViewById(R.id.current_temperature);
        temperatureChart = root.findViewById(R.id.temperate_chart);

        currentHumidity = root.findViewById(R.id.current_humidity);
        humidityChart = root.findViewById(R.id.humidity_chart);

        currentLight = root.findViewById(R.id.current_light);
        lightChart = root.findViewById(R.id.light_chart);

        OkHttpClient client = new OkHttpClient();
        String url = "https://iotsleeptracking.herokuapp.com/roomenvironment";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("STATE", "roomEnvironment request failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("STATE", "roomEnvironment request success");
                    final String myResponse = response.body().string();
                    if (getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonarray = new JSONArray(myResponse);
                                currentTemperature.setText("Current temperature: " + jsonarray.getJSONObject(jsonarray.length()-1).getString("temperature").toString() + " °C");
                                currentHumidity.setText("Current humidity: " + jsonarray.getJSONObject(jsonarray.length()-1).getString("humidity").toString() + " %");
                                currentLight.setText("Current light: " + jsonarray.getJSONObject(jsonarray.length()-1).getString("light").toString() + " lx");

                                List<Entry> temperatureEntry = new ArrayList<>();
                                List<Entry> humidityEntry = new ArrayList<>();
                                List<Entry> lightEntry = new ArrayList<>();
                                String[] dates;
                                int i = (jsonarray.length() > 100) ? jsonarray.length() - 100 : 0;
                                for (int idx = i; idx < jsonarray.length(); idx++) {
                                    JSONObject jsonobject = jsonarray.getJSONObject(idx);
                                    temperatureEntry.add(new Entry(idx, Float.parseFloat(jsonobject.getString("temperature"))));
                                    humidityEntry.add(new Entry(idx, Float.parseFloat(jsonobject.getString("humidity"))));
                                    lightEntry.add(new Entry(idx, Float.parseFloat(jsonobject.getString("light"))));
                                }

                                TemperatureChart(temperatureEntry);
                                HumidityChart(humidityEntry);
                                LightChart(lightEntry);


                            } catch (JSONException e) {
                                Log.e("ERROR", e.toString());
                            }
                        }
                    });
                }
            }
        });
    }

    private void TemperatureChart(List<Entry> temperatureEntry)
    {
        YAxis temperatureleftAxis = temperatureChart.getAxisLeft();
        YAxis temperaturerightAxis = temperatureChart.getAxisRight();
        // don't print right Y axis and Y axis in grid
        temperaturerightAxis.setDrawLabels(false);
        temperaturerightAxis.setDrawGridLines(false);
        // Maximum value for a good sleep
        LimitLine temperatureLine = new LimitLine(28, "Maximum (28)");
        temperatureLine.setLineColor(Color.RED);
        temperatureLine.setLineWidth(2f);
        temperatureleftAxis.addLimitLine(temperatureLine);
        // set maximum value of the graph
        temperatureleftAxis.setAxisMaximum(30f);
        // don't print x axis and x axis in grid
        XAxis temperaturexAxis = temperatureChart.getXAxis();
        temperaturexAxis.setDrawGridLines(false);
        temperaturexAxis.setDrawLabels(false);
        LineDataSet temperatureDataSet = new LineDataSet(temperatureEntry, "Temperature (°C)");
        temperatureDataSet.setDrawFilled(true);
        LineData temperatureData = new LineData(temperatureDataSet);
        temperatureChart.setVisibility(View.VISIBLE);
        temperatureChart.animateY(1000);
        temperatureChart.setData(temperatureData);
        temperatureChart.getDescription().setEnabled(false);
    }

    private void HumidityChart(List<Entry> humidityEntry)
    {
        YAxis humidityleftAxis = humidityChart.getAxisLeft();
        YAxis humidityrightAxis = humidityChart.getAxisRight();
        // don't print right Y axis
        humidityrightAxis.setDrawLabels(false);
        humidityrightAxis.setDrawGridLines(false);
        // Maximum value for a good sleep
        LimitLine humidityLine = new LimitLine(60, "Maximum (60)");
        humidityLine.setLineColor(Color.RED);
        humidityLine.setLineWidth(2f);
        humidityleftAxis.addLimitLine(humidityLine);
        // set maximum value of the graph
        humidityleftAxis.setAxisMaximum(70f);
        // don't print x axis
        XAxis humidityxAxis = humidityChart.getXAxis();
        humidityxAxis.setDrawGridLines(false);
        humidityxAxis.setDrawLabels(false);
        LineDataSet humidityDataSet = new LineDataSet(humidityEntry, "Humidity (%)");
        humidityDataSet.setDrawFilled(true);
        LineData humidityData = new LineData(humidityDataSet);
        humidityChart.setVisibility(View.VISIBLE);
        humidityChart.animateY(1000);
        humidityChart.setData(humidityData);
        humidityChart.getDescription().setEnabled(false);
    }

    private void LightChart(List<Entry> lightEntry)
    {
        YAxis lightleftAxis = lightChart.getAxisLeft();
        YAxis lightrightAxis = lightChart.getAxisRight();
        // don't print right Y axis
        lightrightAxis.setDrawLabels(false);
        lightrightAxis.setDrawGridLines(false);
        // Maximum value for a good sleep
        LimitLine lightLine = new LimitLine(10, "Maximum (10)");
        lightLine.setLineColor(Color.RED);
        lightLine.setLineWidth(2f);
        lightleftAxis.addLimitLine(lightLine);
        // graph doesn't go below zero
        lightleftAxis.setStartAtZero(true);
        XAxis lightxAxis = lightChart.getXAxis();
        lightxAxis.setDrawGridLines(false);
        // set maximum value of the graph
        lightleftAxis.setAxisMaximum(20f);
        // don't print x axis
        lightxAxis.setDrawLabels(false);
        LineDataSet lightDataSet = new LineDataSet(lightEntry, "Luminosity (lx)");
        lightDataSet.setDrawFilled(true);
        LineData lightData = new LineData(lightDataSet);
        lightChart.setVisibility(View.VISIBLE);
        lightChart.animateY(1000);
        lightChart.setData(lightData);
        lightChart.getDescription().setEnabled(false);
    }
}