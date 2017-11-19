package com.example.chase.dontpaniceducational;

import com.google.gson.JsonArray;

import java.util.ArrayList;

public class Classes {
    private String classId, schoolId, courseType, courseNumber,
            sectionNumber, courseTitle;
    private ArrayList questions = new ArrayList();
    private ArrayList answers = new ArrayList();
    //, students, teacherAssistants, teachers;

    public Classes() {}

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

    public ArrayList<String> getQuestions() {
        return questions;
    }

    public void setQuestions(final ArrayList questions) {
        this.questions.addAll(questions);
    }

    public ArrayList<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(final ArrayList answers) {
        this.answers.addAll(answers);
    }

    /*public ArrayList<String> getStudents() {
        return students;
    }

    public void setStudents(final ArrayList<String> students) {
        this.students.addAll(students);
    }

    public ArrayList<String> getTeacherAssistants() {
        return teacherAssistants;
    }

    public void setTeacherAssistants(final ArrayList<String> teacherAssistants) {
        this.teacherAssistants.addAll(teacherAssistants);
    }

    public ArrayList<String> getTeachers() {
        return teachers;
    }

    public void setTeachers(final ArrayList<String> teachers) {
        this.teachers.addAll(teachers);
    }*/
}