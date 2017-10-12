package com.prathab.shopping.views;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.prathab.shopping.LoggingListener;
import com.prathab.shopping.R;
import com.prathab.shopping.activity.ProductDetail;
import com.prathab.shopping.model.ProductsModel;
import java.util.LinkedList;

public class ProductsRecyclerViewAdapter
    extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.MyViewHolder> {

  private LinkedList<ProductsModel> products;

  public ProductsRecyclerViewAdapter(LinkedList<ProductsModel> products) {
    this.products = products;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.products_list_single_row, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    ProductsModel current = products.get(position);
    holder.name.setText(current.getName());
    holder.price.setText("Rs." + current.getPrice());
    holder.description.setText(current.getDescription());

    holder.image.setOnClickListener(v -> {
      Intent intent = new Intent(holder.image.getContext(), ProductDetail.class);
      intent.putExtra("products_model", products.get(position));
      holder.image.getContext().startActivity(intent);
    });

    /*
    ArrayList<String> tagsList = current.getTags();
    for (int i = 0; i < tagsList.size(); i++) {
      holder.tags.setText(holder.tags.getText() + "," + tagsList.get(i));
    }
    */

    //String firstImage = current.getImages().get(0);
    String text = current.getName() + " ";
    text = text.split(" ")[0];
    String firstImage = "http://via.placeholder.com/1000/FF9800/FFFFFF?text=" + text;
    Glide.with(holder.image.getContext())
        .load(firstImage)
        .placeholder(R.drawable.product_placeholder)
        .error(R.drawable.product_placeholder)
        .crossFade()
        .listener(new LoggingListener<>())
        .into(holder.image);
  }

  @Override public int getItemCount() {
    return products.size();
  }

  class MyViewHolder extends ViewHolder {
    TextView name, price, description;
    ImageView image;

    MyViewHolder(View itemView) {
      super(itemView);
      name = (TextView) itemView.findViewById(R.id.name);
      price = (TextView) itemView.findViewById(R.id.price);
      description = (TextView) itemView.findViewById(R.id.description);
      image = (ImageView) itemView.findViewById(R.id.image);
    }
  }
}
