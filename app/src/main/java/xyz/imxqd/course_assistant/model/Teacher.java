package xyz.imxqd.course_assistant.model;

/**
 * Created by imxqd on 2016/3/2.
 *
 */
public class Teacher {
    String tablehtml;

    public String getTablehtml() {
        return tablehtml;
    }

    public void setTablehtml(String tablehtml) {
        this.tablehtml = tablehtml;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "tablehtml='" + tablehtml + '\'' +
                '}';
    }
}
