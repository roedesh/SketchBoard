package nl.fhict.sketchboard.layers;

import android.graphics.Canvas;

import java.io.Serializable;

import nl.fhict.sketchboard.StableString;

/**
 * Created by ruudschroen on 07-01-16.
 */
public interface Layerable extends Serializable {

    StableString getName();

    void draw(Canvas canvas);



}
