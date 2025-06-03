package c.plus.plan.shorts.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import c.plus.plan.jowosdk.entity.DramaHistory;
import c.plus.plan.shorts.databinding.ItemHistoryBinding;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<DramaHistory> dramaHistories;

    public void setDramaHistories(List<DramaHistory> dramaHistories) {
        this.dramaHistories = dramaHistories;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        DramaHistory item = dramaHistories.get(position);
        String bannerUrl = item.getDrama().getBanner();
        if (bannerUrl != null && !bannerUrl.startsWith("https://")) {
            String host = Uri.parse(item.getDrama().getCover()).getHost();
            LogUtils.d("host:" + host);
            bannerUrl = "https://" + host +"/"+ bannerUrl;
        }
        if (bannerUrl == null) {
            bannerUrl = item.getDrama().getCover();
        }
        Glide.with(holder.binding.iv).load(bannerUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.iv);
        holder.binding.tv.setText(item.getDrama().getName());

        holder.binding.getRoot().setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.click(position, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dramaHistories == null ? 0 : dramaHistories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemHistoryBinding binding;
        public ViewHolder(@NonNull ItemHistoryBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, DramaHistory item);
    }
}
