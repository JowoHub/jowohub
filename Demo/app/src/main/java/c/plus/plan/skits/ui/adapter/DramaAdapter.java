package c.plus.plan.skits.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.skits.databinding.ItemDramaBinding;

public class DramaAdapter extends RecyclerView.Adapter<DramaAdapter.ViewHolder> {
    private List<Drama> dramas;

    public void setDramas(List<Drama> dramas) {
        this.dramas = dramas;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDramaBinding binding = ItemDramaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drama item = dramas.get(position);
        Glide.with(holder.binding.iv).load(item.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.iv);
        holder.binding.tv.setText(item.getName());

        holder.binding.getRoot().setOnClickListener(v -> {
            if(onItemClickListener != null){
                Drama it = dramas.get(position);
                onItemClickListener.click(position, it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dramas == null ? 0 : dramas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemDramaBinding binding;
        public ViewHolder(@NonNull ItemDramaBinding item) {
            super(item.getRoot());
            binding = item;

        }
    }

    public interface OnItemClickListener {
        void click(int position, Drama item);
    }
}
