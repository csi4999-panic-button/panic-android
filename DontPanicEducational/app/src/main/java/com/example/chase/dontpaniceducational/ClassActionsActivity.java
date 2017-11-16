package com.example.chase.dontpaniceducational;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.util.ArrayList;

public class ClassActionsActivity extends AppCompatActivity {
    private ListView listView;
    private SharedPreferences mySharedPreferences;
    public static String MY_PREFS = "MY_PREFS";
    int prefMode = JoinClassActivity.MODE_PRIVATE;
    private String token;
    private JsonObject jsonObject;
    private ArrayList<String> classIds;
<<<<<<< HEAD
    private ClassListView classObject;
    private ArrayList<ClassListView> classObjectsArray;
    private ArrayList<String> courseTypeArray, courseNumberArray, classTitleArray;
=======
    private Classes classObject;
    private ArrayList<Classes> classObjectsArray;
    private ArrayList<String> courseTypeArray, courseNumberArray, courseTitleArray;
    private ArrayList<String> emptyArrayList = new ArrayList<>(1);
>>>>>>> cd7dce6b9a4fe8993b8fe825d2c9245b364e8fcd
    private CustomAdapter adapter;
    private RestRequests request = new RestRequests();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_actions);
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
                editor.putString("classroom", classObject.getClassId());
                editor.putString("courseType", classObject.getCourseType());
                editor.putString("courseNumber", classObject.getCourseNumber());
                editor.commit();
                Intent intent = new Intent(ClassActionsActivity.this, PanicRoomActivity.class);
                startActivity(intent);
            }
        });
        classObjectsArray = new ArrayList<>();
        courseTypeArray = new ArrayList<>();
        courseNumberArray = new ArrayList<>();
        classTitleArray = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateClassList(this);
    }

    public void createClass(View view) {
        Intent intent = new Intent(this, CreateClassActivity.class);
        startActivity(intent);
    }

    public void joinClass(View view) {
        Intent intent = new Intent(this, JoinClassActivity.class);
        startActivity(intent);
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
                        for (int i = 0; i < result.size(); i++) {
                            jsonObject = result.get(i).getAsJsonObject();
                            classObject = new Classes();
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