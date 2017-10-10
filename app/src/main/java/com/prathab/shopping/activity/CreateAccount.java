package com.prathab.shopping.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.prathab.shopping.R;
import com.prathab.shopping.constants.EndPoints;
import com.prathab.shopping.constants.HttpConstants;
import com.prathab.shopping.constants.JwtConstants;
import com.prathab.shopping.constants.SharedPreferencesConstants;
import com.prathab.shopping.utility.Validators;
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

public class CreateAccount extends AppCompatActivity implements Callback {
  @BindView(R.id.editTextName) EditText mEditTextName;
  @BindView(R.id.editTextMobile) EditText mEditTextMobile;
  @BindView(R.id.editTextPassword) EditText mEditTextPassword;
  @BindView(R.id.editTextConfirmPassword) EditText mEditTextConfirmPassword;
  @BindView(R.id.buttonCreateAccount) Button mButtonCreateAccount;
  OkHttpClient mOkHttpClient;
  private AlertDialog mAlertDialog;
  private ProgressDialog mProgressDialog;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);
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
  }

  @OnClick(R.id.buttonCreateAccount) public void createAccount() {
    String name = mEditTextName.getText().toString();
    String mobile = mEditTextMobile.getText().toString().trim();
    String password = mEditTextPassword.getText().toString().trim();
    if (name.isEmpty() || !Validators.isMobileValid(mobile) || !Validators.isPasswordValid(
        password)) {
      Toast.makeText(CreateAccount.this, "Enter valid details", Toast.LENGTH_SHORT).show();
      return;
    }
    createAccount(name, mobile, password);
  }

  private void createAccount(String name, String mobile, String password) {
    Timber.d("Attempting to Create account ...Name : %s, Mobile : %s, Password : %s", name, mobile,
        password);
    showProgress();

    mOkHttpClient.newCall(createSignupRequest(name, mobile, password)).enqueue(this);
  }

  private Request createSignupRequest(String name, String mobile, String password) {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("name", name);
      jsonObject.put("mobile", mobile);
      jsonObject.put("password", password);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    RequestBody requestBody = RequestBody.create(JwtConstants.JSON, jsonObject.toString());

    Timber.d("Making create account request[POST] to URL %s", EndPoints.ACCOUNTS_CREATE_ACCCOUNT);

    return new Request.Builder().url(EndPoints.ACCOUNTS_CREATE_ACCCOUNT).post(requestBody).build();
  }

  @Override protected void onStop() {
    super.onStop();
    mOkHttpClient.dispatcher().cancelAll();
  }

  private void createAccountFailure(Response response) {
    String message;
    if (response == null) {
      message = "Unexpected error";
      Timber.d("response is null," + message);
    } else if (response.code() == 400) {
      message = "Invalid input";
      Timber.d(message);
    } else if (response.code() == 409) {
      message = "Account already exists. Please login";
      Timber.d(message);
    } else {
      message = "Unexpected error";
      Timber.d(message);
    }

    CreateAccount.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(CreateAccount.this).setTitle("Cannot Signup")
          .setMessage(message)
          .setCancelable(false)
          .setPositiveButton("Okay", (dialog, which) -> {
            dialog.dismiss();
          })
          .create();
      mAlertDialog.show();
    });
  }

  private void createAccountSuccess(Response response) {
    String jwt = response.header(HttpConstants.HTTP_HEADER_JWT_TOKEN);

    Timber.d("JWT : %s", jwt);

    writeToSharedPreference(jwt);
    CreateAccount.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(CreateAccount.this).setTitle("Account Created")
          .setMessage("Successfully Created new account ...")
          .setCancelable(false)
          .setPositiveButton("Okay", (dialog, which) -> {
            dialog.dismiss();
            startLoginActivity();
          })
          .create();
      mAlertDialog.show();
    });
  }

  private void startLoginActivity() {
    NavUtils.navigateUpFromSameTask(this);
  }

  private void writeToSharedPreference(String jwt) {
    SharedPreferences.Editor editor =
        getSharedPreferences(SharedPreferencesConstants.SHARED_PREFERENCES_NAME,
            MODE_PRIVATE).edit();
    editor.putString(SharedPreferencesConstants.JWT_TOKEN, jwt);
    editor.apply();
    Timber.d("Wrting JWT: [%s] to SharedPrefs", jwt);
  }

  @Override public void onFailure(Call call, IOException e) {

    Timber.d("Create account request failed : %s", e.getCause());

    CreateAccount.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(CreateAccount.this).setTitle("Cannot Signup")
          .setMessage("Please try again later")
          .setCancelable(false)
          .setPositiveButton("Okay", (dialog, which) -> {
            dialog.dismiss();
          })
          .create();
      mAlertDialog.show();
    });

    hideProgress();
  }

  @Override public void onResponse(Call call, Response response) throws IOException {
    if (response.code() == 200) {
      createAccountSuccess(response);
    } else {
      createAccountFailure(response);
    }
    hideProgress();
  }

  private void hideProgress() {
    mProgressDialog.dismiss();
  }

  private void showProgress() {
    mProgressDialog.setTitle("Please wait");
    mProgressDialog.setMessage("Creating account");
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setCancelable(false);
    mProgressDialog.show();
  }
}
