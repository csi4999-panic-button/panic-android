package com.example.chase.dontpaniceducational;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class AnswerActivity extends AppCompatActivity {

    Button sendAnswer, resetAnswer;
    private EditText answer;
    private String answerGiven, url = "http://www.panic-button.stream/api/v1/classrooms/", classroom;
    private SharedPreferences mySharedPreferences;
    public static String MY_PREFS = "MY_PREFS";
    int prefMode = CreateClassActivity.MODE_PRIVATE;
    private Intent intent;
    Question questionObject = new Question();
    Ion ion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        intent = getIntent();
        questionObject = (Question) intent.getSerializableExtra("questionObject");
        answer = (EditText) findViewById(R.id.editText_Answer);
        mySharedPreferences = getSharedPreferences(MY_PREFS, prefMode);
        classroom = intent.getSerializableExtra("classroom").toString();
        url = url.concat(classroom).concat("/questions/").concat(
                questionObject.getQuestionId()).concat("/answers");
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