package com.prathab.shopping.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.prathab.shopping.LoggingListener;
import com.prathab.shopping.R;
import com.prathab.shopping.model.ProductsModel;
import timber.log.Timber;

public class ProductDetail extends AppCompatActivity {
  @BindView(R.id.name) TextView name;
  @BindView(R.id.price) TextView price;
  @BindView(R.id.description) TextView description;
  @BindView(R.id.image) ImageView image;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_product_detail);
    initialize();
    Intent intent = getIntent();
    ProductsModel productsModel = (ProductsModel) intent.getExtras().get("products_model");

    name.setText(productsModel.getName());
    price.setText(productsModel.getPrice());
    description.setText(productsModel.getDescription());

    Glide.with(this)
        .load(productsModel.getImages() == null ? R.drawable.product_placeholder
            : productsModel.getImages().get(0))
        .placeholder(R.drawable.product_placeholder)
        .error(R.drawable.product_placeholder)
        .crossFade()
        .listener(new LoggingListener<>())
        .into(image);
  }

  private void initialize() {
    ButterKnife.bind(this);
    setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
    ActionBar actionBar = getSupportActionBar();
    if (actionBar == null) {
      Timber.d("Action Bar is null");
    }
    actionBar.setDisplayHomeAsUpEnabled(true);
  }
}
