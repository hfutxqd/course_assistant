package xyz.imxqd.course_assistant.model;

/**
 * Created by imxqd on 2016/3/2.
 *
 */
public class SelectItem {
    String courseCode, classNo, courseName, credit, price, coursetype;

    public SelectItem()
    {

    }

    public SelectItem(String courseCode)
    {
        this.courseCode = courseCode;
    }

    public SelectItem(String classNo, CourseItem item)
    {
        courseCode = item.courseCode;
        this.classNo = classNo;
        courseName = item.courseName;
        credit = item.credit;
        coursetype = item.courseType;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getClassNo() {
        return classNo;
    }

    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCoursetype() {
        return coursetype;
    }

    public void setCoursetype(String coursetype) {
        this.coursetype = coursetype;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        SelectItem item = (SelectItem) o;
        return courseCode.equals(item.courseCode);
    }

    @Override
    public String toString() {
        return "SelectItem{" +
                "courseCode='" + courseCode + '\'' +
                ", classNo='" + classNo + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credit='" + credit + '\'' +
                ", price='" + price + '\'' +
                ", coursetype='" + coursetype + '\'' +
                '}';
    }
}
