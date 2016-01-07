package nl.fhict.sketchboard.layers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;

public class ImageLayer implements Layerable {

    private Context context;
    private Bitmap image;
    private PointF point;

    public ImageLayer(Bitmap image, PointF point){
        this.image = image;
        this.point = point;
    }

    public ImageLayer(Context context, int resourceId) {
        this.image = BitmapFactory.decodeResource(context.getResources(), resourceId);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, point.x, point.y, null);
    }
}
