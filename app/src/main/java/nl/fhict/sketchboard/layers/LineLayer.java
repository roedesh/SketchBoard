package nl.fhict.sketchboard.layers;

import android.graphics.Canvas;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class LineLayer implements Layerable {

    private List<Line> lines = new ArrayList<>();

    public void addLine(Line line){
        lines.add(line);
    }

    @Override
    public void draw(Canvas canvas) {
        for(Line line : lines){
            for (PointF point : line.getPoints()){
                canvas.drawPoint(point.x, point.y, line.getPaint());
            }
        }
    }

}
