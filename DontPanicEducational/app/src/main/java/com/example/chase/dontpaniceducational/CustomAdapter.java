package com.example.chase.dontpaniceducational;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
    private Activity context;
    private String[] courseType;
    private String[] courseNumber;
    private String[] courseTitle;

    CustomAdapter() {
        courseType = null;
        courseNumber = null;
        courseTitle = null;
    }

    public CustomAdapter(String[] textType, String[] textNumber, String[] textTitle) {
        courseType = textType;
        courseNumber = textNumber;
        courseTitle = textTitle;
    }

    @Override
    public int getCount() {
        return courseType.length;
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
        LayoutInflater inflater = context.getLayoutInflater();
        View row;
        row = inflater.inflate(R.layout.list_row, parent, false);
        TextView classType, classNumber, classTitle;
        classType = (TextView) row.findViewById(R.id.classType);
        classNumber = (TextView) row.findViewById(R.id.classNumber);
        classTitle = (TextView) row.findViewById(R.id.classTitle);
        classType.setText(courseType[position]);
        classNumber.setText(courseNumber[position]);
        classTitle.setText(courseTitle[position]);
        return (row);
    }
}
