package be.vives.student.david.d_rc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by David on 23/11/15.
 */
public class NeoPixelString {
    //@Expose = an annotiation tht indicates this member
    //should be exposed for JSON serialization or deserialization
    //@SerializedName annotates the name of the attribute in the json response string

    @SerializedName("string_id")
    @Expose
    private String stringID;


    @SerializedName("number_of_pixels")
    @Expose
    private String numberOfPixels;


    String getStringId()
    {
        return stringID;
    }

    String getNumberOfPixels()
    {
        return numberOfPixels;
    }


}
