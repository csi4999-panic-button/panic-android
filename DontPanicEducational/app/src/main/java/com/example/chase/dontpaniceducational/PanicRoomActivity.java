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
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class PanicRoomActivity extends AppCompatActivity {
    private TextView numberOfPanicStudents;
    private Socket panicSocket, questionSocket;
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
    private Classes classes = new Classes();
    private Set<String> questionSet = new Set<String>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @NonNull
        @Override
        public Iterator<String> iterator() {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] a) {
            return null;
        }

        @Override
        public boolean add(String s) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends String> c) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    };
    private ArrayList questionArray = new ArrayList();
    private Button panicButton;

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
                questionSocket = IO.socket(request.website());
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
        questionSocket.on("new_question", questionListener);
        questionSocket.connect();
        numberOfPanicStudents.setText("0");
        panicSocket.emit("login", apiToken);
        panicState = false;
        questionButton = (FloatingActionButton) findViewById(R.id.questionFAB);
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
        questionArray.addAll(mySharedPreferences.getStringSet("questions", null));
        panicButton = (Button) findViewById(R.id.button_panicButton);
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

    private Emitter.Listener questionListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            PanicRoomActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String classID, questionId, questionString;
                    int numberOfQuestions = 0;
                    try {
                        classID = data.getString("classroom");
                        questionId = data.getString("questionId");
                        questionString = data.getString("questionStr");
                        numberOfQuestions = data.getInt("numberOfQuestions");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    questionArray.add(questionString);
                    adapter = new PanicRoomActivity.CustomAdapter(questionArray);
                    listView.setAdapter(adapter);
                }
            });
        }
    };

    protected void onStart() {
        super.onStart();
        panicSocket.emit("login", token);
    }

    protected void onResume() {
        super.onResume();
        adapter = new PanicRoomActivity.CustomAdapter(questionArray);
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
        private ArrayList<String> questions = new ArrayList<>();

        CustomAdapter() {
            questions.clear();
        }

        public CustomAdapter(ArrayList<String> questionList) {
            questions.addAll(questionList);
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
            row = inflater.inflate(R.layout.question_list_row, parent, false);
            TextView questionText;
            questionText = (TextView) row.findViewById(R.id.question);
            questionText.setText(questions.get(position));
            return (row);
        }
    }
}