package com.prathab.shopping.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.prathab.shopping.R;

public class Login extends AppCompatActivity {
  @BindView(R.id.editTextMobile) EditText mEditTextMobile;
  @BindView(R.id.editTextPassword) EditText mEditTextPassword;
  @BindView(R.id.buttonLogin) Button mButtonLogin;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.buttonLogin) public void login() {

  }
}
