package nl.fhict.sketchboard.layers;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.fhict.sketchboard.StableString;

public class TextLayer implements Layerable {

    private float x;
    private float y;

    private static String PREFIX = "TextLayer #";
    private static long nextNumber = 1;
    private StableString name;
    private String text;
    private Paint paint;

    public TextLayer(String text, Paint paint){
        name = new StableString(PREFIX + nextNumber++);
        this.text = text;
        this.paint = paint;

        this.x = 200;
        this.y = 200;
    }

    @Override
    public StableString getName() {
        return name;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(this.text, x, y, this.paint);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);

        out.writeObject(name);
        out.writeObject(text);

        out.writeObject(paint.getAlpha());
        out.writeObject(paint.getColor());
        out.writeObject(paint.getTextSize());
        out.writeObject(paint.getStrokeWidth());
        out.writeObject(paint.getStrokeCap());
        // Missing shit
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        this.x = in.readFloat();
        this.y = in.readFloat();

        this.name = (StableString) in.readObject();
        this.text = (String) in.readObject();

        int alpha = (int) in.readObject();
        int color = (int) in.readObject();
        float size = (float) in.readObject();
        float width = (float) in.readObject();
        Paint.Cap cap = (Paint.Cap) in.readObject();

        this.paint = new Paint();
        this.paint.setAlpha(alpha);
        this.paint.setColor(color);
        this.paint.setTextSize(size);
        this.paint.setStrokeWidth(width);
        this.paint.setStrokeCap(cap);
        // Missing shit
    }
}
