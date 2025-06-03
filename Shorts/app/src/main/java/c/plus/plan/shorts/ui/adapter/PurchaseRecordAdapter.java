package c.plus.plan.shorts.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.TimeUtils;

import java.util.List;

import c.plus.plan.shorts.databinding.ItemPurchaseRecordBinding;
import c.plus.plan.shorts.entity.PurchaseProduct;
import c.plus.plan.shorts.entity.PurchaseRecord;

public class PurchaseRecordAdapter extends RecyclerView.Adapter<PurchaseRecordAdapter.ViewHolder> {
    private List<PurchaseRecord> purchaseRecords;

    public void setPurchaseRecords(List<PurchaseRecord> purchaseRecords) {
        this.purchaseRecords = purchaseRecords;
    }

    private PurchaseRecordAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(PurchaseRecordAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PurchaseRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPurchaseRecordBinding binding = ItemPurchaseRecordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        PurchaseRecordAdapter.ViewHolder viewHolder= new PurchaseRecordAdapter.ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseRecordAdapter.ViewHolder holder, int position) {
        PurchaseRecord item = purchaseRecords.get(position);
        holder.binding.coin.setText(String.valueOf(item.getCoin()));
        holder.binding.time.setText(TimeUtils.millis2String(item.getTime()));
        holder.binding.price.setText(item.getMoneyLabel());
    }

    @Override
    public int getItemCount() {
        return purchaseRecords == null ? 0 : purchaseRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemPurchaseRecordBinding binding;
        public ViewHolder(@NonNull ItemPurchaseRecordBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, PurchaseProduct item);
    }
}
