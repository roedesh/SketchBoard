package nl.fhict.sketchboard.layers;

import android.graphics.Canvas;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import nl.fhict.sketchboard.StableString;

public class DrawingLayer extends BaseLayer {

    private static String PREFIX = "DrawingLayer #";
    private static long nextNumber = 1;
    private StableString name;
    private List<DrawingPoint> points = new ArrayList<>();
    private transient PointF mRotationCenter;

    public DrawingLayer() {
        name = new StableString(PREFIX + nextNumber);
        nextNumber++;
    }

    public void addPoint(DrawingPoint point) {
        //points.add(point);
        points.add(new DrawingPoint(point.getX() - getX(), point.getY() - getY(), point.getPaint()));

        final DrawingPoint initialValues = points.get(0);
        float minX = initialValues.getX(), minY = initialValues.getY(),
                maxX = initialValues.getX(), maxY = initialValues.getY();
        for (DrawingPoint dp : points) {
            if (dp.getX() < minX) {
                minX = dp.getX();
            } else if (dp.getX() > maxX) {
                maxX = dp.getX();
            }
            if (dp.getY() < minY) {
                minY = dp.getY();
            } else if (dp.getY() > maxY) {
                maxY = dp.getY();
            }
        }
        mRotationCenter = new PointF((maxX - minX) / 2 + minX, (maxY - minY) / 2 + minY);
    }

    @Override
    public StableString getName() {
        return name;
    }

    @Override
    public void draw(Canvas canvas) {
        DrawingPoint previousPoint = null;
        for (DrawingPoint point : points) {
            canvas.drawPoint(point.getX() + getX(), point.getY() + getY(), point.getPaint());

            if (previousPoint != null &&
                    previousPoint.getPaint().getColor() == point.getPaint().getColor() &&
                    previousPoint.getPaint().getStrokeWidth() == point.getPaint().getStrokeWidth()) {
                canvas.drawLine(previousPoint.getX(), previousPoint.getY(),
                        point.getX(), point.getY(), point.getPaint());
            }

            previousPoint = point;
        }
    }

    @Override
    public PointF getRotationCenter() {
        return mRotationCenter;
    }
}
