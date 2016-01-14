package nl.fhict.sketchboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by asror on 8-1-2016.
 */
public class NewBoardRecyclerAdapter extends RecyclerView.Adapter<NewBoardRecyclerAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    ArrayList<Integer> templates = new ArrayList<>();

    public NewBoardRecyclerAdapter(ArrayList<Integer> templates) {
        this.templates = templates;
    }

    @Override
    public NewBoardRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listnewboard_item, parent, false));
    }




    @Override
    public void onBindViewHolder(NewBoardRecyclerAdapter.ViewHolder holder, int position) {

        holder.image.setBackgroundResource(templates.get(position));

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
