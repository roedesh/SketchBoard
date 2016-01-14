package nl.fhict.sketchboard.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import nl.fhict.sketchboard.layers.Layerable;

/**
 * Created by asror on 14-1-2016.
 */
public class RecentWrapper implements Serializable {

    private List<Layerable> recents;
    private Bitmap recentmap;

    public RecentWrapper(List<Layerable> recents, Bitmap recentmap) {
        this.recents = recents;
        this.recentmap = recentmap;
    }

    public List<Layerable> getRecents() {
        return recents;
    }

    public Bitmap getRecentmap() {
        return recentmap;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        this.recentmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();

        out.writeObject(imageByteArray);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        byte[] imageByteArray = (byte[]) in.readObject();
        this.recentmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }
}
