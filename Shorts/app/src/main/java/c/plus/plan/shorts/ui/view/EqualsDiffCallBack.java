package c.plus.plan.shorts.ui.view;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;

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
        boolean simpleEqual = mOldItems.size() > oldItemPosition && mNewItems.size() > newItemPosition && mOldItems.get(oldItemPosition) != null && mOldItems.get(oldItemPosition).equals(mNewItems.get(newItemPosition));


        if (simpleEqual) {

            if (mOldItems.get(oldItemPosition) instanceof Drama && mNewItems.get(newItemPosition) instanceof Drama) {
                Drama oldDrama = (Drama) mOldItems.get(oldItemPosition);
                Drama newDrama = (Drama) mNewItems.get(newItemPosition);
                return oldDrama.getVid() == newDrama.getVid()
                        && (oldDrama.getName() != null && oldDrama.getCover().equals(newDrama.getName()))
                        && (oldDrama.getCover() != null && oldDrama.getCover().equals(newDrama.getCover()))
                        && (oldDrama.getBanner() != null && oldDrama.getBanner().equals(newDrama.getBanner()));
            }

        }
        return simpleEqual;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T oldItem = mOldItems.get(oldItemPosition);
        T newItem = mNewItems.get(newItemPosition);

        return oldItem.equals(newItem);
    }
}
