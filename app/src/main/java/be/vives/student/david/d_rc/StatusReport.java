package be.vives.student.david.d_rc;

import com.google.gson.annotations.Expose;

/**
 * Created by David on 23/11/15.
 */
public class StatusReport {
    // this will help us post and retrieve all we need
    @Expose
    private String status;

    public String getStatus()
    {
        return status;
    }

}
