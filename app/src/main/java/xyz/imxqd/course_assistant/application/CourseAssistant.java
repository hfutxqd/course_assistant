package xyz.imxqd.course_assistant.application;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

import xyz.imxqd.course_assistant.adapter.NewSubmitAdapter;
import xyz.imxqd.course_assistant.model.Classroom;
import xyz.imxqd.course_assistant.model.SelectItem;

/**
 * Created by imxqd on 2016/3/3.
 */
public class CourseAssistant extends Application {
    public static long mLastLoginTime = 0;

    private static CourseAssistant courseAssistant;
    private HashMap<String, String> toSubmitMap;//将要提交的课程,用于提交
    private HashMap<String, String> newSubmitMap;//新提交的课程,用于提交
    private HashMap<String, Classroom> newSubmitClassroomMap;//新提交的课程,用于显示
    private ArrayList<SelectItem> selectItemList;//之前已经选中的课程,用于显示
    private NewSubmitAdapter adapter;

    @Override
    public void onCreate() {
        if (courseAssistant == null) {
            courseAssistant = this;
        }
        toSubmitMap = new HashMap<>();
        newSubmitMap = new HashMap<>();
        newSubmitClassroomMap = new HashMap<>();
        super.onCreate();
    }

    public void setNewSubmitAdapter(NewSubmitAdapter adapter) {
        this.adapter = adapter;
    }

    public static CourseAssistant getInstance() {
        return courseAssistant;
    }

    //设置之前已被选中的课程
    public void setSelected(ArrayList<SelectItem> list) {
        selectItemList = list;
        for (SelectItem item : list) {
            toSubmitMap.put(item.getCourseCode(), item.getClassNo());
        }
    }


    public void add(String courseCode, Classroom item) {
        toSubmitMap.put(courseCode, item.getClassNo());
        newSubmitMap.put(courseCode, item.getClassNo());
        newSubmitClassroomMap.put(courseCode, item);
        if (adapter != null) {
            adapter.update();
            adapter.notifyDataSetChanged();
        }
    }

    public void remove(String courseCode) {
        toSubmitMap.remove(courseCode);
        newSubmitClassroomMap.remove(courseCode);
        newSubmitMap.remove(courseCode);
        selectItemList.remove(new SelectItem(courseCode));
        if (adapter != null) {
            adapter.update();
            adapter.notifyDataSetChanged();
        }
    }

    public String get(String courseCode) {
        return toSubmitMap.get(courseCode);
    }

    public void clear() {
        newSubmitMap.clear();
        newSubmitClassroomMap.clear();
        toSubmitMap.clear();
        selectItemList.clear();
    }

    public HashMap<String, String> getToSubmitMap() {
        return toSubmitMap;
    }

    public HashMap<String, String> getNewSubmitMap() {
        return newSubmitMap;
    }

    public HashMap<String, Classroom> getNewSubmitClassroomMap() {
        return newSubmitClassroomMap;
    }

    public ArrayList<SelectItem> getSelectItemList() {
        return selectItemList;
    }

}
