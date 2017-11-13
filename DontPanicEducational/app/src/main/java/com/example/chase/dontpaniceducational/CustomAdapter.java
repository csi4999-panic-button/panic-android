package com.example.chase.dontpaniceducational;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private ArrayList<ClassListView> classDataSet;
    private Activity context;
    private String courseType, courseNumber, courseTitle;

    public CustomAdapter(Activity context, String courseType, String courseNumber, String courseTitle) {
        this.context = context;
        this.courseType = courseType;
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
    }

    @Override
    public int getCount() {
        return courseType.length();
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

        return convertView;
    }
}
