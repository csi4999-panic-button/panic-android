package com.example.chase.dontpaniceducational;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class PanicRoomActivity extends AppCompatActivity implements Serializable {
    private TextView numberOfPanicStudents;
    private Socket panicSocket;
    SharedPreferences mySharedPreferences;
    public static String MY_PREFS = "MY_PREFS";
    int prefMode = JoinClassActivity.MODE_PRIVATE;
    private String classroom, token, apiToken;
    private JsonObject jsonObject;
    private boolean panicState;
    private RestRequests request = new RestRequests();
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barDrawerToggle;
    String panicClassName;
    FloatingActionButton questionButton;
    ListView listView;
    CustomAdapter adapter;
    private Button panicButton;
    private Intent intent;
    Classroom classroomObject;
    Question questionObject;
    private ArrayList<Question> questions = new ArrayList<>();
    private ArrayList<Answer> answers = new ArrayList<>();
    private int totalNumberOfQuestions = 0;
    private ClassActionsActivity classActionsActivityObject = new ClassActionsActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_room);
        questionObject = new Question();
        intent = getIntent();
        classroomObject = (Classroom) intent.getSerializableExtra("classroomObject");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutPanic);
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        barDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(barDrawerToggle);
        barDrawerToggle.syncState();
        NavigationView nav_view = (NavigationView) findViewById(R.id.navViewPanic);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.account)
                    Toast.makeText(PanicRoomActivity.this, "Success", Toast.LENGTH_SHORT).show();
                else if(id == R.id.logout)
                    Toast.makeText(PanicRoomActivity.this, "Success", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mySharedPreferences = getSharedPreferences(MY_PREFS, prefMode);
        panicClassName = classroomObject.getCourseType();
        getSupportActionBar().setTitle(panicClassName);
        {
            try {
                panicSocket = IO.socket(request.website());
            } catch(URISyntaxException e){
                e.printStackTrace();
            }
        }
        classroom = classroomObject.getClassId();
        token = mySharedPreferences.getString("token", null);
        token = token.substring(1, token.length() - 1);
        apiToken = token;
        token = "Bearer ".concat(token);
        numberOfPanicStudents = (TextView) findViewById(R.id.textView_numberOfPanickedStudents);
        jsonObject = new JsonObject();
        panicSocket.on("panic", panicListener);
        panicSocket.on("connect", connectListener);
        panicSocket.on("login_success", loginListener);
        panicSocket.on("new_question", newQuestionListener);
        panicSocket.on("new_answer", newAnswerListener);
        panicSocket.connect();
        numberOfPanicStudents.setText("0");
        panicSocket.emit("login", apiToken);
        panicState = false;
        questionButton = (FloatingActionButton) findViewById(R.id.questionFAB);
        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanicRoomActivity.this, QuestionActivity.class);
                intent.putExtra("classroom", classroom);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.questionsList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                questionObject = questions.get(i);
                Intent intent = new Intent(PanicRoomActivity.this, AnswerActivity.class);
                intent.putExtra("questionObject", questionObject);
                intent.putExtra("classroom", classroom);
                startActivity(intent);
            }
        });
        panicButton = (Button) findViewById(R.id.button_panicButton);
        adapter = new PanicRoomActivity.CustomAdapter(classroomObject);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return barDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private Emitter.Listener panicListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            PanicRoomActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String classID;
                    String numberOfPanics;
                    try {
                        classID = data.get("classroom").toString();
                        numberOfPanics = data.get("panicNumber").toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    numberOfPanicStudents.setText(numberOfPanics);
                }
            });
        }
    };

    private Emitter.Listener connectListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            PanicRoomActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                    } catch (JsonIOException e) {
                        return;
                    }
                    Toast.makeText(PanicRoomActivity.this, "Connected",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener loginListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            PanicRoomActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                    } catch (JsonIOException e) {
                        return;
                    }
                    Toast.makeText(PanicRoomActivity.this, "Logged In",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener newQuestionListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            PanicRoomActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String classID, questionId, questionString;
                    int numberOfQuestions = 0;
                    Question question = new Question();
                    ArrayList<Answer> emptyAnswerList = new ArrayList<>();
                    try {
                        classID = data.getString("classroom");
                        questionId = data.getString("questionId");
                        questionString = data.getString("questionStr");
                        numberOfQuestions = data.getInt("numberOfQuestions");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Ion.with(PanicRoomActivity.this)
                            .load(request.classrooms().concat("/").concat(classID))
                            .setHeader("Authorization", token)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    ArrayList<Question> updatedQuestionList = questions;
                                    ArrayList<Answer> answerArrayList = new ArrayList<>();
                                    ArrayList<String> votesArrayList = new ArrayList<>();
                                    JsonObject newJsonQuestion, newJsonAnswer;
                                    JsonArray jsonQuestionArray = new JsonArray();
                                    JsonArray jsonAnswerArray;
                                    JsonArray jsonVotesArray = new JsonArray();
                                    JsonElement jsonQuestionArrayElement, jsonAnswerArrayElement;
                                    Question newQuestion = new Question();
                                    Answer answer = new Answer();
                                    if (e != null) {
                                        Toast.makeText(PanicRoomActivity.this, "Try again",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (result.has("questions")) {
                                        jsonQuestionArray.addAll(result.get("questions").getAsJsonArray());
                                        jsonQuestionArrayElement = jsonQuestionArray.get(jsonQuestionArray.size() - 1);
                                        newJsonQuestion = jsonQuestionArrayElement.getAsJsonObject();
                                        newQuestion.setResolution(newJsonQuestion.get("resolution").getAsInt());
                                        newQuestion.setVoted(newJsonQuestion.get("voted").getAsBoolean());
                                        newQuestion.setVotes(newJsonQuestion.get("votes").getAsJsonArray().size());
                                        newQuestion.setUser(newJsonQuestion.get("user").getAsString());
                                        newQuestion.setQuestion(newJsonQuestion.get("question").getAsString());
                                        newQuestion.setQuestionId(newJsonQuestion.get("_id").getAsString());
                                        jsonAnswerArray = newJsonQuestion.get("answers").getAsJsonArray();
                                        for (JsonElement answerElement : jsonAnswerArray) {
                                            newJsonAnswer = answerElement.getAsJsonObject();
                                            answer.setMine(newJsonAnswer.get("mine").getAsBoolean());
                                            answer.setResolution(newJsonAnswer.get("isResolution").getAsBoolean());
                                            jsonVotesArray = newJsonAnswer.get("votes").getAsJsonArray();
                                            for (JsonElement vote : jsonVotesArray)
                                                votesArrayList.add(vote.getAsString());
                                            answer.setVotes(votesArrayList);
                                            answer.setId(newJsonAnswer.get("_id").getAsString());
                                            answer.setUser(newJsonAnswer.get("user").getAsString());
                                            answer.setAnswer(newJsonAnswer.get("answer").getAsString());
                                            answerArrayList.add(answer);
                                        }
                                        newQuestion.setAnswerList(answerArrayList);
                                        newQuestion.setMine(newJsonQuestion.get("mine").getAsBoolean());
                                        updatedQuestionList.add(newQuestion);
                                    }
                                    classroomObject.setQuestions(updatedQuestionList);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }
            });
        }
    };

    private Emitter.Listener newAnswerListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            PanicRoomActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    final String classroomId, questionId, answerId, answerString;
                    int numberOfAnswers = 0;
                    Answer answer = new Answer();
                    ArrayList<String> emptyAnswerList = new ArrayList<>();
                    try {
                        classroomId = data.getString("classroom");
                        questionId = data.getString("questionId");
                        answerId = data.getString("answerId");
                        answerString = data.getString("answerStr");
                        numberOfAnswers = data.getInt("numberOfAnswers");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Ion.with(PanicRoomActivity.this)
                            .load(request.classrooms().concat("/").concat(classroomId))
                            .setHeader("Authorization", token)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    JsonArray jsonQuestionArray, jsonAnswerArray, jsonVotesArray;
                                    JsonObject jsonQuestionObject, jsonAnswerObject;
                                    JsonElement jsonAnswerElement;
                                    Answer answer = new Answer();
                                    ArrayList<Answer> updatedAnswerArrayList = new ArrayList<>();
                                    ArrayList<String> votesArrayList = new ArrayList<>();
                                    if (e != null) {
                                        Toast.makeText(PanicRoomActivity.this, "Try again",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if(result.has("questions")) {
                                        jsonQuestionArray = result.get("questions").getAsJsonArray();
                                        for(JsonElement question : jsonQuestionArray) {
                                            jsonQuestionObject = question.getAsJsonObject();
                                            if(jsonQuestionObject.get("_id").getAsString().matches(questionId)) {
                                                jsonAnswerArray = jsonQuestionObject.get("answers").getAsJsonArray();
                                                jsonAnswerElement = jsonAnswerArray.get(jsonAnswerArray.size() - 1);
                                                jsonAnswerObject = jsonAnswerElement.getAsJsonObject();
                                                answer.setAnswer(jsonAnswerObject.get("answer").getAsString());
                                                answer.setUser(jsonAnswerObject.get("user").getAsString());
                                                answer.setId(jsonAnswerObject.get("_id").getAsString());
                                                jsonVotesArray = jsonAnswerObject.get("votes").getAsJsonArray();
                                                for (JsonElement vote : jsonVotesArray)
                                                    votesArrayList.add(vote.getAsString());
                                                answer.setVotes(votesArrayList);
                                                answer.setResolution(jsonAnswerObject.get("isResolution").getAsBoolean());
                                                answer.setMine(jsonAnswerObject.get("mine").getAsBoolean());
                                            }
                                        }
                                    }
                                    for(Question question : questions) {
                                        if(question.getQuestionId().matches(questionId)) {
                                            updatedAnswerArrayList.addAll(question.getAnswerList());
                                            updatedAnswerArrayList.add(answer);
                                            question.setAnswerList(updatedAnswerArrayList);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }
            });
        }
    };

    protected void onStart() {
        super.onStart();
        panicSocket.emit("login", apiToken);
    }

    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    public void panicButtonClick(View view) {
        panicState = !panicState;
        if(panicState) {
            numberOfPanicStudents.setTextColor(Color.RED);
            panicButton.setText("!");
        }
        else {
            numberOfPanicStudents.setTextColor(Color.parseColor("#FFFFFF"));
            panicButton.setText("?");
        }
        jsonObject.addProperty("classroom", classroom);
        jsonObject.addProperty("state", panicState);
        panicSocket.emit("panic", jsonObject);
    }

    public class CustomAdapter extends BaseAdapter {

        private ArrayList<Question> questionArrayList = new ArrayList<>();

        public CustomAdapter(Classroom classroomObjectAdapter) {
            questions.clear();
            this.questionArrayList = classroomObjectAdapter.getQuestions();
            questions = this.questionArrayList;
        }

        @Override
        public int getCount() {
            return questions.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row;
            int numberOfAnswers;
            row = inflater.inflate(R.layout.question_list_row, parent, false);
            TextView questionText, numberOfAnswersTV;
            questionText = (TextView) row.findViewById(R.id.question);
            numberOfAnswersTV = (TextView) row.findViewById(R.id.numberOfAnswersTextView);
            questionText.setText(this.questionArrayList.get(position).getQuestion());
            numberOfAnswers = this.questionArrayList.get(position).getAnswerList().size();
            numberOfAnswersTV.setText(String.valueOf(numberOfAnswers));
            for(Answer answer : this.questionArrayList.get(position).getAnswerList()) {
                if(answer.isMine()) {
                    numberOfAnswersTV.setBackgroundColor(Color.parseColor("#00ff00"));
                    numberOfAnswersTV.setTextColor(Color.parseColor("#000000"));
                }
                if(answer.getUser().matches(mySharedPreferences.getString("userId", null)))
                    answer.setMine(true);
            }
            return (row);
        }
    }
}