package be.vives.student.david.d_rc;

/**
 * Created by David on 01/12/15.
 */
public class SpeedEffect {

    int left_speed;
    int right_speed;


    SpeedEffect(int iLeft, int iRight)
    {
        this.left_speed = iLeft;
        this.right_speed = iRight;
    }

    SpeedEffect()
    {
        this(0,0);
    }



}
