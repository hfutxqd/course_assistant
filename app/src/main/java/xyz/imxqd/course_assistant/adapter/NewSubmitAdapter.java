package xyz.imxqd.course_assistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.application.CourseAssistant;
import xyz.imxqd.course_assistant.model.Classroom;

/**
 * Created by imxqd on 2016/3/4.
 *
 */
public class NewSubmitAdapter extends BaseAdapter{
    ArrayList<Classroom> list;
    public NewSubmitAdapter()
    {
        list = new ArrayList<>(CourseAssistant.getInstance().getNewSubmitClassroomMap().values());
        CourseAssistant.getInstance().setNewSubmitAdapter(this);
    }

    public void update()
    {
        list.clear();
        list.addAll(CourseAssistant.getInstance().getNewSubmitClassroomMap().values());
    }

    public void remove(int pos)
    {
        CourseAssistant.getInstance().remove(list.get(pos).getCourseCode());
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
                    .inflate(R.layout.new_submit_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        Classroom item = list.get(position);
        holder = (ViewHolder) convertView.getTag();
        holder.courseNameAndClass.setText(
                String.format("%s%s班", item.getCourseName(), item.getClassNo()));
        holder.campus.setText(item.getCampus());
        holder.teacher.setText(item.getTeacher());
        return convertView;
    }

    class ViewHolder
    {
        TextView courseNameAndClass, teacher, campus;
        public ViewHolder(View root)
        {
            courseNameAndClass = (TextView) root.findViewById(R.id.courseNameAndClass);
            teacher = (TextView) root.findViewById(R.id.teacher);
            campus = (TextView) root.findViewById(R.id.campus);
        }
    }
}
