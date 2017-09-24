package com.prathab.shopping.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.prathab.shopping.R;

public class CreateAccount extends AppCompatActivity {
  @BindView(R.id.editTextName) EditText mEditTextName;
  @BindView(R.id.editTextMobile) EditText mEditTextMobile;
  @BindView(R.id.editTextPassword) EditText mEditTextPassword;
  @BindView(R.id.editTextConfirmPassword) EditText mEditTextConfirmPassword;
  @BindView(R.id.buttonCreateAccount) Button mButtonCreateAccount;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.buttonCreateAccount) public void createAccount() {

  }
}
