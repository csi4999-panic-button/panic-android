package com.example.chase.dontpaniceducational;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class QuestionActivity extends AppCompatActivity {

    Button sendQuestion, resetQuestion;
    private EditText question;
    private String questionAsked, classroomId;
    private String url = "http://www.panic-button.stream/api/v1/classrooms/";
    Ion ion;
    private boolean state;
    private Intent intent;
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        intent = getIntent();
        classroomId = intent.getSerializableExtra("classroom").toString();
        sendQuestion = (Button) findViewById(R.id.sendQuestion);
        resetQuestion = (Button) findViewById(R.id.resetQuestion);
        question = (EditText) findViewById(R.id.questionToAsk);
        ion = Ion.getDefault(QuestionActivity.this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutQuestion);
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        barDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(barDrawerToggle);
        barDrawerToggle.syncState();
        NavigationView nav_view = (NavigationView) findViewById(R.id.navViewQuestion);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.logout) {
                    Intent intent = new Intent(QuestionActivity.this, StartScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Question?");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return barDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void clearQuestion(View view) {
        question.setText("");
    }

    public void questionToAsk(View view) {
        questionAsked = question.getText().toString();
        if (questionAsked.matches("")) {
            Toast.makeText(this, "You cannot continue because you don't have a question.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        url = url.concat(classroomId).concat("/questions");
        JsonObject json = new JsonObject();
        json.addProperty("question", questionAsked);
        Ion.with(this)
                .load(url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        state = result.get("success").getAsBoolean();
                        if (e != null || !state) {
                            Toast.makeText(QuestionActivity.this,
                                    "Your question is not valid. Try again",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(QuestionActivity.this, "Question Posted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}