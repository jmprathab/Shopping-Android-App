package com.prathab.shopping.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.prathab.shopping.R;
import com.prathab.shopping.constants.EndPoints;
import com.prathab.shopping.utility.Validators;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

public class Login extends AppCompatActivity {
  @BindView(R.id.editTextMobile) EditText mEditTextMobile;
  @BindView(R.id.editTextPassword) EditText mEditTextPassword;
  @BindView(R.id.buttonLogin) Button mButtonLogin;
  OkHttpClient mOkHttpClient = new OkHttpClient();
  ProgressDialog mProgressDialog;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    initialize();
  }

  private void initialize() {
    ButterKnife.bind(this);
    mProgressDialog = new ProgressDialog(this);
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
    Timber.d("Attempting to Login, Mobile : %s, Password : %s", mobile, password);
    showProgress();

    mOkHttpClient.newCall(createLoginRequest(mobile, password)).enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        Timber.d("Login request failed : %s", e.getMessage());
        hideProgress();
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        Timber.d("Successfully got login response : %s", response.body().toString());
        hideProgress();
      }
    });
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
    RequestBody requestBody =
        new FormBody.Builder().add("mobile", mobile).add("password", password).build();

    return new Request.Builder().url(EndPoints.ACCOUNTS_URL).post(requestBody).build();
  }

  @OnClick(R.id.buttonSignUp) public void launchCreateAccount() {
    startActivity(new Intent(this, CreateAccount.class));
  }

  @Override protected void onStop() {
    super.onStop();
    mOkHttpClient.dispatcher().cancelAll();
  }
}