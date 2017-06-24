package com.example.myosr.bitcointicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    // Member Variables:
    TextView mPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = (TextView) findViewById(R.id.priceLabel);
        final Spinner spinner = (Spinner) findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // TODO: Set an OnItemSelected listener on the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Bitcoin", "" + parent.getItemAtPosition(position));

                // get the bitcoin average from API by JSON
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // link of bitcoin api
                        final String BASE_URL =
                                "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC"
                                        + spinner.getSelectedItem().toString();
                        try {
                            URL url = new URL(BASE_URL);
                            HttpURLConnection connection = (HttpURLConnection)
                                    url.openConnection();
                            InputStreamReader streamReader = new InputStreamReader
                                    (connection.getInputStream());
                            BufferedReader reader = new BufferedReader(streamReader);
                            final StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            String result = stringBuilder.toString();

                            JSONObject jsonObject = new JSONObject(result);
                            final String average =
                                    String.valueOf(jsonObject.getJSONObject
                                            ("averages").getDouble("day"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mPriceTextView.setText(average);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
