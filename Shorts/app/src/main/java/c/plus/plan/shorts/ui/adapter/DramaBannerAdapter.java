package c.plus.plan.shorts.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

import c.plus.plan.jowosdk.entity.Banner;
import c.plus.plan.shorts.databinding.ItemBannerBinding;

public class DramaBannerAdapter extends BannerAdapter<Banner, DramaBannerAdapter.BannerViewHolder> {
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public DramaBannerAdapter(List<Banner> banners) {
        super(banners);
    }

    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ItemBannerBinding binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        BannerViewHolder viewHolder= new BannerViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindView(BannerViewHolder holder, Banner banner, int position, int size) {
        Glide.with(holder.binding.iv).load(banner.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.iv);

        holder.binding.getRoot().setOnClickListener(view -> {
            if(onItemClickListener != null){
                onItemClickListener.click(banner);
            }
        });
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ItemBannerBinding binding;

        public BannerViewHolder(@NonNull ItemBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener{
        void click(Banner banner);
    }
}
