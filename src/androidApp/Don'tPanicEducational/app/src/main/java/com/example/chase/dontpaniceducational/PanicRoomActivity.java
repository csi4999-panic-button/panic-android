package com.example.chase.dontpaniceducational;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.net.URISyntaxException;

public class PanicRoomActivity extends AppCompatActivity {
    private TextView numberOfPanicStudents;
    private Socket panicSocket;
    private SharedPreferences mySharedPreferences;
    public static String MY_PREFS = "MY_PREFS";
    int prefMode = JoinClassActivity.MODE_PRIVATE;
    private String classroom, token;
    private JsonObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_room);
        {
            try {
                panicSocket = IO.socket("http://www.panic-button.stream");
            } catch(URISyntaxException e){
                e.printStackTrace();
            }
        }
        mySharedPreferences = getSharedPreferences(MY_PREFS, prefMode);
        classroom = mySharedPreferences.getString("classroom", null);
        token = mySharedPreferences.getString("token", null);
        token = token.substring(1, token.length() - 1);
        token = "Bearer ".concat(token);
        numberOfPanicStudents = (TextView) findViewById(R.id.textView_numberOfPanickedStudents);
        jsonObject = new JsonObject();
        panicSocket.on("panic", panicListener);
        panicSocket.on("connect", connectListener);
        panicSocket.connect();
    }

    private Emitter.Listener panicListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            PanicRoomActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JsonObject data = (JsonObject) args[0];
                    String classId;
                    int numberOfPanics;
                    try {
                        classId = data.get("classroom").toString();
                        numberOfPanics = data.get("panicNumber").getAsInt();
                    } catch (JsonIOException e) {
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

    @Override
    protected void onStart() {
        super.onStart();
        panicSocket.emit("login", token);
    }

    public void panicButtonClick(View view) {
        jsonObject.addProperty("classroom", classroom);
        jsonObject.addProperty("state", "true");
        panicSocket.emit("panic", jsonObject);
        String something = "Chase";
    }
}
