package com.kovtsun.apple.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.FacebookSdk;
import com.kovtsun.apple.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private int duration;
    private EditText login, password;
    private String loginPrefActive = "", passwordPrefActive = "";
    private SignInButton mGoogleBtn;
    private static  final  int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "MAIN_ACTIVITY";

    private LoginButton mFacebook;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        login = (EditText) findViewById(R.id.editLogin);
        password = (EditText) findViewById(R.id.editPassword);
        mGoogleBtn = (SignInButton) findViewById(R.id.googleBtn);
        mFacebook = (LoginButton) findViewById (R.id.fb_login_btn);
        mFacebook.setReadPermissions("email", "public_profile");
        SharedPreferences sharedPrefActiveNow = getSharedPreferences("userInfoActive", Context.MODE_PRIVATE);
        loginPrefActive = sharedPrefActiveNow.getString("username", "");
        passwordPrefActive = sharedPrefActiveNow.getString("password", "");
        if ((loginPrefActive != "")&&(passwordPrefActive != "")){
            login.setText(loginPrefActive);
            password.setText(passwordPrefActive);
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                    finish();
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(MainActivity.this, "You Got an Error", Toast.LENGTH_LONG).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        mFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.i(TAG, "goodFacebook");
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "cancelFacebook");
            }

            @Override
            public void onError(FacebookException error) {
                Log.w(TAG, "errorFacebook");
            }
        });

    }

    private  void handleFacebookAccessToken(AccessToken token){
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credital = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credital).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete" + task.isSuccessful());
                if (!task.isSuccessful()){
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else {

            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()){
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onClickLogIn (View view){
        String l = login.getText().toString();
        String pass = password.getText().toString();
        String p = md5Custom(pass);

        String sLogin =  getString(R.string.login);
        String sPassword =  getString(R.string.password);

        duration = Toast.LENGTH_SHORT;
        Toast toast;

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String loginPref = sharedPref.getString("username", "");
        String passwordPref = sharedPref.getString("password", "");

        if ((l.equals(""))||(p.equals(""))){
            CharSequence textError = getString(R.string.inputData);
            toast = Toast.makeText(MainActivity.this, textError, duration);
            toast.show();
        }else {
            if ((l.equals(sLogin)&&(p.equals(sPassword)))||(l.equals(loginPref)&&(p.equals(passwordPref)))){

                SharedPreferences sharedPrefActive  = getSharedPreferences("userInfoActive", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorActive = sharedPrefActive.edit();
                editorActive.putString("username", l);
                editorActive.putString("password", p);
                editorActive.apply();

                startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                this.finish();
            }else{
                CharSequence textError2 =  getString(R.string.invalidData);
                toast = Toast.makeText(MainActivity.this, textError2, duration);
                toast.show();
            }
        }
    }

    public void onClickRegistration (View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
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
