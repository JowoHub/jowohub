package c.plus.plan.shorts.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.DramaHistory;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.ItemCollectBinding;
import c.plus.plan.shorts.databinding.ItemHistoryVerticalBinding;

public class HistoryVerticalAdapter  extends RecyclerView.Adapter<HistoryVerticalAdapter.ViewHolder> {
    private List<DramaHistory> dramaHistories;
    private boolean edit;

    public void setDramaHistories(List<DramaHistory> dramaHistories) {
        this.dramaHistories = dramaHistories;
    }

    private HistoryVerticalAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(HistoryVerticalAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public HistoryVerticalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryVerticalBinding binding = ItemHistoryVerticalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryVerticalAdapter.ViewHolder holder, int position) {
        DramaHistory item = dramaHistories.get(position);
        Glide.with(holder.binding.iv).load(item.getDrama().getCover())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.iv);
        holder.binding.name.setText(item.getDrama().getName());

        holder.binding.time.setText(TimeUtils.millis2String(item.getTimeUpdate()));

        if(edit){
            holder.binding.rb.setVisibility(ViewGroup.VISIBLE);
            holder.binding.menu.setVisibility(ViewGroup.GONE);
        } else {
            holder.binding.menu.setVisibility(ViewGroup.VISIBLE);
            holder.binding.rb.setVisibility(ViewGroup.GONE);
        }

        holder.binding.getRoot().setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.click(position, item);
            }
        });

        holder.binding.menu.setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.menu(position, item);
            }
        });

        holder.binding.rb.setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.checked(position, item, holder.binding.rb.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dramaHistories == null ? 0 : dramaHistories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemHistoryVerticalBinding binding;
        public ViewHolder(@NonNull ItemHistoryVerticalBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, DramaHistory item);
        void menu(int position, DramaHistory item);
        void checked(int position, DramaHistory item, boolean isChecked);
    }
}
