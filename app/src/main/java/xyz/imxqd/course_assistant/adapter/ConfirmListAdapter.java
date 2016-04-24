package xyz.imxqd.course_assistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.application.CourseAssistant;
import xyz.imxqd.course_assistant.model.Classroom;
import xyz.imxqd.course_assistant.model.SelectItem;

/**
 * Created by imxqd on 2016/3/11.
 *
 */
public class ConfirmListAdapter extends BaseAdapter{

    ArrayList<Classroom> list;
    public ConfirmListAdapter()
    {
        list = new ArrayList<>(CourseAssistant.getInstance().getNewSubmitClassroomMap().values());
        ArrayList<SelectItem> selectItems = CourseAssistant.getInstance().getSelectItemList();
        if(selectItems != null)
        {
            for(SelectItem item: selectItems)
            {
                Classroom room = new Classroom(item.getCourseCode());
                room.setClassNo(item.getClassNo());
                room.setCourseName(item.getCourseName());
                list.add(room);
            }
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null)
        {
            convertView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.confirm_list_ltem, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Classroom item = list.get(position);
        holder.courseName.setText(item.getCourseName());
        holder.classNo.setText(String.format("%s班", item.getClassNo()));
        return convertView;
    }

    public class ViewHolder {
        TextView courseName, classNo;
        public ViewHolder(View root) {
            courseName = (TextView) root.findViewById(R.id.courseName);
            classNo = (TextView) root.findViewById(R.id.classNo);
        }
    }
}
