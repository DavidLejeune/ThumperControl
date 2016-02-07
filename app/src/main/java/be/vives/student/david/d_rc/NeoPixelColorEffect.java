package be.vives.student.david.d_rc;

/**
 * Created by David on 23/11/15.
 */
public class NeoPixelColorEffect {
// Fixed color effect
    private int green;
    private int red;
    private int blue;

    NeoPixelColorEffect(int iRed, int iGreen, int iBlue)
    {
        this.red = iRed;
        this.blue = iBlue;
        this.green = iGreen;
    }

    NeoPixelColorEffect()
    {
        this(0,0,0);
    }



}
