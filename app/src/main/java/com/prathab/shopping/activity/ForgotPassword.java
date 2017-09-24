package com.prathab.shopping.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.prathab.shopping.R;

public class ForgotPassword extends AppCompatActivity {
  @BindView(R.id.editTextEmail) EditText mEditTextEmail;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.buttonResetPassword) public void resetPassword() {

  }
}
