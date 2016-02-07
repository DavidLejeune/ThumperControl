package be.vives.student.david.d_rc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class AboutActivity extends Activity {
    // About activity
    // Display simple information screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
    }
}
