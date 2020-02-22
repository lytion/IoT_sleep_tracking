package com.devdivision.iotsleeptracking.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.devdivision.iotsleeptracking.MainActivity;
import com.devdivision.iotsleeptracking.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView currentTemperature;
    private TextView currentHumidity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        currentTemperature = root.findViewById(R.id.current_temperature);
        currentHumidity = root.findViewById(R.id.current_humidity);
        OkHttpClient client = new OkHttpClient();
        String url = "http://129.12.128.210:3000/thermometer";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("STATE", "failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("STATE", "success");
                    final String myResponse = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonarray = new JSONArray(myResponse);
//                                for (int i = 0; i < jsonarray.length(); i++) {
//                                    JSONObject jsonobject = jsonarray.getJSONObject(i);
////                                    currentTemperature.setText(jsonobject.getString("temperature").toString());
////                                    Log.d("DATA", jsonobject.getString("temperature").toString());
//                                }
                                currentTemperature.setText("Current temperature: " + jsonarray.getJSONObject(jsonarray.length()-1).getString("temperature").toString() + " °C");
                                currentHumidity.setText("Current humidity: " + jsonarray.getJSONObject(jsonarray.length()-1).getString("humidity").toString() + " %");
                            } catch (JSONException e) {

                            }
                        }
                    });
                }
            }
        });
        return root;
    }
}