package nl.fhict.sketchboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestRecyclerAdapter extends RecyclerView.Adapter<TestRecyclerAdapter.ViewHolder> {
    private final String ITEM_TEXT;

    private OnItemClickListener mOnItemClickListener;

    public TestRecyclerAdapter(String text) {
        ITEM_TEXT = text + " %d";
    }

    @Override
    public TestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_test, parent, false));
    }

    @Override
    public void onBindViewHolder(TestRecyclerAdapter.ViewHolder holder, int position) {
        holder.text.setText(String.format(ITEM_TEXT, position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView text;

        public ViewHolder(View view) {
            super(view);
            text = (TextView)view;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(String.format(ITEM_TEXT, getAdapterPosition()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return 25;
    }

    public interface OnItemClickListener {
        void onItemClick(String text);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}