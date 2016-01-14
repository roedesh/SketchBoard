package nl.fhict.sketchboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class TestRecyclerAdapter extends RecyclerView.Adapter<TestRecyclerAdapter.ViewHolder> {

    private ArrayList<BitmapWrapper> recents;

    private OnItemClickListener mOnItemClickListener;

    public TestRecyclerAdapter(ArrayList<BitmapWrapper> bitmaps) {
        recents = bitmaps;
    }

    @Override
    public TestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_test, parent, false));
    }

    @Override
    public void onBindViewHolder(TestRecyclerAdapter.ViewHolder holder, int position) {
        holder.image.setImageBitmap(recents.get(position).getBitmap());
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
}