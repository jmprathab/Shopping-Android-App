package com.prathab.shopping.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.prathab.shopping.R;
import com.prathab.shopping.constants.EndPoints;
import com.prathab.shopping.model.ProductsModel;
import com.prathab.shopping.views.ProductsRecyclerViewAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class Products extends AppCompatActivity implements Callback {
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  ProgressDialog mProgressDialog;
  ProductsRecyclerViewAdapter mAdapter;
  LinkedList<ProductsModel> products = new LinkedList<>();
  AlertDialog mAlertDialog;
  private OkHttpClient mOkHttpClient;
  private DividerItemDecoration mDividerItemDecoration;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_products);
    initialize();
  }

  private void initialize() {
    ButterKnife.bind(this);
    setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
    ActionBar actionBar = getSupportActionBar();
    if (actionBar == null) {
      Timber.d("Action Bar is null");
    }
    actionBar.setDisplayHomeAsUpEnabled(true);
    mProgressDialog = new ProgressDialog(this);
    mOkHttpClient = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build();

    mAdapter = new ProductsRecyclerViewAdapter(products);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
        new LinearLayoutManager(this).getOrientation());
    recyclerView.addItemDecoration(mDividerItemDecoration);
    recyclerView.setAdapter(mAdapter);

    //TODO Remove this after testing
    new Thread(() -> Glide.get(Products.this).clearDiskCache()).start();
  }

  @OnClick(R.id.buttonFetchProducts) public void fetchProducts() {
    //TODO: fetch a list of products from api and display in recycler view
    showProgress();

    mOkHttpClient.newCall(createFetchProductsRequest()).enqueue(this);
  }

  private Request createFetchProductsRequest() {
    HttpUrl.Builder urlBuilder = HttpUrl.parse(EndPoints.PRODUCTS_URL).newBuilder();
    urlBuilder.addQueryParameter("page", "1");
    urlBuilder.addQueryParameter("limit", "50");
    String url = urlBuilder.toString();

    Timber.d("Making fetch products request[GET] to URL %s", url);

    return new Request.Builder().url(url).build();
  }

  private void hideProgress() {
    mProgressDialog.dismiss();
  }

  private void showProgress() {
    mProgressDialog.setTitle("Please wait");
    mProgressDialog.setMessage("Fetching Products");
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setCancelable(false);
    mProgressDialog.show();
  }

  @Override public void onFailure(Call call, IOException e) {
    Timber.d("Fetch products request failed : %s", e.getCause());

    Products.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(Products.this).setTitle("Cannot fetch products")
          .setMessage("Please try again later")
          .setCancelable(false)
          .setPositiveButton("Okay", (dialog, which) -> dialog.dismiss())
          .create();
      mAlertDialog.show();
    });

    hideProgress();
  }

  @Override public void onResponse(Call call, Response response) throws IOException {
    if (response.code() == 200) {
      fetchProductsSuccess(response);
    } else {
      fetchProductsFailure(response);
    }
    hideProgress();
  }

  private void fetchProductsFailure(Response response) {
    String message;
    if (response == null) {
      message = "Unexpected error response is null";
      Timber.d("response is null," + message);
    } else {
      message = "Unexpected error";
      Timber.d(message);
    }

    Products.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(Products.this).setTitle("Cannot fetch products")
          .setMessage(message)
          .setCancelable(false)
          .setPositiveButton("Okay", (dialog, which) -> {
            dialog.dismiss();
          })
          .create();
      mAlertDialog.show();
    });
  }

  private void fetchProductsSuccess(Response response) {
    String responseBody = null;
    try {
      responseBody = response.body().string();
    } catch (IOException e) {
      Timber.d(e, "Exception in loginSuccess()");
      fetchProductsFailure(null);
      e.printStackTrace();
    }
    Timber.d("Successfully fetched products : Response 200 : %s", responseBody);
    LinkedList<ProductsModel> productsModelLinkedList =
        parseResponseAndGetProductsList(responseBody);
    displayProductsList(productsModelLinkedList);
  }

  private void displayProductsList(LinkedList<ProductsModel> productsModelLinkedList) {
    for (int i = 0; i < productsModelLinkedList.size(); i++) {
      products.add(productsModelLinkedList.get(i));
    }
    Products.this.runOnUiThread(() -> mAdapter.notifyDataSetChanged());
  }

  private LinkedList<ProductsModel> parseResponseAndGetProductsList(String responseBody) {
    LinkedList<ProductsModel> productsList = new LinkedList<>();
    try {
      JSONArray jsonArray = new JSONArray(responseBody);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject currentProduct = (JSONObject) jsonArray.get(i);
        String id = currentProduct.getString("id");
        String description = currentProduct.getString("description");
        String name = currentProduct.getString("name");
        String price = currentProduct.getString("price");
        int rating = currentProduct.getInt("rating");

        ArrayList<String> imagesList, tagsList;
        imagesList = tagsList = null;

        if (currentProduct.has("images")) {
          JSONArray images = currentProduct.getJSONArray("images");
          imagesList = new ArrayList<>(images.length());
          for (int j = 0; j < images.length(); j++) {
            imagesList.add(images.getString(j));
          }
        }

        if (currentProduct.has("tags")) {
          JSONArray tags = currentProduct.getJSONArray("tags");
          tagsList = new ArrayList<>(tags.length());
          for (int j = 0; j < tags.length(); j++) {
            tagsList.add(tags.getString(j));
          }
        }

        ProductsModel productsModel =
            new ProductsModel(id, name, price, rating, imagesList, tagsList, description);
        productsList.add(productsModel);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return productsList;
  }

  @Override protected void onStop() {
    super.onStop();
    mOkHttpClient.dispatcher().cancelAll();
  }
}
