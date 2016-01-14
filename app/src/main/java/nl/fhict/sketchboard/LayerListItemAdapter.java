/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package nl.fhict.sketchboard;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.List;

import nl.fhict.sketchboard.layers.Layerable;
import nl.fhict.sketchboard.utils.DrawableUtils;
import nl.fhict.sketchboard.utils.ViewUtils;

public class LayerListItemAdapter
        extends RecyclerView.Adapter<LayerListItemAdapter.MyViewHolder>
        implements DraggableItemAdapter<LayerListItemAdapter.MyViewHolder> {
    private static final String TAG = "LayerListItemAdapter";

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    private List<Layerable> layerableList;
    private static OnItemClickListener mOnItemClickListener;
    private Fragment parentFragment;

    public static class MyViewHolder extends AbstractDraggableItemViewHolder implements View.OnClickListener {
        public FrameLayout container;
        public View dragHandle;
        public TextView textView;

        public MyViewHolder(View v) {
            super(v);
            container = (FrameLayout) v.findViewById(R.id.container);
            dragHandle = v.findViewById(R.id.drag_handle);
            textView = (TextView) v.findViewById(R.id.text1);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public LayerListItemAdapter(List<Layerable> layerableList, Fragment fragment) {
        this.layerableList = layerableList;
        this.parentFragment = fragment;

        mOnItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ((CompositionActivity)parentFragment.getActivity()).setActiveLayer(position);
            }
        };

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_draggable, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Layerable layer = layerableList.get(position);

        // set text
        holder.textView.setText(layer.getName().getValue());

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.container.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.container.setBackgroundResource(bgResId);
        }
    }

    @Override
    public long getItemId(int position) {
        return layerableList.get(position).getName().getId();
    }

    @Override
    public int getItemCount() {
        return layerableList.size();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        final Layerable item = layerableList.remove(fromPosition);
        layerableList.add(toPosition, item);


        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.container;
        final View dragHandleView = holder.dragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public boolean isOnItemClickListenerNull() {
        return mOnItemClickListener == null;
    }
}