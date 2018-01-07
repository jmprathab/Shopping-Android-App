package com.prathab.shopping.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.prathab.shopping.LoggingListener;
import com.prathab.shopping.R;
import com.prathab.shopping.constants.EndPoints;
import com.prathab.shopping.constants.JwtConstants;
import com.prathab.shopping.constants.SharedPreferencesConstants;
import com.prathab.shopping.model.ProductsModel;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

import static com.prathab.shopping.constants.HttpConstants.HTTP_HEADER_JWT_TOKEN;

public class ProductDetail extends AppCompatActivity implements Callback {
  @BindView(R.id.name) TextView name;
  @BindView(R.id.price) TextView price;
  @BindView(R.id.description) TextView description;
  @BindView(R.id.image) ImageView image;
  ProgressDialog mProgressDialog;
  AlertDialog mAlertDialog;
  private OkHttpClient mOkHttpClient;

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
    mProgressDialog = new ProgressDialog(this);
    mOkHttpClient = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build();
  }

  @OnClick(R.id.addToCart) public void addToCart() {
    showProgress();

    mOkHttpClient.newCall(createAddToCartRequest()).enqueue(this);
  }

  @OnClick(R.id.checkout) public void checkout() {
    showProgress();

    mOkHttpClient.newCall(createCheckoutRequest()).enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        Timber.d("Checkout request failed : %s", e.getCause());

        ProductDetail.this.runOnUiThread(() -> {
          mAlertDialog = new AlertDialog.Builder(ProductDetail.this).setTitle("Cannot checkout")
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
          checkoutSuccess(response);
        } else {
          checkoutFailure(response);
        }
        hideProgress();
      }
    });
  }

  private void checkoutFailure(Response response) {
    String message;
    if (response == null) {
      message = "Unexpected error response is null";
      Timber.d("response is null," + message);
    } else {
      message = "Unexpected error";
      Timber.d(message);
    }

    ProductDetail.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(ProductDetail.this).setTitle("Cannot checkout")
          .setMessage(message)
          .setCancelable(false)
          .setPositiveButton("Okay", (dialog, which) -> {
            dialog.dismiss();
          })
          .create();
      mAlertDialog.show();
    });
  }

  private void checkoutSuccess(Response response) {
    if (response.code() != 200) {
      Timber.d("checkoutSuccess() was actually a failure");
      checkoutFailure(null);
    }

    ProductDetail.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(ProductDetail.this).setTitle("Success")
          .setMessage("Successfully checked out")
          .setCancelable(false)
          .setPositiveButton("Okay", (dialog, which) -> {
            dialog.dismiss();
          })
          .create();
      mAlertDialog.show();
    });

    Timber.d("Successfully checked out : Response 200");
  }

  private Request createCheckoutRequest() {
    JSONObject jsonObject = new JSONObject();

    RequestBody requestBody = RequestBody.create(JwtConstants.JSON, jsonObject.toString());

    Timber.d("Making checkout request[POST] to URL %s", EndPoints.CARTS_URL + "/checkout");

    SharedPreferences pref =
        getSharedPreferences(SharedPreferencesConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    String jwtToken = pref.getString(SharedPreferencesConstants.JWT_TOKEN, "");

    return new Request.Builder().post(requestBody)
        .url(EndPoints.CARTS_URL + "/checkout")
        .header(HTTP_HEADER_JWT_TOKEN, jwtToken)
        .build();
  }

  private Request createAddToCartRequest() {
    JSONObject jsonObject = new JSONObject();
    ProductsModel productsModel = (ProductsModel) getIntent().getExtras().get("products_model");
    try {
      jsonObject.put("productsId", productsModel.getId());
      jsonObject.put("quantity", 1);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    RequestBody requestBody = RequestBody.create(JwtConstants.JSON, jsonObject.toString());

    Timber.d("Making Add to cart request[POST] to URL %s", EndPoints.CARTS_URL);

    SharedPreferences pref =
        getSharedPreferences(SharedPreferencesConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    String jwtToken = pref.getString(SharedPreferencesConstants.JWT_TOKEN, "");

    return new Request.Builder().post(requestBody)
        .url(EndPoints.CARTS_URL)
        .header(HTTP_HEADER_JWT_TOKEN, jwtToken)
        .build();
  }

  private void showProgress() {
    mProgressDialog.setTitle("Please wait");
    mProgressDialog.setMessage("Adding to cart");
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setCancelable(false);
    mProgressDialog.show();
  }

  @Override public void onFailure(Call call, IOException e) {
    Timber.d("Add to cart request failed : %s", e.getCause());

    ProductDetail.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(ProductDetail.this).setTitle("Cannot add to cart")
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
      addToCartSuccess(response);
    } else {
      addToCartFailure(response);
    }
    hideProgress();
  }

  private void addToCartFailure(Response response) {
    String message;
    if (response == null) {
      message = "Unexpected error response is null";
      Timber.d("response is null," + message);
    } else {
      message = "Unexpected error";
      Timber.d(message);
    }

    ProductDetail.this.runOnUiThread(
        () -> new AlertDialog.Builder(ProductDetail.this).setTitle("Cannot add to cart")
            .setMessage("Add to cart failed")
            .setCancelable(false)
            .setPositiveButton("Okay", (dialog, which) -> dialog.dismiss())
            .create()
            .show());
  }

  private void addToCartSuccess(Response response) {
    if (response.code() != 200) {
      Timber.d("addToCartSuccess() was actually a failure");
      addToCartFailure(null);
    }
    ProductDetail.this.runOnUiThread(
        () -> new AlertDialog.Builder(ProductDetail.this).setTitle("Success")
            .setMessage("Successfully added to cart")
            .setCancelable(false)
            .setPositiveButton("Okay", (dialog, which) -> dialog.dismiss())
            .create()
            .show());

    Timber.d("Successfully added product to cart : Response 200");
  }

  private void hideProgress() {
    mProgressDialog.dismiss();
  }

  @Override protected void onStop() {
    super.onStop();
    mOkHttpClient.dispatcher().cancelAll();
  }
}
