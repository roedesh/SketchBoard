package nl.fhict.sketchboard.layers;

import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruudschroen on 07-01-16.
 */
public class Line {

    private List<PointF> points;
    private final Paint paint;

    public Line(Paint paint) {
        points = new ArrayList<>();
        this.paint = paint;
    }

    public void addPoint(PointF point){
        points.add(point);
    }

    public List<PointF> getPoints() {
        return points;
    }

    public Paint getPaint() {
        return paint;
    }

}
