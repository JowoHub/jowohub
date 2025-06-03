package c.plus.plan.shorts.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.ItemCollectBinding;

public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.ViewHolder> {
    private List<Drama> dramas;
    private boolean edit;

    public void setDramas(List<Drama> dramas) {
        this.dramas = dramas;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CollectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCollectBinding binding = ItemCollectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CollectAdapter.ViewHolder holder, int position) {
        Drama item = dramas.get(position);
        Glide.with(holder.binding.iv).load(item.getCover())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.iv);
        holder.binding.name.setText(item.getName());

        if(item.isEnd()){
            holder.binding.state.setText(Utils.getApp().getText(R.string.drama_end));
        } else {
            holder.binding.state.setText(Utils.getApp().getText(R.string.drama_continue));
        }

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
        return dramas == null ? 0 : dramas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemCollectBinding binding;
        public ViewHolder(@NonNull ItemCollectBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, Drama item);
        void menu(int position, Drama item);
        void checked(int position, Drama item, boolean isChecked);
    }
}
