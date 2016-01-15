package nl.fhict.sketchboard.layers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.fhict.sketchboard.StableString;

public class ImageLayer extends BaseLayer {

    //private float x;
    //private float y;

    private static String PREFIX = "ImageLayer #";
    private static long nextNumber = 1;
    private StableString name;
    private Bitmap originalImage;
    private Bitmap image;
    private float scale;
    private float initialWidth, initialHeight;

    public ImageLayer(Bitmap image, PointF point){
        this.name = new StableString(PREFIX + nextNumber++);
        this.image = originalImage = image;
        setPosition(point.x, point.y);
        scale = 1f;
        initialWidth = image.getWidth();
        initialHeight = image.getHeight();
        //this.x = point.x;
        //this.y = point.y;
    }

    public Bitmap getImage() {
        return image;
    }

    @Override
    public StableString getName() {
        return name;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;

        image = Bitmap.createScaledBitmap(originalImage, (int)Math.ceil(initialWidth * scale),
                (int)Math.ceil(initialHeight * scale), false);
    }

    @Override
    public void draw(Canvas canvas) {
        //canvas.drawBitmap(image, x, y, null);
        canvas.drawBitmap(image, getX(), getY(), null);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(name);

        out.writeFloat(getX());
        out.writeFloat(getY());
        //out.writeFloat(x);
        //out.writeFloat(y);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        this.image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();

        out.writeObject(imageByteArray);
    }


    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        this.name = (StableString) in.readObject();

        final float x = in.readFloat();
        final float y = in.readFloat();
        setPosition(x, y);
        //this.x = in.readFloat();
        //this.y = in.readFloat();

        byte[] imageByteArray = (byte[]) in.readObject();
        this.image = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }

    @Override
    public PointF getRotationCenter() {
        return new PointF(getX() + image.getWidth() / 2, getY() + image.getHeight() / 2);
    }
}
