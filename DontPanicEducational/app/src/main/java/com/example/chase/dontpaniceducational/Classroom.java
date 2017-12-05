package com.example.chase.dontpaniceducational;

import java.io.Serializable;
import java.util.ArrayList;

public class Classroom implements Serializable{
    private String classId, schoolId, courseType, courseNumber,
            sectionNumber, courseTitle;
    private ArrayList<Question> questions = new ArrayList<>();

    public Classroom() {}

    public String getClassId() {
        return classId;
    }

    public void setClassId(final String classId) {
        this.classId = classId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(final String schoolId) {
        this.schoolId = schoolId;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(final String courseType) {
        this.courseType = courseType;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(final String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(final String sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(final String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}