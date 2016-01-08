package nl.fhict.sketchboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;

/**
 * This class is a custom View which can be drawn on.
 * Contains a Paint object, Bitmap to create the canvas with, Canvas to draw on, a brush radius that
 * determines how large the spraying area is and the amount of dots to draw at a time.
 */
public class DrawingView extends View {

    /**
     * Used for keeping track of finger movements.
     */
    private Path drawPath;

    /**
     * Paint objects which contain information like color, stroke width, etc.
     */
    private Paint drawPaint, canvasPaint;

    /**
     * Canvas for drawing.
     */
    private Canvas drawCanvas;

    /**
     * Bitmap to be used with the canvas.
     */
    private Bitmap canvasBitmap;

    /**
     * Initial paint color.
     */
    private int paintColor = 0xFF660000;

    /**
     * Initial stroke width.
     */
    private int strokeWidth = 20;

    private boolean eraserMode = false;

    /**
     * Creates a new instance of DrawingView. Requires a Context object.
     *
     * @param context The context in which this view is used
     */
    public DrawingView(Context context) {
        super(context);
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(strokeWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    /**
     * This method will be executed whenever the screen size changes.
     *
     * @param w    New width
     * @param h    New height
     * @param oldW Old width
     * @param oldH Old height
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    /**
     * This method will be called after invalidate()
     *
     * @param canvas Canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }
    
    /**
     * Sets the stroke width.
     *
     * @param strokeWidth Width to set
     */
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        drawPaint.setStrokeWidth(this.strokeWidth);
    }

    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
        this.drawPaint.setColor(paintColor);
    }

    public int getPaintColor() {
        return this.paintColor;
    }

    /**
     * This method returns a base64 string representation of the Bitmap data.
     *
     * @return Base64 string
     */
    public String getBase64String() {
        Bitmap bm = this.canvasBitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.NO_WRAP);
        return imageEncoded;
    }

    /**
     * Sets the Xfermode of drawCanvas. Takes a boolean argument.
     * If value is true, set Xfermode to CLEAR (this means erase), else set to null.
     */
    public void setEraserMode(boolean value) {
        if (value) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            eraserMode = true;
        } else {
            drawPaint.setXfermode(null);
            eraserMode = false;
        }
    }

    public Canvas getCanvas(){
        return this.drawCanvas;
    }

    public Paint getDrawPaint(){
        return drawPaint;
    }


    public boolean isInEraserMode() {
        return eraserMode;
    }

}
