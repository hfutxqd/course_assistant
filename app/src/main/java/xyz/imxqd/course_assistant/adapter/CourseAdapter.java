package xyz.imxqd.course_assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.model.CourseItem;

/**
 * Created by imxqd on 2016/3/2.
 *
 */
public class CourseAdapter extends BaseAdapter {
    ArrayList<CourseItem> list;
    public CourseAdapter()
    {
        list = new ArrayList<>();
    }

    public void setList(ArrayList<CourseItem> li)
    {
        list.clear();
        list.addAll(li);
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
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.course_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        CourseItem item = list.get(position);
        holder = (ViewHolder) convertView.getTag();
        holder.courseName.setText(item.getCourseName());
        holder.courseCode.setText(item.getCourseCode());
        holder.credit.setText(String.format("学分:%s",item.getCredit()));
        holder.school.setText(String.format("%s%s", parent.getContext().getString(R.string.course_school)
                , item.getShcool()));
        if(item.getPlanType().equals(" "))
        {
            holder.courseType.setText(item.getCourseType());
        }else {
            holder.courseType.setText(item.getPlanType());
        }
        return convertView;
    }

    class ViewHolder
    {
        TextView courseName, courseCode, credit, courseType, school;
        public ViewHolder(View root)
        {
            courseName = (TextView) root.findViewById(R.id.courseName);
            courseCode = (TextView) root.findViewById(R.id.courseCode);
            credit = (TextView) root.findViewById(R.id.credit);
            courseType = (TextView) root.findViewById(R.id.courseType);
            school = (TextView) root.findViewById(R.id.school);
        }
    }
}
