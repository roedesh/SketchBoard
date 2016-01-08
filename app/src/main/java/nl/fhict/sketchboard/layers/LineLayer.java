package nl.fhict.sketchboard.layers;

import android.graphics.Canvas;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class LineLayer implements Layerable {

    private static String PREFIX = "LineLayer #";
    private static long nextNumber = 1;
    private String name;
    private List<Line> lines = new ArrayList<>();

    public LineLayer(){
        name = PREFIX + nextNumber;
        nextNumber++;
    }

    public void addLine(Line line){
        lines.add(line);
    }

    @Override
    public String getName() {
        return name;
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
