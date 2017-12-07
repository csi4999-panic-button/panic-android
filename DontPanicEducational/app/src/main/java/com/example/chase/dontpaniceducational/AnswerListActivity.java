package com.example.chase.dontpaniceducational;

import android.content.Intent;
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
import java.util.ArrayList;

public class AnswerListActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barDrawerToggle;
    private FloatingActionButton answerButton;
    private ListView listView;
    private CustomAdapter adapter;
    private ArrayList<Answer> answerArrayList = new ArrayList<>();
    private Intent intent;
    private Question questionObject = new Question();
    private Classroom classroom = new Classroom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        intent = getIntent();
        answerArrayList = (ArrayList<Answer>) intent.getSerializableExtra("answerArrayList");
        questionObject = (Question) intent.getSerializableExtra("questionObject");
        classroom = (Classroom) intent.getSerializableExtra("classroom");
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
                    Toast.makeText(AnswerListActivity.this, "Success", Toast.LENGTH_SHORT).show();
                else if(id == R.id.logout)
                    Toast.makeText(AnswerListActivity.this, "Success", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Something");
        answerButton = (FloatingActionButton) findViewById(R.id.answerFAB);
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnswerListActivity.this, AnswerActivity.class);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.answerList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AnswerListActivity.this, AnswerActivity.class);
                intent.putExtra("questionObject", questionObject);
                intent.putExtra("classroom", classroom);
                startActivity(intent);
            }
        });
        adapter = new CustomAdapter(answerArrayList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return barDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    public class CustomAdapter extends BaseAdapter {

        private ArrayList<Answer> updatedAnswerArrayList = new ArrayList<>();

        public CustomAdapter(ArrayList<Answer> answerArrayListAdapter) {
            this.updatedAnswerArrayList = answerArrayListAdapter;
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