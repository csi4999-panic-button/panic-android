package com.example.chase.dontpaniceducational;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private EditText loginUsername, loginPassword;
    private SharedPreferences mySharedPreferences;
    public static String MY_PREFS = "MY_PREFS";
    int prefMode = CreateClassActivity.MODE_PRIVATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginUsername = (EditText) findViewById(R.id.editText_login);
        loginPassword = (EditText) findViewById(R.id.editText_passwordLogin);
        mySharedPreferences = getSharedPreferences(MY_PREFS, prefMode);
    }

    public void clearFieldsLogin(View view) {
        loginUsername.setText("");
        loginPassword.setText("");
    }

    public void loginButtonClick(View view) {
        String username = loginUsername.getText().toString();
        String password = loginPassword.getText().toString();
        if (username.matches("") || password.matches("")) {
            Toast.makeText(this, "You cannot continue because one of the fields is empty.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("email",username);
        json.addProperty("password",password);
        Ion.with(this)
                .load("http://www.panic-button.stream/api/v1/authenticate")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null || !result.has("token")) {
                            Toast.makeText(LoginActivity.this, "Your email or password are not valid. Try again",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("token", result.get("token").toString());
                        editor.commit();
                        Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, ClassActionsActivity.class);
                        startActivity(intent);
                    }
                });
    }
}