package be.vives.student.david.d_rc;

import com.google.gson.annotations.Expose;

/**
 * Created by David on 07/12/15.
 */
public class ThumperStatusReport extends StatusReport{

    @Expose
    private String battery_voltage;

    public String batteryvoltage()
    {
        return battery_voltage;
    }

}
