package c.plus.plan.shorts.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.ItemEpisodesBinding;
import c.plus.plan.shorts.manager.VipManager;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {
    public final static int PAGE_COUNT = 30;
    private int episodeCount;
    private int freeCount;
    private List<Integer> unlockNums;
    private int page;
    private int currentNum;

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public void setFreeCount(int freeCount) {
        this.freeCount = freeCount;
    }

    public void setUnlockNums(List<Integer> unlockNums) {
        this.unlockNums = unlockNums;
    }

    private EpisodesAdapter.OnItemClickListener onItemClickListener;

    public void setPage(int page) {
        this.page = page;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }

    public void setOnItemClickListener(EpisodesAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public EpisodesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEpisodesBinding binding = ItemEpisodesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesAdapter.ViewHolder holder, int position) {
        int num = (position + 1) + (page - 1)*PAGE_COUNT;
        holder.binding.num.setText(String.valueOf(num));
        if(isUnLock(num)){
            holder.binding.lock.setVisibility(View.GONE);
        } else {
            holder.binding.lock.setVisibility(View.VISIBLE);
        }

        if(num == currentNum){
            holder.binding.getRoot().setBackgroundResource(R.drawable.bg_item_episodes_s);
        } else {
            holder.binding.getRoot().setBackgroundResource(R.drawable.bg_item_episodes);
        }

        holder.binding.getRoot().setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.click(num, !isUnLock(num));
            }
        });
    }

    private boolean isUnLock(int num) {
        return num <= freeCount || (unlockNums != null && unlockNums.contains(num)) || VipManager.isVip();
    }

    @Override
    public int getItemCount() {
        return episodeCount;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemEpisodesBinding binding;
        public ViewHolder(@NonNull ItemEpisodesBinding item) {
            super(item.getRoot());
            binding = item;

        }
    }

    public interface OnItemClickListener {
        void click(int num, boolean isLock);
    }
}
