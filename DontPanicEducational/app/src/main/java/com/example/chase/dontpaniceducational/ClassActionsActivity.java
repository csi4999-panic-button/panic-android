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
import android.util.Log;
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
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClassActionsActivity extends AppCompatActivity {
    private ListView listView;
    SharedPreferences mySharedPreferences;
    public static String MY_PREFS = "MY_PREFS";
    int prefMode = JoinClassActivity.MODE_PRIVATE;
    private String token;
    private JsonObject jsonObject, jsonQuestion, jsonAnswer;
    private ArrayList<String> classIds;
    private Classes classObject;
    private ArrayList<Classes> classObjectsArray;
    ArrayList<String> courseTypeArray, courseNumberArray, classTitleArray, questionsArray, answerArrayString;
    ArrayList<Integer> answerArrayInt;
    private CustomAdapter adapter;
    private RestRequests request = new RestRequests();
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barDrawerToggle;
    FloatingActionButton optionsButton, joinClassButton, createClassButton;
    private LinearLayout joinLayout, createLayout;
    private JsonArray classElementArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_actions);
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
                Intent intent = new Intent(ClassActionsActivity.this, JoinClassActivity.class);
                startActivity(intent);
            }
        });
        createClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassActionsActivity.this, CreateClassActivity.class);
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
                    Toast.makeText(ClassActionsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                else if(id == R.id.logout)
                    Toast.makeText(ClassActionsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Classrooms");
        mySharedPreferences = getSharedPreferences(MY_PREFS, prefMode);
        final SharedPreferences.Editor editor = mySharedPreferences.edit();
        token = mySharedPreferences.getString("token", null);
        token = token.substring(1, token.length() - 1);
        token = "Bearer ".concat(token);
        classIds = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView_classesJoined);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                classObject = classObjectsArray.get(i);
                TinyDB tinyDB = new TinyDB(ClassActionsActivity.this);
                for(Integer answer : classObject.getAnswers())
                    answerArrayString.add(answer.toString());
                editor.putString("classroom", classObject.getClassId());
                editor.putString("courseType", classObject.getCourseType());
                editor.putString("courseNumber", classObject.getCourseNumber());
                editor.commit();
                tinyDB.putListString("questions", classObject.getQuestions());
                tinyDB.putListString("answers", answerArrayString);
                Intent intent = new Intent(ClassActionsActivity.this, PanicRoomActivity.class);
                startActivity(intent);
            }
        });
        classObjectsArray = new ArrayList<>();
        courseTypeArray = new ArrayList<>();
        courseNumberArray = new ArrayList<>();
        classTitleArray = new ArrayList<>();
        questionsArray = new ArrayList<>();
        answerArrayInt = new ArrayList<>();
        jsonAnswer = new JsonObject();
        answerArrayString = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return barDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateClassList(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        joinLayout.setVisibility(View.GONE);
        createLayout.setVisibility(View.GONE);
    }

    private void updateClassList(Context c) {
        Ion.with(this)
                .load(request.classrooms())
                .setHeader("Authorization", token)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        Log.d("Ion", "Received response from request");
                        if (e != null) {
                            Toast.makeText(ClassActionsActivity.this, "Try again",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d("Ion", "Generating classIds");
                        String classElement;
                        JsonObject emptyObject = new JsonObject();
                        emptyObject.addProperty("empty", "");
                        for (int i = 0; i < result.size(); i++) {
                            jsonObject = result.get(i).getAsJsonObject();
                            classObject = new Classes();
                            classElementArray = new JsonArray();
                            jsonQuestion = new JsonObject();
                            if (jsonObject.has("_id")) {
                                classElement = jsonObject.get("_id").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classObject.setClassId(classElement);
                            if (jsonObject.has("schoolId")) {
                                classElement = jsonObject.get("schoolId").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classObject.setSchoolId(classElement);
                            if (jsonObject.has("courseType")) {
                                classElement = jsonObject.get("courseType").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classObject.setCourseType(classElement);
                            if (jsonObject.has("courseNumber")) {
                                classElement = jsonObject.get("courseNumber").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classObject.setCourseNumber(classElement);
                            if (jsonObject.has("sectionNumber")) {
                                classElement = jsonObject.get("sectionNumber").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classObject.setSectionNumber(classElement);
                            if (jsonObject.has("courseTitle")) {
                                classElement = jsonObject.get("courseTitle").toString();
                                classElement = classElement.substring(1, classElement.length() - 1);
                            } else
                                classElement = "";
                            classObject.setCourseTitle(classElement);
                            if(jsonObject.getAsJsonArray("questions").size() > 0) {
                                classElementArray = jsonObject.getAsJsonArray("questions");
                                for(int j = 0; j < classElementArray.size(); j++) {
                                    jsonQuestion = classElementArray.get(j).getAsJsonObject();
                                    answerArrayInt.add(jsonQuestion.getAsJsonArray("answers").size());
                                    questionsArray.add(jsonQuestion.get("question").toString());
                                }
                                classObject.setQuestions(questionsArray);
                                classObject.setAnswers(answerArrayInt);
                                classObjectsArray.add(classObject);
                                courseTypeArray.add(classObject.getCourseType());
                                courseNumberArray.add(classObject.getCourseNumber());
                                classTitleArray.add(classObject.getCourseTitle());
                                break;
                            }
                            classObject.setQuestions(questionsArray);
                            classObject.setAnswers(answerArrayInt);
                            classObjectsArray.add(classObject);
                            courseTypeArray.add(classObject.getCourseType());
                            courseNumberArray.add(classObject.getCourseNumber());
                            classTitleArray.add(classObject.getCourseTitle());
                        }
                        adapter = new CustomAdapter(classObjectsArray);
                        listView.setAdapter(adapter);
                        Log.d("Ion", "Successfully generated classIds");
                    }
                });
    }

    public class CustomAdapter extends BaseAdapter {
        private ArrayList<String> courseTitle = new ArrayList<>();
        private ArrayList<String> courseType = new ArrayList<>();
        private ArrayList<String> courseNumber = new ArrayList<>();

        CustomAdapter() {
            courseTitle.clear();
            courseType.clear();
            courseNumber.clear();
        }

        public CustomAdapter(ArrayList<Classes> classList) {
            for (Classes classes : classList) {
                courseTitle.add(classes.getCourseTitle());
                courseType.add(classes.getCourseType());
                courseNumber.add(classes.getCourseNumber());
            }
        }

        @Override
        public int getCount() {
            return courseType.size();
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
            row = inflater.inflate(R.layout.list_row, parent, false);
            TextView classType, classNumber, classTitle;
            classType = (TextView) row.findViewById(R.id.classType);
            classNumber = (TextView) row.findViewById(R.id.classNumber);
            classTitle = (TextView) row.findViewById(R.id.classTitle);
            classType.setText(courseType.get(position));
            classNumber.setText(courseNumber.get(position));
            classTitle.setText(courseTitle.get(position));
            return (row);
        }
    }
}