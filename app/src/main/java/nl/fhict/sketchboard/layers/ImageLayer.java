package nl.fhict.sketchboard.layers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ImageLayer implements Layerable {

    private static String PREFIX = "ImageLayer #";
    private static long nextNumber = 1;
    private String name;
    private Bitmap image;
    private PointF point;

    public ImageLayer(Bitmap image, PointF point){
        this.name = PREFIX + nextNumber++;
        this.image = image;
        this.point = point;
    }

    public ImageLayer(Context context, int resourceId) {
        this.name = PREFIX + nextNumber++;
        this.image = BitmapFactory.decodeResource(context.getResources(), resourceId);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, point.x, point.y, null);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(name);

        out.writeFloat(point.x);
        out.writeFloat(point.y);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        this.image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();

        out.writeObject(imageByteArray);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        this.name = (String) in.readObject();

        float x = in.readFloat();
        float y = in.readFloat();
        this.point = new PointF(x, y);

        byte[] imageByteArray = (byte[]) in.readObject();
        this.image = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }
}
