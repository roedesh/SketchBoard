package nl.fhict.sketchboard.layers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;

public class ImageLayer implements Layerable {

    private static String PREFIX = "ImageLayer #";
    private static long nextNumber = 1;
    private String name;
    private Context context;
    private Bitmap image;
    private PointF point;

    public ImageLayer(long id, Bitmap image, PointF point){
        this.name = PREFIX + 1;
        this.image = image;
        this.point = point;
        nextNumber++;
    }

    public ImageLayer(Context context, int resourceId) {
        this.name = PREFIX + 1;
        this.image = BitmapFactory.decodeResource(context.getResources(), resourceId);
        nextNumber++;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, point.x, point.y, null);
    }
}
