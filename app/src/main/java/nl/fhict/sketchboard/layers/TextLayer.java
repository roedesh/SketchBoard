package nl.fhict.sketchboard.layers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class TextLayer implements Layerable {

    private static String PREFIX = "TextLayer #";
    private static long nextNumber = 1;
    private String name;
    private String text;
    private Paint paint;

    public TextLayer(String text){
        name = PREFIX + nextNumber++;
        this.text = text;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
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
}
