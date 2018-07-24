package com.example.triph.chat_sever;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends AppCompatActivity
        implements ClientController.ConnectionListener,
        ClientController.LoginStateListener {

    @BindView(R.id.edtEmail)
    TextInputEditText edtEmail;

    @BindView(R.id.tvError)
    TextView tvError;

    @OnClick(R.id.btnLogin)
    public void onLoginClick() {
        ClientController.setConnectionListener(this);
        ClientController.setLoginStateListener(this);
        ClientController.connect();
        ClientController.addCommand("user~|>$?<@"+edtEmail.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        ClientController.setConnectionListener(null);
        ClientController.setLoginStateListener(null);
        super.onDestroy();
    }

    @Override
    public void onConnectionError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LogInActivity.this, "Connection error occurs", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onLoginSuccess() {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginFailed(final String e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvError.setText(e);
            }
        });
    }
}
