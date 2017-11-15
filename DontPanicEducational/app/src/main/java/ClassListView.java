import java.util.ArrayList;

public class ClassListView {
    private String classId, schoolId, courseType, courseNumber,
            sectionNumber, courseTitle;
    private ArrayList<String> questions, students, teacherAssistants, teachers;

    public ClassListView(){}

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

    public void setQuestions(final ArrayList<String> questions) {
        this.questions.addAll(questions);
    }

    public ArrayList<String> getStudents() {
        return students;
    }
}