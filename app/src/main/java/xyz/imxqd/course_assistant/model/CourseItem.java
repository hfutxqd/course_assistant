package xyz.imxqd.course_assistant.model;

/**
 * Created by imxqd on 2016/3/2.
 *
 */
public class CourseItem {
    String courseCode, courseName, courseType, shcool, credit, planType;

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

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getShcool() {
        return shcool;
    }

    public void setShcool(String shcool) {
        this.shcool = shcool;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }


    @Override
    public String toString() {
        return "CourseItem{" +
                "courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseType='" + courseType + '\'' +
                ", shcool='" + shcool + '\'' +
                ", credit='" + credit + '\'' +
                ", planType='" + planType + '\'' +
                '}';
    }
}
