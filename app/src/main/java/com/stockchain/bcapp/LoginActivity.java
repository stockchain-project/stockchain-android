package com.stockchain.bcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stockchain.cosmos.Account;
import com.stockchain.cosmos.PreferenceManager;
import com.stockchain.cosmos.Tools;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button goSigninButton = findViewById(R.id.goSigninButton);
        goSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(intent);
            }
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView usernameView = findViewById(R.id.signinName);
                String username = usernameView.getText().toString();
                TextView mnemonicView = findViewById(R.id.mnemonic);
                String mnemonic = mnemonicView.getText().toString();

                // --LOGIN--
                Account ac = new Account(getApplicationContext(), getString(R.string.server_ip));
                ac.createWalletByMnemonicLocal(username, mnemonic);
                if (ac.isUsernameExistsBlockchain(username)) {
                    if (ac.isUsernameExistsLocal(username)) {
                        try {
                            String address = ac.getAddressByUsernameLocal(username);
                            PreferenceManager pm = new PreferenceManager();
                            pm.setString(getApplicationContext(), "username", username);
                            pm.setString(getApplicationContext(), "address", address);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (IOException e) {
                        }
                    }else{
                        //address는 mnemonic->pub_key->address로 생성되기에 mnemonic으로
                        if (ac.createWalletByMnemonicLocal(username, mnemonic)) {
                            try {
                                String address = ac.getAddressByUsernameLocal(username);
                                PreferenceManager pm = new PreferenceManager();
                                pm.setString(getApplicationContext(), "username", username);
                                pm.setString(getApplicationContext(), "address", address);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (IOException e) {
                            }

                        }else {
                            Tools.showDialog(LoginActivity.this, "로그인", "로그인 실패: 유효한 mnemonic이 아닙니다.");
                        }
                    }
                } else {
                    Tools.showDialog(LoginActivity.this, "로그인", "존재하지않는 Name입니다.");
                }
            }
        });

    }
}