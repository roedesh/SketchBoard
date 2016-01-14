package nl.fhict.sketchboard.layers;

import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by ruudschroen on 14-01-16.
 */
public class DrawingPoint extends PointF {

    private Paint paint;

    public DrawingPoint(float x, float y, Paint paint){
        this.x = x;
        this.y = y;
        this.paint = paint;
    }

    public Paint getPaint(){
        return paint;
    }

}
