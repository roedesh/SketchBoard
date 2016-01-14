package nl.fhict.sketchboard;

import android.graphics.Bitmap;

/**
 * Created by Stan on 14-1-2016.
 */
public class BitmapWrapper{
    private String name;
    private Bitmap bitmap;

    public BitmapWrapper(String name, Bitmap bitmap) {
        this.name = name;
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}