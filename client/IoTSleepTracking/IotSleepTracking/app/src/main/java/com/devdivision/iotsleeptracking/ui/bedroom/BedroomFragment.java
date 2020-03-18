package com.devdivision.iotsleeptracking.ui.bedroom;

import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class BedroomFragment extends Fragment {

    private BedroomViewModel roomViewModel;
    private TextView currentTemperature;
    private TextView currentHumidity;
    private TextView currentLight;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        roomViewModel =
                ViewModelProviders.of(this).get(BedroomViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bedroom, container, false);
        currentTemperature = root.findViewById(R.id.current_temperature);
        currentHumidity = root.findViewById(R.id.current_humidity);
        currentLight = root.findViewById(R.id.current_light);
        OkHttpClient client = new OkHttpClient();
        String url = "https://iotsleeptracking.herokuapp.com/roomenvironment";
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
                                currentLight.setText("Current light: " + jsonarray.getJSONObject(jsonarray.length()-1).getString("light").toString() + " lx");
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