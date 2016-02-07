package be.vives.student.david.d_rc;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by David on 01/12/15.
 */
public interface SpeedService {

    // interface to make the thumper drive at certain speed
    @POST("/speed")
    Call<ThumperStatusReport> setSpeed(@Body SpeedEffect effect);


}
