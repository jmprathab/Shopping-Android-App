package com.prathab.shopping.views;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.prathab.shopping.R;
import com.prathab.shopping.model.ProductsModel;
import java.util.ArrayList;
import java.util.LinkedList;

public class ProductsRecyclerViewAdapter
    extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.MyViewHolder> {

  LinkedList<ProductsModel> products;

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
    holder.name.setText("Name : " + current.getName());
    holder.price.setText("Price : " + current.getPrice());
    holder.description.setText("Description : " + current.getDescription());

    ArrayList<String> tagsList = current.getTags();
    holder.tags.setText("Tags : ");
    for (int i = 0; i < tagsList.size(); i++) {
      holder.tags.setText(holder.tags.getText() + "," + tagsList.get(i));
    }

    ArrayList<String> images = current.getImages();
    holder.images.setText("Images : ");
    for (int i = 0; i < images.size(); i++) {
      holder.images.setText(holder.images.getText() + "," + images.get(i));
    }
  }

  @Override public int getItemCount() {
    return products.size();
  }

  public class MyViewHolder extends ViewHolder {
    TextView name, price, description, tags, images;

    public MyViewHolder(View itemView) {
      super(itemView);
      name = (TextView) itemView.findViewById(R.id.name);
      price = (TextView) itemView.findViewById(R.id.price);
      description = (TextView) itemView.findViewById(R.id.description);
      tags = (TextView) itemView.findViewById(R.id.tags);
      images = (TextView) itemView.findViewById(R.id.images);
    }
  }
}
