package be.vives.student.david.d_rc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MotionEvent;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class ManActivity extends Activity {

    int iLeft;
    int iRight;
    int iUp;
    int iDown;
    int color2;

    private String base_url; // BAse url must end with a slash!!
    private Retrofit retrofit;
    private SpeedService service;
    private NeoPixelService ledService; // Necessary to access NeoPixel while driving


    private static final int MAX_SPEED = 120;       // This should be a setting
    private static final int REFRESH_MS = 250;       // This should be a setting

    private boolean leftIsHeld;
    private boolean rightIsHeld;

    private boolean isStopped;

    private int left_speed;
    private int right_speed;

    private ImageView left_throttle; // Where whe slide our fingers
    private ImageView right_throttle; // Where whe slide our fingers

    private Rect rectLeft;
    private Rect rectRight;

    private int half_range;
    private NeoPixelColorEffect color;

    // A Handler allows you to send and process Message and Runnable objects associated with a thread's MessageQueue.
    private Handler refreshTimer;

    // Represents a command that can be executed. Often used to run code in a different Thread.
    private Runnable thumperControlCode;

    // SparseArrays map integers to Objects. Like HashMap but more performant
    private SparseArray<Point> mActivePointers;

    TextView txtThumperState;
    TextView txtLeftSpeed; // Display left speed
    TextView txtRightSpeed; // Display right speed

    ImageView imgDisplayNeo; // Fixed color display of Neopixel when driving , no delay


    private Response<ThumperStatusReport> responsebattery;
    private String batteryvoltage;

    private class ThumperControlTask implements Runnable {

        @Override
        public void run() {

            // Run method so we will be listening continously for user input (sliding)

            leftIsHeld = false;
            rightIsHeld = false;



            for (int size = mActivePointers.size(), i = 0; i < size; i++) {
                Point point = mActivePointers.valueAt(i);
                if (point != null) {
                    if (rectLeft.contains(point.x, point.y)) {
                        left_speed = calculateSpeed(rectLeft.centerY(), point.y, half_range);

                        leftIsHeld = true;
                    } else if (rectRight.contains(point.x, point.y)) {

                        right_speed = calculateSpeed(rectLeft.centerY(), point.y, half_range);

                        rightIsHeld = true;
                    }
                }
            }



            // We need to do this outside of for because of else clause
            // which cant be determined before the full list of pointers is itterated
            if (leftIsHeld) {
                left_throttle.setBackgroundColor(Color.GREEN);
            } else {
                left_throttle.setBackgroundColor(Color.DKGRAY);
                left_speed = 0;
            }

            if (rightIsHeld) {
                right_throttle.setBackgroundColor(Color.GREEN);
            } else {
                right_throttle.setBackgroundColor(Color.DKGRAY);
                right_speed = 0;
            }
            // Simple text warning that' we are driving
            if (leftIsHeld || rightIsHeld) {
                txtThumperState.setText("Hauling Ass");
                txtThumperState.setTextColor(Color.BLUE);
            } else {
                txtThumperState.setText("Swipe 4 fun");
                txtThumperState.setTextColor(Color.WHITE);
            }

            // display the speed
            txtLeftSpeed.setText(left_speed + "");
            txtRightSpeed.setText(right_speed + "");

            // Check if we can stop sending when user is not touching screen
            if (!isStopped || (left_speed != 0) || (right_speed != 0)) {
                sendThumperSpeed();
                // simple Neopixel color event for reaching certain speeds
                if ((Math.abs(left_speed) + Math.abs(right_speed))/2 < 80)
                {
                    // Fixed color when driving resonably fast
                    sendColorEffect();
                }
                else
                {
                    // Flashing light when driving too fast
                    sendColorStrobe();
                }
            }

            isStopped = (left_speed == 0 && right_speed == 0);
            if (isStopped)
            {
                // Resetting the color after we stopped
                color2 = Color.argb(00, 0, 0, 0);
                imgDisplayNeo.setBackgroundColor(color2);
            }

            // Repeat this same runnable code again every x milliseconds
            refreshTimer.postDelayed(thumperControlCode, REFRESH_MS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_man);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Create the Handler object (on the main thread by default)
        refreshTimer = new Handler();

        // Define the task to be run here
        thumperControlCode = new ThumperControlTask();

        mActivePointers = new SparseArray<Point>();

        // Following some helpful data to have for touch
        // Get location of controls
        // We need to switch left and right because motors are connected wrong
        right_throttle = ((ImageView)findViewById(R.id.imgLeftThrottle));
        left_throttle = ((ImageView)findViewById(R.id.imgRightThrottle));

        txtThumperState = ((TextView)findViewById(R.id.txtSubtitle));

        // We need to switch left and right because motors are connected wrong
        txtRightSpeed = ((TextView)findViewById(R.id.txtSpeedLeft));
        txtLeftSpeed = ((TextView)findViewById(R.id.txtSpeedRight));

        imgDisplayNeo = ((ImageView)findViewById(R.id.imgDisplayNeo));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // We cant do this in the onCreate as the Views have not been placed yet
        int[] l = new int[2];
        left_throttle.getLocationOnScreen(l);
        rectLeft = new Rect(l[0], l[1], l[0] + left_throttle.getWidth(), l[1] + left_throttle.getHeight());

        right_throttle.getLocationOnScreen(l);
        rectRight = new Rect(l[0], l[1], l[0] + right_throttle.getWidth(), l[1] + right_throttle.getHeight());

        half_range = Math.abs(rectLeft.top-rectLeft.bottom)/2;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Removes pending code execution
        refreshTimer.removeCallbacks(thumperControlCode);

        // Kill the Thumper
        left_speed = 0;
        right_speed = 0;
        sendThumperSpeed();
        isStopped = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Needs implementation
        initRetrofit();


        isStopped = true;

        // Schedule code for execution
        refreshTimer.post(thumperControlCode);

    }





    private void initRetrofit()

    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String serverip = sharedPref.getString(SettingsActivity.PREF_KEY_SERVERIP, "");
        String servergate = sharedPref.getString(SettingsActivity.PREF_KEY_SERVERPORT, "");





        base_url = "http://" + serverip + ":" + servergate + "/"; // http://192.168.1.100:3000/";


        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(SpeedService.class);
        ledService = retrofit.create(NeoPixelService.class);




    }



    private int calculateSpeed(float centerY, float yPos, int half_range) {
        return (int)(MAX_SPEED * (centerY - yPos)/half_range);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // We have a new pointer. Lets add it to the list of pointers

                Point point = new Point();
                point.x = (int)event.getX(pointerIndex);
                point.y = (int)event.getY(pointerIndex);
                mActivePointers.put(pointerId, point);
                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    Point point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = (int)event.getX(i);
                        point.y = (int)event.getY(i);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                mActivePointers.remove(pointerId);
                break;
            }
        }

        return true;
    }

    public void sendThumperSpeed() {
        // implementation

        // use speedeffect class to send speed from left and right slider
        SpeedEffect speedEffect = new SpeedEffect(left_speed,right_speed);

        Call<ThumperStatusReport> call = service.setSpeed(speedEffect);

        // Call enqueue to make an asynchronous request
        call.enqueue(new Callback<ThumperStatusReport>() {
            // On Android, callbacks will be executed on the main thread
            @Override
            public void onResponse(Response<ThumperStatusReport> response, Retrofit retrofit) {
                if (response.body() != null) {
                    // Display the batteryvoltage continously as we are driving
                    batteryvoltage = ((ThumperStatusReport) (response.body())).batteryvoltage();
                    TextView txtBatteryVoltage = ((TextView) findViewById(R.id.txtBatteryVoltage));
                    txtBatteryVoltage.setText(batteryvoltage );

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


    public String getStringId(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String stringId = sharedPref.getString(SettingsActivity.PREF_KEY_STRINGID, "");
        return stringId;

    }


    public void sendColorStrobe() {
        String id = getStringId();
        // Here we will flash the light as we drive 'too fast'
        // Neopixel strobe
        NeoPixelColorStrobe color = new NeoPixelColorStrobe(255, 0, 0, 50);
        // Set color of the imageview and throttles to same color as strobe
        color2 = Color.argb(255, 255, 0, 0);
        imgDisplayNeo.setBackgroundColor(color2);
        left_throttle.setBackgroundColor(color2);
        right_throttle.setBackgroundColor(color2);

        Call<StatusReport> callSetStrobe = ledService.setNeoPixelStrobe(id, color);

        callSetStrobe.enqueue(new Callback<StatusReport>() {
            @Override
            public void onResponse(Response<StatusReport> response, Retrofit retrofit) {
                if (response.body() != null) {
                    StatusReport status = response.body();
                    Log.i("REST", response.toString());
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

    public void sendColorEffect() {

        String id = getStringId();
        // Here we will send a fixed neopixel color for certain speeds
        // Neopixel color efect

        // Send blue above 50
        if ((Math.abs(left_speed) + Math.abs(right_speed))/2 > 50)
        {

            color = new NeoPixelColorEffect(0, 0, 255);
            color2 = Color.BLUE;
            imgDisplayNeo.setBackgroundColor(color2);

            left_throttle.setBackgroundColor(color2);
            right_throttle.setBackgroundColor(color2);

        }
        // Send Green-Blue when between 50 and 20
        else if (((Math.abs(left_speed) + Math.abs(right_speed))/2 < 50) && ((Math.abs(left_speed) + Math.abs(right_speed))/2 > 20))
        {

            color = new NeoPixelColorEffect(0, 255, 255);
            color2 = Color.argb(255, 0, 255, 255);
            imgDisplayNeo.setBackgroundColor(color2);
            left_throttle.setBackgroundColor(color2);
            right_throttle.setBackgroundColor(color2);

        }
        // Send Green for lowest speed
        else if ((Math.abs(left_speed) + Math.abs(right_speed))/2 < 20)
        {

            color = new NeoPixelColorEffect(0, 255, 0);
            color2 = Color.argb(255, 0, 255, 0);
            imgDisplayNeo.setBackgroundColor(color2);
            left_throttle.setBackgroundColor(color2);
            right_throttle.setBackgroundColor(color2);
        }


        Call<StatusReport> callSetColor = ledService.setNeoPixelColor(id, color);
        callSetColor.enqueue(new Callback<StatusReport>() {
            @Override
            public void onResponse(Response<StatusReport> response, Retrofit retrofit) {
                if (response.body() != null) {
                    StatusReport status = response.body();
                    Log.i("REST", response.toString());
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

}
