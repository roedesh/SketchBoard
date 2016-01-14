package nl.fhict.sketchboard.layers;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import nl.fhict.sketchboard.StableString;

public class DrawingLayer implements Layerable {

    private static String PREFIX = "DrawingLayer #";
    private static long nextNumber = 1;
    private StableString name;
    private List<DrawingPoint> points = new ArrayList<>();

    public DrawingLayer() {
        name = new StableString(PREFIX + nextNumber);
        nextNumber++;
    }

    public void addPoint(DrawingPoint point) {
        points.add(point);
    }

    @Override
    public StableString getName() {
        return name;
    }

    @Override
    public void draw(Canvas canvas) {
        for (DrawingPoint point : points) {
            canvas.drawPoint(point.x, point.y, point.getPaint());
        }
    }
}
