package com.prathab.shopping.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class Login extends AppCompatActivity implements Callback {
  @BindView(R.id.editTextMobile) EditText mEditTextMobile;
  @BindView(R.id.editTextPassword) EditText mEditTextPassword;
  @BindView(R.id.buttonLogin) Button mButtonLogin;
  OkHttpClient mOkHttpClient;
  ProgressDialog mProgressDialog;
  AlertDialog mAlertDialog;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    initialize();
  }

  private void initialize() {
    ButterKnife.bind(this);
    mProgressDialog = new ProgressDialog(this);
    mOkHttpClient = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build();
  }

  @OnClick(R.id.buttonLogin) public void login() {
    String mobile = mEditTextMobile.getText().toString().trim();
    String password = mEditTextPassword.getText().toString().trim();
    if (!Validators.isMobileValid(mobile) || !Validators.isPasswordValid(password)) {
      Toast.makeText(Login.this, "Enter valid details", Toast.LENGTH_SHORT).show();
      return;
    }
    loginUser(mobile, password);
  }

  private void loginUser(String mobile, String password) {
    Timber.d("Attempting to Login ... Mobile : %s, Password : %s", mobile, password);
    showProgress();

    mOkHttpClient.newCall(createLoginRequest(mobile, password)).enqueue(this);
  }

  private void loginFailure(Response response) {
    String message;
    if (response == null) {
      message = "Unexpected error";
      Timber.d("response is null," + message);
    } else if (response.code() == 401) {
      message = "Please check your credentials";
      Timber.d(message);
    } else {
      message = "Unexpected error";
      Timber.d(message);
    }

    Login.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(Login.this).setTitle("Cannot Login")
          .setMessage(message)
          .setCancelable(false)
          .setPositiveButton("Okay", (dialog, which) -> {
            dialog.dismiss();
          })
          .create();
      mAlertDialog.show();
    });
  }

  private void loginSuccess(Response response) {
    String jwt = response.header(HttpConstants.HTTP_HEADER_JWT_TOKEN);
    String responseBody = null;
    try {
      responseBody = response.body().string();
    } catch (IOException e) {
      Timber.d(e, "Exception in loginSuccess()");
      loginFailure(null);
      e.printStackTrace();
    }
    Timber.d("Successful login : Response 200 : %s", responseBody);
    Timber.d("JWT : %s", jwt);

    writeToSharedPreference(jwt, responseBody);
    Login.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(Login.this).setTitle("Login Successful")
          .setMessage("Successfully logged in ...")
          .setCancelable(false)
          .setPositiveButton("Okay", (dialog, which) -> {
            dialog.dismiss();
            startHomeActivity();
          })
          .create();
      mAlertDialog.show();
    });
  }

  private void writeToSharedPreference(String jwt, String responseBody) {
    JSONObject jsonObject = null;
    String userName = null;
    try {
      jsonObject = new JSONObject(responseBody);
      userName = jsonObject.getString("name");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    SharedPreferences.Editor editor =
        getSharedPreferences(SharedPreferencesConstants.SHARED_PREFERENCES_NAME,
            MODE_PRIVATE).edit();
    editor.putString(SharedPreferencesConstants.JWT_TOKEN, jwt);
    editor.putString(SharedPreferencesConstants.SHARED_PREFERENCES_NAME, userName);
    editor.apply();
    Timber.d("Wrting JWT: [%s] and User's Name : [%s] to SharedPrefs", jwt, userName);
  }

  private void startHomeActivity() {
    //TODO Start the home activity here
    //Toast.makeText(Login.this, "Start Next Activity", Toast.LENGTH_SHORT).show();
    finish();
  }

  private void hideProgress() {
    mProgressDialog.dismiss();
  }

  private void showProgress() {
    mProgressDialog.setTitle("Please wait");
    mProgressDialog.setMessage("Logging In");
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setCancelable(false);
    mProgressDialog.show();
  }

  private Request createLoginRequest(String mobile, String password) {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("mobile", mobile);
      jsonObject.put("password", password);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    RequestBody requestBody = RequestBody.create(JwtConstants.JSON, jsonObject.toString());

    Timber.d("Making login request[POST] to URL %s", EndPoints.ACCOUNTS_LOGIN);

    return new Request.Builder().url(EndPoints.ACCOUNTS_LOGIN).post(requestBody).build();
  }

  @OnClick(R.id.buttonSignUp) public void launchCreateAccount() {
    startActivity(new Intent(this, CreateAccount.class));
  }

  @Override protected void onStop() {
    super.onStop();
    mOkHttpClient.dispatcher().cancelAll();
  }

  @Override public void onFailure(Call call, IOException e) {
    Timber.d("Login request failed : %s", e.getCause());

    Login.this.runOnUiThread(() -> {
      mAlertDialog = new AlertDialog.Builder(Login.this).setTitle("Cannot Login")
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
      loginSuccess(response);
    } else {
      loginFailure(response);
    }
    hideProgress();
  }
}