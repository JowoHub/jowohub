package c.plus.plan.shorts.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.Utils;

import java.util.List;

import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.ItemPurchaseProductBinding;
import c.plus.plan.shorts.entity.PurchaseProduct;

public class PurchaseProductAdapter extends RecyclerView.Adapter<PurchaseProductAdapter.ViewHolder> {
    private List<PurchaseProduct> purchaseProducts;

    public void setPurchaseProducts(List<PurchaseProduct> purchaseProducts) {
        this.purchaseProducts = purchaseProducts;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PurchaseProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPurchaseProductBinding binding = ItemPurchaseProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseProductAdapter.ViewHolder holder, int position) {
        PurchaseProduct item = purchaseProducts.get(position);

        try {
            String unit = item.getPrice().replaceAll("\\d", "").replaceAll("\\.", "");
            String price = item.getPrice().substring(unit.length());
            holder.binding.price.setText(price);
            holder.binding.unit.setText(unit);
        } catch (Exception e){
            holder.binding.price.setText(item.getPrice());
        }

        holder.binding.coin.setText(item.getCoin() + " " + Utils.getApp().getResources().getString(R.string.coin));

        if(item.getBenefit() != null){
            holder.binding.ll.setVisibility(ViewGroup.VISIBLE);
            holder.binding.benefitLabel.setText(item.getBenefit().getLabel());
            holder.binding.benefitCoin.setText("+" + item.getBenefit().getCoin() + " " + Utils.getApp().getResources().getString(R.string.coin));
        } else {
            holder.binding.benefitLabel.setText(null);
            holder.binding.benefitCoin.setText(null);
            holder.binding.ll.setVisibility(ViewGroup.GONE);
        }

        holder.binding.btn.setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.click(position, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return purchaseProducts == null ? 0 : purchaseProducts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemPurchaseProductBinding binding;
        public ViewHolder(@NonNull ItemPurchaseProductBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, PurchaseProduct item);
    }
}
