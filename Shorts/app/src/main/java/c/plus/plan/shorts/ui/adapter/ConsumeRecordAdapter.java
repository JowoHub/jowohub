package c.plus.plan.shorts.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;

import java.util.List;

import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.ItemConsumeRecordBinding;
import c.plus.plan.shorts.entity.PurchaseProduct;
import c.plus.plan.shorts.entity.UnlockDramaRecord;

public class ConsumeRecordAdapter extends RecyclerView.Adapter<ConsumeRecordAdapter.ViewHolder> {
    private List<UnlockDramaRecord> unlockDramaRecords;

    public void setUnlockDramaRecords(List<UnlockDramaRecord> unlockDramaRecords) {
        this.unlockDramaRecords = unlockDramaRecords;
    }

    private ConsumeRecordAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(ConsumeRecordAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ConsumeRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConsumeRecordBinding binding = ItemConsumeRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ConsumeRecordAdapter.ViewHolder viewHolder= new ConsumeRecordAdapter.ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConsumeRecordAdapter.ViewHolder holder, int position) {
        UnlockDramaRecord item = unlockDramaRecords.get(position);
        Glide.with(holder.binding.getRoot()).load(item.getDramaCover()).into(holder.binding.iv);
        holder.binding.title.setText(item.getDramaName());
        holder.binding.num.setText(String.format(Utils.getApp().getResources().getString(R.string.dram_num), item.getDramaEpisodeNum()) + " " + TimeUtils.millis2String(item.getTime(), "yyyy-MM-dd"));
        holder.binding.coin.setText(String.valueOf(item.getCoin()));
    }

    @Override
    public int getItemCount() {
        return unlockDramaRecords == null ? 0 : unlockDramaRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemConsumeRecordBinding binding;
        public ViewHolder(@NonNull ItemConsumeRecordBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, PurchaseProduct item);
    }
}
