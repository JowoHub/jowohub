package c.plus.plan.shorts.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.ProductDetails;
import com.blankj.utilcode.util.Utils;
import com.youth.banner.adapter.BannerAdapter;

import java.util.ArrayList;
import java.util.List;

import c.plus.plan.jowosdk.entity.Banner;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.ItemMenuBinding;
import c.plus.plan.shorts.databinding.ItemVipItemBinding;
import c.plus.plan.shorts.ui.entity.Menu;

public class VipItemAdapter extends BannerAdapter<ProductDetails.SubscriptionOfferDetails, VipItemAdapter.ViewHolder> {
    public static final String VIP_M = "monthvip";
//    public static final String VIP_Q = "vip-qua";
    public static final String VIP_Y = "yearvip";
    private List<ProductDetails.SubscriptionOfferDetails> productDetailsList;
    private VipItemAdapter.OnItemClickListener onItemClickListener;

    public VipItemAdapter(List<ProductDetails.SubscriptionOfferDetails> datas) {
        super(datas);
        productDetailsList = datas;
    }

    public void setOnItemClickListener(VipItemAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ProductDetails.PricingPhase getPricing(ProductDetails.SubscriptionOfferDetails item, int recurrenceMode){
        for (ProductDetails.PricingPhase pp: item.getPricingPhases().getPricingPhaseList()) {
            if(pp.getRecurrenceMode() == recurrenceMode){
                return pp;
            }
        }
        return null;
    }

    @Override
    public ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ItemVipItemBinding binding = ItemVipItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindView(ViewHolder holder, ProductDetails.SubscriptionOfferDetails data, int position, int size) {
        ProductDetails.SubscriptionOfferDetails item = productDetailsList.get(position);

        ProductDetails.PricingPhase pricingPhase = getPricing(item, ProductDetails.RecurrenceMode.INFINITE_RECURRING);
        if(pricingPhase != null){
            String unit = pricingPhase.getFormattedPrice().replaceAll("\\d", "").replaceAll("\\.", "");
            holder.binding.tvPrice.setText(pricingPhase.getFormattedPrice());
            if(TextUtils.equals(VIP_M, item.getBasePlanId())){
                holder.binding.tvTitle.setText(R.string.vip_m_title);
                holder.binding.tvDesc.setText(R.string.vip_m_desc);
            } else if(TextUtils.equals(VIP_Y, item.getBasePlanId())){
                holder.binding.tvTitle.setText(R.string.vip_y_title);
                holder.binding.tvDesc.setText(R.string.vip_y_desc);
            }
        }

        holder.itemView.setOnClickListener(view -> {
            if(onItemClickListener != null){
                onItemClickListener.click(position, item);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemVipItemBinding binding;
        public ViewHolder(@NonNull ItemVipItemBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, ProductDetails.SubscriptionOfferDetails item);
    }
}
