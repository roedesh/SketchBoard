package nl.fhict.sketchboard.layers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TextLayer implements Layerable {

    private static String PREFIX = "TextLayer #";
    private static long nextNumber = 1;
    private String name;
    private String text;
    private Paint paint;

    public TextLayer(String text, Paint paint){
        name = PREFIX + nextNumber++;
        this.text = text;
        this.paint = paint;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(this.text, 10, 10, this.paint);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(name);
        out.writeObject(text);

        out.writeObject(paint.getAlpha());
        out.writeObject(paint.getColor());
        // Missing shit
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        this.name = (String) in.readObject();
        this.text = (String) in.readObject();

        int alpha = (int) in.readObject();
        int color = (int) in.readObject();

        this.paint = new Paint();
        this.paint.setAlpha(alpha);
        this.paint.setColor(color);
        // Missing shit
    }
}
