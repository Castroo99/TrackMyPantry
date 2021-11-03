package com.progetto.trackingmypantry.ui.home;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.progetto.trackingmypantry.R;
import com.progetto.trackingmypantry.retrofit.Products;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<Products> productsList;
    private OnRatingListener vOnRatingListener;
    private OnAddListener aOnAddListener;

    public HomeAdapter(Context context, List<Products> productsList, OnRatingListener onRatingListener, OnAddListener onAddListener) {
        this.context = context;
        this.productsList = productsList;
        this.vOnRatingListener = onRatingListener;
        this.aOnAddListener = onAddListener;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_cards, parent, false);
        return new ViewHolder(view, vOnRatingListener, aOnAddListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Products products = productsList.get(position);
        holder.nameProd.setText(products.getName() + " / " + products.getBarcode());
        holder.descProd.setText(products.getDescription());
        holder.productId.setText(products.getId());
        if (products.getImg() != null && !products.getImg().equals("null")) {
            if (products.getImg().contains("data:image/jpeg;base64,") ||products.getImg().contains("data:image/png;base64,")) {
                Glide.with(context).load(products.getImg()).into(holder.imageProd);
            }
            else {
                Glide.with(context).load(Base64.decode(products.getImg(), Base64.DEFAULT)).into(holder.imageProd);
            }
        }
        else {
            Glide.with(context).clear(holder.imageProd);
            holder.imageProd.setImageResource(R.drawable.ic_food);
        }
        holder.ratingBar.setRating(0);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements RatingBar.OnRatingBarChangeListener, ImageButton.OnClickListener {
        private ImageView imageProd;
        private TextView nameProd, descProd;
        private RatingBar ratingBar;
        private TextView productId;
        private ImageButton imageButton;
        OnRatingListener onRatingListener;
        OnAddListener onAddListener;

        public ViewHolder(View itemView, OnRatingListener onRatingListener, OnAddListener onAddListener) {
            super(itemView);
            imageProd = itemView.findViewById(R.id.imageProd);
            nameProd = itemView.findViewById(R.id.nameProd);
            descProd = itemView.findViewById(R.id.descriptionProd);
            productId = itemView.findViewById(R.id.productId);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imageButton = itemView.findViewById(R.id.imageButton);
            this.onRatingListener = onRatingListener;
            this.onAddListener = onAddListener;
            imageButton.setOnClickListener(this);
            ratingBar.setOnRatingBarChangeListener(this);
        }

        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            onRatingListener.onRatingClick(getAbsoluteAdapterPosition(), ratingBar, v);
        }

        @Override
        public void onClick(View view) {
            onAddListener.onAddClick(view);
        }
    }

    public interface OnRatingListener {
        void onRatingClick(int position, RatingBar ratingBar, float v);
    }

    public interface OnAddListener {
        void onAddClick(View view);
    }

}
