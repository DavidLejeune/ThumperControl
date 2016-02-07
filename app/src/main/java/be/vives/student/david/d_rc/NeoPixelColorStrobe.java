package be.vives.student.david.d_rc;

/**
 * Created by David on 01/12/15.
 */
public class NeoPixelColorStrobe {

// Flashing color effect

    private int green;
    private int red;
    private int blue;
    private int delay;

    NeoPixelColorStrobe(int iRed, int iGreen, int iBlue, int iDelay)
    {
        this.red = iRed;
        this.blue = iBlue;
        this.green = iGreen;
        this.delay = iDelay;
    }

    NeoPixelColorStrobe()
    {
        this(0,0,0,0);
    }




}
