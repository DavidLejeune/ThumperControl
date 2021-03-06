package be.vives.student.david.d_rc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    // Main Activity of the app
    // Let's the user navigate to different activities



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                Toast.makeText(this, "Selected settings", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_man:
                Intent man = new Intent(this, ManActivity.class);
                startActivity(man);

                Toast.makeText(this, "Selected manual", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_auto:
                Intent auto = new Intent(this, AutoActivity.class);
                startActivity(auto);

                Toast.makeText(this, "Selected auto", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_neopixel:
                Intent neopixel = new Intent(this, NeoPixelActivity.class);
                startActivity(neopixel);

                Toast.makeText(this, "Selected NeoPixel", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_about:
                //show info activity
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);

                Toast.makeText(this, "Selected about", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAboutClick(View view)
    {
        // Display the About activity
        Intent about = new Intent(this, AboutActivity.class);
        //Toast.makeText(this, "Showing About activity" , Toast.LENGTH_SHORT).show();
        startActivity(about);
    }
    public void onAutoClick(View view)
    {
        // Display the Automatic drive activity
        Intent auto = new Intent(this, AutoActivity.class);
        //Toast.makeText(this, "Showing Auto activity" , Toast.LENGTH_SHORT).show();
        startActivity(auto);
    }
    public void onManClick(View view)
    {
        // Display the Manual drive activity
        Intent man = new Intent(this, ManActivity.class);
        startActivity(man);
    }
    public void onNeopixelClick(View view)
    {
        // Display the Neopixel activity
        Intent neo = new Intent(this, NeoPixelActivity.class);
        startActivity(neo);
    }

    public void onSettingClick(View view)
    {
        // Obsolete, was for when my theme didn't allow the actionbar
        // Theme problem was resolved
        Toast.makeText(this, "You want settings man" , Toast.LENGTH_SHORT).show();
    }

}


