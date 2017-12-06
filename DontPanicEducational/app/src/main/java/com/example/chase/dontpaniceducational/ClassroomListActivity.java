package com.example.chase.dontpaniceducational;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class ClassroomListActivity extends AppCompatActivity {
    private ListView listView;
    SharedPreferences mySharedPreferences;
    public static String MY_PREFS = "MY_PREFS";
    int prefMode = JoinClassActivity.MODE_PRIVATE;
    private String token, editedString, userId;
    private JsonObject jsonObject, jsonQuestion, answerJsonObject;
    private Classroom classroomObject;
    private ArrayList<Classroom> classroomObjectsArray;
    ArrayList<String> courseTypeArray, courseNumberArray, classTitleArray, questionsArray, answerArrayString, voteArray;
    ArrayList<Integer> answerArrayInt;
    private ArrayList<Question> questionArrayList;
    private ArrayList<Answer> answerArrayList;
    private CustomAdapter adapter;
    private RestRequests request = new RestRequests();
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barDrawerToggle;
    FloatingActionButton optionsButton, joinClassButton, createClassButton;
    private LinearLayout joinLayout, createLayout;
    private JsonArray classQuestionJsonArray, classAnswerJsonArray, questionVotesArray;
    private Question questionObject;
    private Answer answerObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_actions);
        Ion.with(this)
                .load(request.currentUser())
                .setHeader("Authorization", token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(e != null) {
                            Toast.makeText(ClassroomListActivity.this,"Try again",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("userId", result.get("_id").getAsString());
                        editor.commit();
                    }
                });
        optionsButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        joinClassButton = (FloatingActionButton) findViewById(R.id.floatingActionButtonJoinClass);
        createClassButton = (FloatingActionButton) findViewById(R.id.floatingActionButtonCreateClass);
        joinLayout = (LinearLayout) findViewById(R.id.layoutJoinClass);
        createLayout = (LinearLayout) findViewById(R.id.layoutCreateClass);
        joinLayout.setVisibility(View.GONE);
        createLayout.setVisibility(View.GONE);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(joinLayout.getVisibility() == View.VISIBLE &&
                        createLayout.getVisibility() == View.VISIBLE) {
                    joinLayout.setVisibility(View.GONE);
                    createLayout.setVisibility(View.GONE);
                } else {
                    joinLayout.setVisibility(View.VISIBLE);
                    createLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        joinClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassroomListActivity.this, JoinClassActivity.class);
                startActivity(intent);
            }
        });
        createClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassroomListActivity.this, CreateClassActivity.class);
                startActivity(intent);
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        barDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        barDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(barDrawerToggle);
        barDrawerToggle.syncState();
        NavigationView nav_view = (NavigationView) findViewById(R.id.navView);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.account)
                    Toast.makeText(ClassroomListActivity.this, "Success", Toast.LENGTH_SHORT).show();
                else if(id == R.id.logout)
                    Toast.makeText(ClassroomListActivity.this, "Success", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Classrooms");
        mySharedPreferences = getSharedPreferences(MY_PREFS, prefMode);
        token = mySharedPreferences.getString("token", null);
        token = token.substring(1, token.length() - 1);
        token = "Bearer ".concat(token);
        listView = (ListView) findViewById(R.id.listView_classesJoined);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                classroomObject = classroomObjectsArray.get(i);
                Intent intent = new Intent(ClassroomListActivity.this, PanicRoomActivity.class);
                intent.putExtra("classroomObject", classroomObject);
                startActivity(intent);
            }
        });
        classroomObjectsArray = new ArrayList<>();
        courseTypeArray = new ArrayList<>();
        courseNumberArray = new ArrayList<>();
        classTitleArray = new ArrayList<>();
        questionsArray = new ArrayList<>();
        answerArrayInt = new ArrayList<>();
        answerArrayString = new ArrayList<>();
        voteArray = new ArrayList<>();
        questionVotesArray = new JsonArray();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return barDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new CustomAdapter(classroomObjectsArray);
        listView.setAdapter(adapter);
        updateClassList(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        joinLayout.setVisibility(View.GONE);
        createLayout.setVisibility(View.GONE);
    }

    public void updateClassList(Context c) {
        Ion.with(this)
                .load(request.classrooms())
                .setHeader("Authorization", token)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            Toast.makeText(ClassroomListActivity.this, "Try again",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String classElement;
                        JsonObject emptyObject = new JsonObject();
                        emptyObject.addProperty("empty", "");
                        for (int i = 0; i < result.size(); i++) {
                            jsonObject = result.get(i).getAsJsonObject();
                            classroomObject = new Classroom();
                            classQuestionJsonArray = new JsonArray();
                            classAnswerJsonArray = new JsonArray();
                            jsonQuestion = new JsonObject();
                            questionArrayList = new ArrayList<>();
                            answerArrayList = new ArrayList<>();
                            if (jsonObject.has("_id")) {
                                classElement = jsonObject.get("_id").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classroomObject.setClassId(classElement);
                            if (jsonObject.has("schoolId")) {
                                classElement = jsonObject.get("schoolId").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classroomObject.setSchoolId(classElement);
                            if (jsonObject.has("courseType")) {
                                classElement = jsonObject.get("courseType").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classroomObject.setCourseType(classElement);
                            if (jsonObject.has("courseNumber")) {
                                classElement = jsonObject.get("courseNumber").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classroomObject.setCourseNumber(classElement);
                            if (jsonObject.has("sectionNumber")) {
                                classElement = jsonObject.get("sectionNumber").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classroomObject.setSectionNumber(classElement);
                            if (jsonObject.has("courseTitle")) {
                                classElement = jsonObject.get("courseTitle").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classroomObject.setCourseTitle(classElement);
                            if(jsonObject.getAsJsonArray("questions").size() > 0) {
                                classQuestionJsonArray = jsonObject.getAsJsonArray("questions");
                                questionArrayList.clear();
                                for(int j = 0; j < classQuestionJsonArray.size(); j++) {
                                    questionObject = new Question();
                                    answerArrayList = new ArrayList<>();
                                    classAnswerJsonArray = new JsonArray();
                                    jsonQuestion = classQuestionJsonArray.get(j).getAsJsonObject();
                                    editedString = jsonQuestion.get("question").toString();
                                    editedString = editedString.substring(1, editedString.length() - 1);
                                    questionObject.setQuestion(editedString);
                                    editedString = jsonQuestion.get("user").toString();
                                    editedString = editedString.substring(1, editedString.length() - 1);
                                    questionObject.setUser(editedString);
                                    editedString = jsonQuestion.get("_id").toString();
                                    editedString = editedString.substring(1, editedString.length() - 1);
                                    questionObject.setQuestionId(editedString);
                                    questionVotesArray.addAll(jsonQuestion.get("votes").getAsJsonArray());
                                    questionObject.setVotes(questionVotesArray.size());
                                    questionObject.setResolution(jsonQuestion.get("resolution").getAsInt());
                                    questionObject.setVoted(jsonQuestion.get("voted").getAsBoolean());
                                    questionArrayList.add(questionObject);
                                    classAnswerJsonArray.addAll(jsonQuestion.getAsJsonArray("answers"));
                                    for(JsonElement answerElement : classAnswerJsonArray) {
                                        answerObject = new Answer();
                                        answerJsonObject = new JsonObject();
                                        answerJsonObject = answerElement.getAsJsonObject();
                                        editedString = answerJsonObject.get("answer").toString();
                                        editedString = editedString.substring(1, editedString.length() - 1);
                                        answerObject.setAnswer(editedString);
                                        editedString = answerJsonObject.get("user").toString();
                                        editedString = editedString.substring(1, editedString.length() - 1);
                                        answerObject.setUser(editedString);
                                        editedString = answerJsonObject.get("_id").toString();
                                        editedString = editedString.substring(1, editedString.length() - 1);
                                        answerObject.setId(editedString);
                                        for(JsonElement vote : answerJsonObject.get("votes").getAsJsonArray())
                                            voteArray.add(vote.toString());
                                        answerObject.setVotes(voteArray);
                                        if(answerJsonObject.get("isResolution").getAsBoolean())
                                            answerObject.setResolution(true);
                                        else
                                            answerObject.setResolution(false);
                                        if(answerJsonObject.get("mine").getAsBoolean())
                                            answerObject.setMine(true);
                                        else
                                            answerObject.setMine(false);
                                        answerArrayList.add(answerObject);
                                    }
                                    questionObject.setAnswerList(answerArrayList);
                                }
                            }
                            classroomObject.setQuestions(questionArrayList);
                            classroomObjectsArray.add(classroomObject);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public class CustomAdapter extends BaseAdapter {

        private ArrayList<Classroom> classesArrayList = new ArrayList<>();

        public CustomAdapter(ArrayList<Classroom> classList) {
            classroomObjectsArray.clear();
            this.classesArrayList = classList;
        }

        @Override
        public int getCount() {
            return this.classesArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return this.classesArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.list_row, parent, false);
            TextView classType, classNumber, classTitle;
            classType = (TextView) row.findViewById(R.id.classType);
            classNumber = (TextView) row.findViewById(R.id.classNumber);
            classTitle = (TextView) row.findViewById(R.id.classTitle);
            classType.setText(this.classesArrayList.get(position).getCourseType());
            classNumber.setText(this.classesArrayList.get(position).getCourseNumber());
            classTitle.setText(this.classesArrayList.get(position).getCourseTitle());
            return (row);
        }
    }
}