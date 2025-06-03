package c.plus.plan.shorts.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import c.plus.plan.shorts.databinding.ItemMenuBinding;
import c.plus.plan.shorts.ui.entity.Menu;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private ArrayList<Menu> menus;

    public void setMenus(ArrayList<Menu> menus) {
        this.menus = menus;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMenuBinding binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
        Menu item = menus.get(position);
        holder.binding.tv.setText(item.getName());

        holder.binding.getRoot().setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.click(position, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menus == null ? 0 : menus.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemMenuBinding binding;
        public ViewHolder(@NonNull ItemMenuBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, Menu item);
    }
}
