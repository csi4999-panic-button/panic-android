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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.net.URISyntaxException;

public class PanicRoomActivity extends AppCompatActivity {
    private TextView numberOfPanicStudents;
    private Socket panicSocket;
    private SharedPreferences mySharedPreferences;
    public static String MY_PREFS = "MY_PREFS";
    int prefMode = JoinClassActivity.MODE_PRIVATE;
    private String classroom, token, apiToken;
    private JsonObject jsonObject;
    private boolean panicState;
    private RestRequests request = new RestRequests();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barDrawerToggle;
    private String panicClassName;
    private FloatingActionButton questionButton;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_room);
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
        panicClassName = mySharedPreferences.getString("courseType", null);
        panicClassName = panicClassName.concat(" ").concat(mySharedPreferences.getString("courseNumber", null));
        getSupportActionBar().setTitle(panicClassName);
        {
            try {
                panicSocket = IO.socket(request.website());
            } catch(URISyntaxException e){
                e.printStackTrace();
            }
        }
        mySharedPreferences = getSharedPreferences(MY_PREFS, prefMode);
        classroom = mySharedPreferences.getString("classroom", null);
        token = mySharedPreferences.getString("token", null);
        token = token.substring(1, token.length() - 1);
        apiToken = token;
        token = "Bearer ".concat(token);
        numberOfPanicStudents = (TextView) findViewById(R.id.textView_numberOfPanickedStudents);
        jsonObject = new JsonObject();
        panicSocket.on("panic", panicListener);
        panicSocket.on("connect", connectListener);
        panicSocket.on("login_success", loginListener);
        panicSocket.connect();
        numberOfPanicStudents.setText("0");
        panicSocket.emit("login", apiToken);
        panicState = false;
        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanicRoomActivity.this, QuestionActivity.class);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.questionsList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
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
                    Toast.makeText(PanicRoomActivity.this, "Success",
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
                    Toast.makeText(PanicRoomActivity.this, "logged in",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        panicSocket.emit("login", token);
    }
    public void panicButtonClick(View view) {
        panicState = !panicState;
        jsonObject.addProperty("classroom", classroom);
        jsonObject.addProperty("state", panicState);
        panicSocket.emit("panic", jsonObject);
    }
}