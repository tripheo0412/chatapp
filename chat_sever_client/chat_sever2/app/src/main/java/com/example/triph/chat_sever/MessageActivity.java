package com.example.triph.chat_sever;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageActivity extends AppCompatActivity
        implements ClientController.MessageListener, ClientController.NameListener, ClientController.LogOutListener {

    @BindView(R.id.rcvChat)
    RecyclerView rcvChat;

    @BindView(R.id.toolbarChat)
    Toolbar toolbarChat;

    @BindView(R.id.edtMess)
    TextInputEditText edtMess;

    @BindView(R.id.tvUserName)
    TextView tvUserName;
    private Dialog dialogUsers;

    @OnClick(R.id.btnSent)
    public void onBtnSentClick() {
        String text = edtMess.getText().toString();
        if (text.isEmpty())
            return;
        text = text.replace("\n", "<<<|||space_bar|||>>>");
        String command = "message~|>$?<@" + text;
        ClientController.addCommand(command);
        edtMess.getText().clear();
    }

    MessageAdapter messageAdapter;
    List<Sms> smsArrayList;
    UsersAdapter usersAdapter;
    List<String> usernames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarChat);
        smsArrayList = Collections.synchronizedList(new ArrayList<Sms>());
        messageAdapter = new MessageAdapter(this, smsArrayList);
        rcvChat.setAdapter(messageAdapter);
        rcvChat.setItemAnimator(null);
        setUpDialog();
        ClientController.setMessageListener(this);
        ClientController.setNameListener(this);
        ClientController.setLogOutListener(this);
        ClientController.addCommand("messages~|>$?<@");
        ClientController.addCommand("name~|>$?<@");
    }

    private void setUpDialog() {
        dialogUsers = new Dialog(this);
        dialogUsers.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUsers.setContentView(R.layout.dialog_users);
        usernames = new ArrayList<>();
        RecyclerView rcvUsers = dialogUsers.findViewById(R.id.rcvUsers);
        usersAdapter = new UsersAdapter(usernames, this);
        rcvUsers.setAdapter(usersAdapter);
        Button btnClose = dialogUsers.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUsers.dismiss();
            }
        });
        dialogUsers.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                usernames.clear();
                usersAdapter.notifyDataSetChanged();
                ClientController.addCommand("users~|>$?<@");
            }
        });
    }

    @Override
    protected void onDestroy() {
        ClientController.addCommand("quit~|>$?<@");
        ClientController.setMessageListener(null);
        ClientController.setNameListener(null);
        ClientController.setLogOutListener(null);
        messageAdapter.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_sign_out:
                ClientController.addCommand("signout~|>$?<@");
                break;
            case R.id.mnu_users:
                dialogUsers.show();
                break;
        }
        return true;
    }

    @Override
    public void onNewMessage(final Sms sms) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                smsArrayList.add(sms);
                if (messageAdapter.getItemCount() > 1)
                    messageAdapter.notifyItemChanged(smsArrayList.size() - 2);
                messageAdapter.notifyItemInserted(smsArrayList.size() - 1);
                rcvChat.scrollToPosition(smsArrayList.size() - 1);
            }
        });
    }

    @Override
    public void onNameReceive(final String name) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvUserName.setText(name);
            }
        });
    }

    @Override
    public void onNameListReceive(final String user) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                usernames.add(user);
                usersAdapter.notifyItemInserted(usernames.size() - 1);
            }
        });
    }

    @Override
    public void onLogOut() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }
}
