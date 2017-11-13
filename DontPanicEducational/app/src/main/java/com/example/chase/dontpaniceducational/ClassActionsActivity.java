package com.example.chase.dontpaniceducational;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class ClassActionsActivity extends AppCompatActivity {
    private ListView listView;
    //private ArrayAdapter<ClassListView> arrayAdapter;
    private SharedPreferences mySharedPreferences;
    public static String MY_PREFS = "MY_PREFS";
    int prefMode = JoinClassActivity.MODE_PRIVATE;
    private String token;
    private JsonElement jsonElement;
    private JsonObject jsonObject;
    private ArrayList<String> classIds;
    private ClassListView classObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_actions);
        mySharedPreferences = getSharedPreferences(MY_PREFS, prefMode);
        final SharedPreferences.Editor editor = mySharedPreferences.edit();
        token = mySharedPreferences.getString("token", null);
        token = token.substring(1, token.length() - 1);
        token = "Bearer ".concat(token);
        classIds = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView_classesJoined);
        //arrayAdapter = new ArrayAdapter<ClassListView>(this, R.layout.activity_list_view, classObject);
        //listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putString("classroom", listView.getItemAtPosition(i).toString());
                editor.commit();
                Intent intent = new Intent(ClassActionsActivity.this, PanicRoomActivity.class);
                startActivity(intent);
            }
        });
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

    private void updateClassList(Context c){
        Ion.with(this)
                .load("http://www.panic-button.stream/api/v1/classrooms")
                .setHeader("Authorization", token)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        Log.d("Ion","Received response from request");
                        if (e != null) {
                            Toast.makeText(ClassActionsActivity.this, "Try again",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d("Ion","Generating classIds");
                        //arrayAdapter.clear();
                        String classElement;
                        for (int i = 0; i < result.size(); i++) {
                            jsonObject = result.get(i).getAsJsonObject();
                            classObject = new ClassListView();
                            classElement = jsonObject.get("_id").toString();
                            classElement = classElement.substring(1, classElement.length() - 1);
                            classObject.setClassId(classElement);
                            classElement = jsonObject.get("schoolId").toString();
                            classElement = classElement.substring(1, classElement.length() - 1);
                            classObject.setSchoolId(classElement);
                            classElement = jsonObject.get("courseType").toString();
                            classElement = classElement.substring(1, classElement.length() - 1);
                            classObject.setCourseType(classElement);
                            classElement = jsonObject.get("courseNumber").toString();
                            classElement = classElement.substring(1, classElement.length() - 1);
                            classObject.setCourseNumber(classElement);
                            classElement = jsonObject.get("sectionNumber").toString();
                            classElement = classElement.substring(1, classElement.length() - 1);
                            classObject.setSectionNumber(classElement);
                            classElement = jsonObject.get("courseTitle").toString();
                            classElement = classElement.substring(1, classElement.length() - 1);
                            classObject.setCourseType(classElement);
                            //arrayAdapter.add(classObject);
                        }
                        Log.d("Ion","Successfully generated classIds");
                    }
                });
    }
}