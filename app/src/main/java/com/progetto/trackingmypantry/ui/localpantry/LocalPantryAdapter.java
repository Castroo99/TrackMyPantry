package com.progetto.trackingmypantry.ui.localpantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.progetto.trackingmypantry.R;
import com.progetto.trackingmypantry.room.LocalProducts;
import java.util.List;

public class LocalPantryAdapter extends RecyclerView.Adapter<LocalPantryAdapter.ViewHolder> {

    private Context context;
    private List<LocalProducts> localProductsList;
    private OnDeleteListener dOnDeleteListener;

    public LocalPantryAdapter(Context context, List<LocalProducts> localProductsList, OnDeleteListener onDeleteListener) {
        this.context = context;
        this.localProductsList = localProductsList;
        this.dOnDeleteListener = onDeleteListener;
    }

    @NonNull
    @Override
    public LocalPantryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.local_products_card, parent, false);
        return new ViewHolder(view, dOnDeleteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalProducts localProducts = localProductsList.get(position);
        holder.localNameProd.setText(localProducts.getProdName() + " /\n " + localProducts.getLocalBarcode());
        holder.localDescProd.setText(localProducts.getLocalDescription());
        holder.expirationDate.setText("Scadenza: " + localProducts.getExpirationDate());
        holder.typeProd.setText(localProducts.getType());
        if (localProducts.getLocalImg() != null) {
            Glide.with(context).load(localProducts.getLocalImg()).into(holder.localImageProd);
        }
        else {
            Glide.with(context).clear(holder.localImageProd);
            holder.localImageProd.setImageResource(R.drawable.ic_food);
        }
    }

    @Override
    public int getItemCount() {
        return localProductsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView localImageProd;
        private TextView localNameProd, localDescProd, expirationDate, typeProd;
        private ImageButton deleteBtn;
        OnDeleteListener onDeleteListener;

        public ViewHolder(View itemView, OnDeleteListener onDeleteListener) {
            super(itemView);
            localImageProd = itemView.findViewById(R.id.localImageProd);
            localNameProd = itemView.findViewById(R.id.localNameProd);
            localDescProd = itemView.findViewById(R.id.localDescriptionProd);
            expirationDate = itemView.findViewById(R.id.expirationDate);
            typeProd = itemView.findViewById(R.id.typeProd);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            this.onDeleteListener = onDeleteListener;
            deleteBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onDeleteListener.onDeleteClick(getAbsoluteAdapterPosition(), localProductsList);
        }
    }

    public interface OnDeleteListener {
        void onDeleteClick(int position, List<LocalProducts> localProductsList);
    }

}