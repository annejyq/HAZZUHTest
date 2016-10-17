package com.example.anne.hazzuhtest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView ack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button DisplayOn = (Button) findViewById(R.id.DisplayOn);
        Button DisplayOff = (Button) findViewById(R.id.DisplayOff);
        Button Show = (Button) findViewById(R.id.Command);

        ack = (TextView) findViewById(R.id.Acknowledge);

        DisplayOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendHuzzah().execute("turn on");
            }

        });

        DisplayOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendHuzzah().execute("turn off");
            }
        });

        Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendHuzzah().execute("Show Command");
            }
        });
    }


    class SendHuzzah extends AsyncTask<String, Void, String> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected String doInBackground(String...command) {
            String[] SendCommand = command;

            String AckResult;

            String url = "http://53f243dd.ngrok.io";

            JSONObject comm = new JSONObject();

            try {
                URL obj = new URL(url);
                HttpURLConnection http = (HttpURLConnection) obj.openConnection();
                http.setConnectTimeout(20000);
                http.setDoInput(true);
                http.setDoOutput(true);
                http.setRequestMethod("POST");
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Host","53f243dd.ngrok.io");


                comm.put("api_command", SendCommand[0]);

                OutputStream os = http.getOutputStream();
                os.write(comm.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
                http.connect();

                int responseCode = http.getResponseCode();
                System.out.println("Response Code : " + responseCode);
                StringBuilder response = new StringBuilder();

                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(http.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    AckResult = "Success";

                } else {
                    AckResult = "Error";
                }

                return AckResult;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        protected void onPostExecute(String AckResult) {
            ack.append(AckResult);

        }
    }
}
