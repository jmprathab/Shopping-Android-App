package com.prathab.shopping.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import butterknife.ButterKnife;
import com.prathab.shopping.R;

public class ForgotPassword extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);
    ButterKnife.bind(this);
  }
}
