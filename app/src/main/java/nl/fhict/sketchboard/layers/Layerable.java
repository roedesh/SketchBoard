package nl.fhict.sketchboard.layers;

import android.graphics.Canvas;
import android.graphics.PointF;

import java.io.Serializable;

import nl.fhict.sketchboard.StableString;

/**
 * Created by ruudschroen on 07-01-16.
 */
public interface Layerable extends Serializable {

    StableString getName();

    float getX();

    float getY();

    void setPosition(float x, float y);

    int getRotationAngle();

    void setRotationAngle(int rotation);

    PointF getRotationCenter();

    void draw(Canvas canvas);



}
