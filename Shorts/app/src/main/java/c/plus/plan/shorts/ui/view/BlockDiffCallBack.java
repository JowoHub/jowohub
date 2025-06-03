package c.plus.plan.shorts.ui.view;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import c.plus.plan.jowosdk.entity.Block;
import c.plus.plan.jowosdk.entity.Block;

public class BlockDiffCallBack<T> extends DiffUtil.Callback {
    private List<T> mOldItems, mNewItems;

    public BlockDiffCallBack(List<T> mOldItems, List<T> mNewItems) {
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

            if (mOldItems.get(oldItemPosition) instanceof Block && mNewItems.get(newItemPosition) instanceof Block) {
                Block oldDrama = (Block) mOldItems.get(oldItemPosition);
                Block newDrama = (Block) mNewItems.get(newItemPosition);
                return oldDrama.getSort() == newDrama.getSort()
                        && (oldDrama.getName() != null && oldDrama.getName().equals(newDrama.getName()))
                        && (oldDrama.getBlockKey() != null && oldDrama.getBlockKey().equals(newDrama.getBlockKey()));
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
