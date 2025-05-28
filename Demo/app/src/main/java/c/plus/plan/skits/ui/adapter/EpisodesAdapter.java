package c.plus.plan.skits.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import c.plus.plan.skits.databinding.ItemEpisodesBinding;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {
    private int episodeCount;
    private int freeCount;
    private List<Integer> unlockNums;

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public void setFreeCount(int freeCount) {
        this.freeCount = freeCount;
    }

    public void setUnlockNums(List<Integer> unlockNums) {
        this.unlockNums = unlockNums;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEpisodesBinding binding = ItemEpisodesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.num.setText(String.valueOf(position + 1));
        if(isUnLock(position + 1)){
            holder.binding.lock.setVisibility(View.GONE);
        } else {
            holder.binding.lock.setVisibility(View.VISIBLE);
        }

        holder.binding.getRoot().setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.click(position + 1, !isUnLock(position + 1));
            }
        });
    }

    private boolean isUnLock(int num) {
        return num <= freeCount || (unlockNums != null && unlockNums.contains(num));
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
