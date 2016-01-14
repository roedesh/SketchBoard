package nl.fhict.sketchboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import nl.fhict.sketchboard.utils.RecentWrapper;

/**
 * Created by asror on 8-1-2016.
 */
public class NewBoardRecyclerAdapter extends RecyclerView.Adapter<NewBoardRecyclerAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    ArrayList<Integer> templates = new ArrayList<>();
    private Context context;

    public NewBoardRecyclerAdapter(ArrayList<Integer> templates, Context context) {
        this.templates = templates;
        this.context = context;
    }

    @Override
    public NewBoardRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listnewboard_item, parent, false));
    }




    @Override
    public void onBindViewHolder(NewBoardRecyclerAdapter.ViewHolder holder, int position) {
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                templates.get(position));
        Bitmap rotatedBitmap = Bitmap.createBitmap(icon , 0, 0, icon .getWidth(), icon .getHeight(), matrix, true);

        holder.image.setImageBitmap(rotatedBitmap);

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
                mOnItemClickListener.onItemClick(getAdapterPosition() + "");
            }
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }


    public interface OnItemClickListener {
        void onItemClick(String text);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
