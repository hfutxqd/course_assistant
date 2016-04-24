package xyz.imxqd.course_assistant.model;

/**
 * Created by imxqd on 2016/3/2.
 *
 */
public class Classroom {
    String courseName,campus, weeks, assessment, selectedCount,
            totalCount,range, timeAndPlace, prohibit, remarks, classNo, teacher, courseCode;

    public Classroom(String courseCode)
    {
        this.courseCode = courseCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public String getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(String selectedCount) {
        this.selectedCount = selectedCount;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getTimeAndPlace() {
        return timeAndPlace;
    }

    public void setTimeAndPlace(String timeAndPlace) {
        this.timeAndPlace = timeAndPlace;
    }

    public String getProhibit() {
        return prohibit;
    }

    public void setProhibit(String prohibit) {
        this.prohibit = prohibit;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getClassNo() {
        return classNo;
    }

    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "courseName='" + courseName + '\'' +
                ", campus='" + campus + '\'' +
                ", weeks='" + weeks + '\'' +
                ", assessment='" + assessment + '\'' +
                ", selectedCount='" + selectedCount + '\'' +
                ", totalCount='" + totalCount + '\'' +
                ", range='" + range + '\'' +
                ", timeAndPlace='" + timeAndPlace + '\'' +
                ", prohibit='" + prohibit + '\'' +
                ", remarks='" + remarks + '\'' +
                ", classNo='" + classNo + '\'' +
                ", teacher='" + teacher + '\'' +
                '}';
    }
}
