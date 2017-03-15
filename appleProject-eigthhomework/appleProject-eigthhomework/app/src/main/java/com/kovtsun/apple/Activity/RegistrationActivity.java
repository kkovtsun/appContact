package com.kovtsun.apple.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kovtsun.apple.Activity.ContactsActivity;
import com.kovtsun.apple.Activity.MainActivity;
import com.kovtsun.apple.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistrationActivity extends AppCompatActivity {

    private EditText login, password, repeatPassword;
    private String loginPrefActive = "", passwordPrefActive = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        SharedPreferences sharedPrefActiveNow = getSharedPreferences("userInfoActive", Context.MODE_PRIVATE);
        loginPrefActive = sharedPrefActiveNow.getString("username", "");
        passwordPrefActive = sharedPrefActiveNow.getString("password", "");
        login = (EditText) findViewById(R.id.editLoginR);
        password = (EditText) findViewById(R.id.editPasswordR);
        repeatPassword = (EditText) findViewById(R.id.editRepeatPasswordR);
    }

    public void onClickRegistrationR(View view){
        String l = login.getText().toString();
        String p = password.getText().toString();
        String rp = repeatPassword.getText().toString();

        if (l.equals("")||(p.equals(""))){
            Toast.makeText(this, R.string.inputData, Toast.LENGTH_SHORT).show();
        }else {
            if (!p.equals(rp)){
                Toast.makeText(this, R.string.passwordsDoNotMatch, Toast.LENGTH_SHORT).show();
            }else {
                SharedPreferences sharedPrefActive  = getSharedPreferences("userInfoActive", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorActive = sharedPrefActive.edit();
                editorActive.putString("username", l);
                editorActive.putString("password", p);
                editorActive.apply();

                SharedPreferences sharedPref  = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("username", l);
                editor.putString("password", p);
                editor.apply();

                Toast.makeText(this, R.string.txtRegistrationComplite, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, ContactsActivity.class);
                startActivity(intent);
                this.finish();
            }
        }
    }

    public void onClickCancelR(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public static  String md5Custom(String st){
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);
        while (md5Hex.length()<32){
            md5Hex = "0" + md5Hex;
        }
        return st;
    }
}
