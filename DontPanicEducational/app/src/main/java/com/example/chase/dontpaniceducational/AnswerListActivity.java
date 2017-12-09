package com.example.chase.dontpaniceducational;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.net.URISyntaxException;
import java.util.ArrayList;

public class AnswerListActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barDrawerToggle;
    private FloatingActionButton answerButton;
    private ListView listView;
    private AnswerListActivity.CustomAdapter adapter;
    private ArrayList<Answer> answerArrayList = new ArrayList<>();
    private Intent intent;
    private String classroom, token, apiToken;
    private RestRequests request = new RestRequests();
    private SharedPreferences mySharedPreferences;
    public static String MY_PREFS = "MY_PREFS";
    int prefMode = JoinClassActivity.MODE_PRIVATE;
    private ArrayList<Question> questions = new ArrayList<>();
    private Socket answerSocket;
    private Question questionObject = new Question();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        mySharedPreferences = getSharedPreferences(MY_PREFS, prefMode);
        token = mySharedPreferences.getString("token", null);
        token = token.substring(1, token.length() - 1);
        apiToken = token;
        token = "Bearer ".concat(token);
        intent = getIntent();
        {
            try {
                answerSocket = IO.socket(request.website());
            } catch(URISyntaxException e){
                e.printStackTrace();
            }
        }
        answerSocket.on("new_answer", newAnswerListener);
        answerSocket.connect();
        answerSocket.emit("login", apiToken);
        questionObject = (Question) intent.getSerializableExtra("questionObject");
        classroom = (String) intent.getSerializableExtra("classroom");
        questions = (ArrayList<Question>) intent.getSerializableExtra("questionList");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutAnswerList);
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        barDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(barDrawerToggle);
        barDrawerToggle.syncState();
        NavigationView nav_view = (NavigationView) findViewById(R.id.navViewAnswerList);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.logout) {
                    Intent intent = new Intent(AnswerListActivity.this, StartScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Answer List");
        answerButton = (FloatingActionButton) findViewById(R.id.answerFAB);
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnswerListActivity.this, AnswerActivity.class);
                intent.putExtra("questionObject", questionObject);
                intent.putExtra("classroom", classroom);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.answerList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        adapter = new AnswerListActivity.CustomAdapter(questionObject);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return barDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private Emitter.Listener newAnswerListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AnswerListActivity.this.runOnUiThread(new Runnable() {
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
                    Ion.with(AnswerListActivity.this)
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
                                    ArrayList<Answer> newAnswerArrayList = questionObject.getAnswerList();
                                    ArrayList<String> votesArrayList = new ArrayList<>();
                                    if (e != null) {
                                        Toast.makeText(AnswerListActivity.this, "Try again",
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
                                                answer.setId(jsonAnswerObject.get("_id").getAsString());
                                                jsonVotesArray = jsonAnswerObject.get("votes").getAsJsonArray();
                                                for (JsonElement vote : jsonVotesArray)
                                                    votesArrayList.add(vote.getAsString());
                                                answer.setVotes(votesArrayList);
                                                answer.setResolution(jsonAnswerObject.get(
                                                        "isResolution").getAsBoolean());
                                                answer.setMine(jsonAnswerObject.get("mine").getAsBoolean());
                                            }
                                        }
                                    }
                                    for(Question question : questions) {
                                        if(question.getQuestionId().matches(questionId)) {
                                            newAnswerArrayList.clear();
                                            newAnswerArrayList.addAll(question.getAnswerList());
                                            newAnswerArrayList.add(answer);
                                            question.setAnswerList(newAnswerArrayList);
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
        answerSocket.emit("login", apiToken);
    }

    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    public class CustomAdapter extends BaseAdapter {

        private ArrayList<Answer> updatedAnswerArrayList;

        public CustomAdapter(Question questionObject) {
            updatedAnswerArrayList = questionObject.getAnswerList();
            answerArrayList.clear();
            this.updatedAnswerArrayList = questionObject.getAnswerList();
            answerArrayList = this.updatedAnswerArrayList;
        }

        @Override
        public int getCount() {
            return this.updatedAnswerArrayList.size();
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
            row = inflater.inflate(R.layout.question_list_row, parent, false);
            TextView answerText;
            answerText = (TextView) row.findViewById(R.id.question);
            answerText.setText(this.updatedAnswerArrayList.get(position).getAnswer());
            return (row);
        }
    }
}