package be.vives.student.david.d_rc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class NeoPixelActivity extends Activity {


    private String base_url; // BAse url must end with a slash!!
    private NeoPixelService service;
    private Retrofit retrofit;
    int iDelay=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_neo_pixel);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initRetrofit();
    }


    private String getStringID()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String stringID = sharedPref.getString(SettingsActivity.PREF_KEY_STRINGID, "DavidL");
        //TextView txtStringID = (TextView) findViewById(R.id.txtStringID);
        //txtStringID.setText(stringID);
        return stringID;

    }


    private void initRetrofit()

    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String serverip = sharedPref.getString(SettingsActivity.PREF_KEY_SERVERIP, "192.168.1.101");
        String servergate = sharedPref.getString(SettingsActivity.PREF_KEY_SERVERPORT, "3000");

        String stringID = getStringID();



        base_url = "http://" + serverip + ":" + servergate + "/"; // http://192.168.1.100:3000/";


        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(NeoPixelService.class);




    }

    public void onGet(View view)
    {

        //TextView txtStringID = (TextView) findViewById(R.id.txtStringID);
        //String stringID = String.valueOf(txtStringID.getText().toString());
        getStringID();
        String stringID = getStringID();
        Call<NeoPixelString> call = service.getNeoPixelStringInfo(stringID);

        // Call enqueue to make an asynchronous request
        call.enqueue(new Callback<NeoPixelString>() {
            // On Android, callbacks will be executed on the main thread
            @Override
            public void onResponse(Response<NeoPixelString> response, Retrofit retrofit) {
                if (response.body() != null) {
                    NeoPixelString str = response.body();
                    Log.i("REST", response.toString());
                    Log.i("REST", "ID = " + str.getStringId() + "\nCOUNT = " + str.getNumberOfPixels());
                    EditText txtNumberOfPixels = (EditText) findViewById(R.id.txtNumberOfPixels);

                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    txtNumberOfPixels.setTextColor(color);

                    txtNumberOfPixels.setText(str.getNumberOfPixels());
                    //((EditText) findViewById(R.id.txtNumberOfPixels)).setText(str.getNumberOfPixels());
                } else {
                    Log.e("REST", "Request returned no data");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("REST", t.toString());
                Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
            }
        });










    }

    public void onPost(View view)
    {

        //TextView txtStringID = (TextView) findViewById(R.id.txtStringID);
        //String stringID = String.valueOf(txtStringID.getText().toString());
        getStringID();
        String stringID = getStringID();


        //EditText txtRed = (EditText) findViewById(R.id.txtRed);
        //EditText txtGreen = (EditText) findViewById(R.id.txtGreen);
        //EditText txtBlue = (EditText) findViewById(R.id.txtBlue);
        //EditText txtDelay = (EditText) findViewById(R.id.txtDelay);
        int iDelay = 0;
        //int iRed = Integer.valueOf(txtRed.getText().toString());
        //int iGreen = Integer.valueOf(txtGreen.getText().toString());
        //int iBlue = Integer.valueOf(txtBlue.getText().toString());

        SeekBar barRed = (SeekBar) findViewById(R.id.barRed);
        SeekBar barBlue = (SeekBar) findViewById(R.id.barBlue);
        SeekBar barGreen = (SeekBar) findViewById(R.id.barGreen);
        SeekBar barDelay = (SeekBar) findViewById(R.id.barDelay);

        int iRed = barRed.getProgress();
        int iGreen = barGreen.getProgress();
        int iBlue = barBlue.getProgress();
        iDelay = barDelay.getProgress();


        // THIS SHIT DOESNT WORK YET
        /*
        String sRed = String.valueOf(txtRed.getText().toString());
        if (sRed.equals(null)) {
            Toast.makeText(this, "You did not enter a red value", Toast.LENGTH_SHORT).show();
            return;
        }
        String sGreen = String.valueOf(txtGreen.getText().toString());
        if (sGreen.matches("")) {
            Toast.makeText(this, "You did not enter a green value", Toast.LENGTH_SHORT).show();
            return;
        }
        String sBlue = String.valueOf(txtBlue.getText().toString());
        if (sBlue.matches("")) {
            Toast.makeText(this, "You did not enter a blue value", Toast.LENGTH_SHORT).show();
            return;
        }
        // END OF SHIT THAT DOESNT WORK
        */


        boolean bool = false;

        if (iRed > 255)
        {
            bool = true;
        }
        if (iGreen > 255)
        {
            bool = true;
        }
        if (iBlue > 255)
        {
            bool = true;
        }

        if (iDelay > 255)
        {
            bool = true;
        }

        if (bool)
        {
            Toast.makeText(this, "Values must be between 0 and 255" , Toast.LENGTH_LONG).show();
        }
        else
        {

            // Do your job

            int color2 = Color.argb(255, iRed, iGreen, iBlue);
            TextView txtColor = (TextView) findViewById(R.id.txtColor);
            txtColor.setBackgroundColor(color2);


            Call<StatusReport> callNeoPixel;
            if (iDelay != 0)
            {


                NeoPixelColorStrobe effect = new NeoPixelColorStrobe(iRed, iGreen, iBlue , iDelay);
                callNeoPixel = service.setNeoPixelStrobe(stringID, effect);
            }
            else
            {


                NeoPixelColorEffect effect = new NeoPixelColorEffect(iRed, iGreen, iBlue);
                callNeoPixel = service.setNeoPixelColor(stringID, effect);
            }



                // Call enqueue to make an asynchronous request
                callNeoPixel.enqueue(new Callback<StatusReport>() {
                    // On Android, callbacks will be executed on the main thread
                    @Override
                    public void onResponse(Response<StatusReport> response, Retrofit retrofit) {
                        if (response.body() != null) {
                            StatusReport status = response.body();
                            //Log.i("REST", response.toString());
                            //Log.i("REST", "Status = " + status.getStringId() + "\nCOUNT = " + str.getNumberOfPixels());
                            //EditText txtNumberOfPixels = (EditText) findViewById(R.id.txtNumberOfPixels);

                            //((EditText) findViewById(R.id.txtNumberOfPixels)).setText(str.getNumberOfPixels());

                            // color = Color.argb(255, iRed, iGreen, iBlue);
                            //TextView txtColor = (TextView) findViewById(R.id.txtColor);
                            //txtColor.setBackgroundColor(color);


                        } else {
                            Log.e("REST", "Request returned no data");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("REST", t.toString());
                        Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                    }
                });






            // End of job

           // int color = Color.argb(255, iRed, iGreen, iBlue);
           // TextView txtColor = (TextView) findViewById(R.id.txtColor);
           // txtColor.setBackgroundColor(color);

        }























    }
}
