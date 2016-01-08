package nl.fhict.sketchboard.layers;

import android.graphics.Canvas;

import java.io.Serializable;

/**
 * Created by ruudschroen on 07-01-16.
 */
public interface Layerable extends Serializable {

    String getName();

    void draw(Canvas canvas);



}
