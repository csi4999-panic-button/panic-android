package com.example.chase.dontpaniceducational;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class AnswerActivity extends AppCompatActivity {

    private EditText answer;
    private String answerGiven, url = "http://www.panic-button.stream/api/v1/classrooms/", classroom;
    private Intent intent;
    Question questionObject = new Question();
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        intent = getIntent();
        questionObject = (Question) intent.getSerializableExtra("questionObject");
        answer = (EditText) findViewById(R.id.editText_Answer);
        classroom = intent.getSerializableExtra("classroom").toString();
        url = url.concat(classroom).concat("/questions/").concat(
                questionObject.getQuestionId()).concat("/answers");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutAnswer);
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        barDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(barDrawerToggle);
        barDrawerToggle.syncState();
        NavigationView nav_view = (NavigationView) findViewById(R.id.navViewAnswer);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.logout) {
                    Intent intent = new Intent(AnswerActivity.this, StartScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Answer");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return barDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void clearAnswer(View view) {
        answer.setText("");
    }

    public void answerToGive(View view) {
        answerGiven = answer.getText().toString();
        if(answerGiven.equals("")) {
            Toast.makeText(this, "You cannot continue because you don't have an answer.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("classroomId", classroom);
        json.addProperty("questionId", questionObject.getQuestionId());
        json.addProperty("answer", answerGiven);
        Ion.with(this)
                .load(url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(AnswerActivity.this,
                                    "Your answer is not valid. Try again",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(AnswerActivity.this, "Answer Posted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}