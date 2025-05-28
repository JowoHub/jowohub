package c.plus.plan.skits.ui.adapter;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class EqualsDiffCallBack<T> extends DiffUtil.Callback {
    private List<T> mOldItems, mNewItems;

    public EqualsDiffCallBack(List<T> mOldItems, List<T> mNewItems) {
        this.mOldItems = mOldItems;
        this.mNewItems = mNewItems;
    }

    @Override
    public int getOldListSize() {
        return mOldItems == null ? 0 : mOldItems.size();
    }

    @Override
    public int getNewListSize() {
        return mNewItems == null ? 0 : mNewItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldItems.size() > oldItemPosition && mNewItems.size() > newItemPosition && mOldItems.get(oldItemPosition).equals(mNewItems.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T oldItem = mOldItems.get(oldItemPosition);
        T newItem = mNewItems.get(newItemPosition);

        return oldItem.equals(newItem);
    }}
