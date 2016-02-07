package be.vives.student.david.d_rc;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by David on 23/11/15.
 */
public interface NeoPixelService {

    // Interface for NeoPixel

    //Do NOT start path with slash!!

    //receive neopixel info
    @GET("neopixels/strings/{id}")
    Call<NeoPixelString> getNeoPixelStringInfo(@Path("id") String stringID);


    //post fixed color
    @POST("neopixels/strings/{id}")
    Call<StatusReport> setNeoPixelColor(@Path("id") String stringID, @Body NeoPixelColorEffect effect);

    //post color strobe
    @POST ("neopixels/effects/strobe/{id}")
    Call<StatusReport> setNeoPixelStrobe(@Path("id") String stringID, @Body NeoPixelColorStrobe effect);




}


