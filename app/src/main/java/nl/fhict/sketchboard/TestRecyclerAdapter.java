package nl.fhict.sketchboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;

public class TestRecyclerAdapter extends RecyclerView.Adapter<TestRecyclerAdapter.ViewHolder> {

    private ArrayList<BitmapWrapper> recents;

    private OnItemClickListener mOnItemClickListener;
    private Context context;

    public TestRecyclerAdapter(ArrayList<BitmapWrapper> bitmaps, Context context) {
        this.recents = bitmaps;
        this.context = context;
    }

    @Override
    public TestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_test, parent, false));
    }

    @Override
    public void onBindViewHolder(TestRecyclerAdapter.ViewHolder holder, int position) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap b = recents.get(position).getBitmap();
        Bitmap rotatedBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Bitmap resizedbitmap = getResizedBitmap(rotatedBitmap, height, width);

        holder.image.setImageBitmap(resizedbitmap);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView image;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView)view;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(recents.get(getAdapterPosition()).getName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return recents.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String text);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        Bitmap b = Bitmap.createScaledBitmap(bm, newWidth, newHeight /4, true);
        return b;
    }
}