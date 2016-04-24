package xyz.imxqd.course_assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.application.CourseAssistant;
import xyz.imxqd.course_assistant.model.Classroom;

/**
 * Created by imxqd on 2016/3/2.
 *
 */
public class ClassroomAdapter extends BaseExpandableListAdapter {
    ArrayList<Classroom> list;

    public ClassroomAdapter()
    {
        list = new ArrayList<>();
    }

    public void setList(ArrayList<Classroom> li)
    {
        list.clear();
        list.addAll(li);
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 2000 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.classroom_group_item, parent, false);
            viewHolder = new GroupViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (GroupViewHolder) convertView.getTag();
        Classroom item = list.get(groupPosition);
        viewHolder.courseNameAndClass.setText(String.format("%s%s班", item.getCourseName(), item.getClassNo()));
        viewHolder.campus.setText(item.getCampus());
        viewHolder.selectedAndTotal.setText(String.format("人数:%s/%s", item.getSelectedCount().substring(5), item.getTotalCount().substring(5)));
        viewHolder.teacher.setText(item.getTeacher());
        viewHolder.weeks.setText(String.format("起止周:%s", item.getWeeks()));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.classroom_child_item, parent, false);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        final Classroom item = list.get(groupPosition);
        viewHolder = (ChildViewHolder) convertView.getTag();
        viewHolder.assessment.setText(parent.getResources().getString(R.string.string_assessment, item.getAssessment()));
        viewHolder.range.setText(parent.getResources().getString(R.string.string_range, item.getRange()));
        viewHolder.timeAndPlace.setText(parent.getResources().getString(R.string.string_timeAndPlace, item.getTimeAndPlace()));
        viewHolder.prohibit.setText(parent.getResources().getString(R.string.string_prohibit, item.getProhibit()));
        CourseAssistant assistant = CourseAssistant.getInstance();
        Classroom tmp = assistant.getNewSubmitClassroomMap().get(item.getCourseCode());
        if(tmp!= null && tmp.getClassNo().equals(item.getClassNo()))
        {
            viewHolder.btn_selectIt.setText(parent.getResources().getString(R.string.string_selected));
            viewHolder.btn_selectIt.setBackgroundResource(R.color.Grey);
        }else {
            viewHolder.btn_selectIt.setText(parent.getResources().getString(R.string.string_select));
            viewHolder.btn_selectIt.setBackgroundResource(R.color.material_deep_teal_500);
        }
        viewHolder.btn_selectIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                {
                    listener.onClassroomitemSelected(item, (Button) v);
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setOnClassroomitemSelectListener(OnClassroomitemSelectListener listener)
    {
        this.listener = listener;
    }

    class GroupViewHolder
    {
        TextView courseNameAndClass, teacher, campus, selectedAndTotal, weeks;
        public GroupViewHolder(View root)
        {
            courseNameAndClass = (TextView) root.findViewById(R.id.courseNameAndClass);
            teacher = (TextView) root.findViewById(R.id.teacher);
            campus = (TextView) root.findViewById(R.id.campus);
            selectedAndTotal = (TextView) root.findViewById(R.id.selectedAndTotal);
            weeks = (TextView) root.findViewById(R.id.weeks);

        }
    }

    class ChildViewHolder
    {
        TextView assessment, range, timeAndPlace, prohibit, remarks;
        Button btn_selectIt;
        public ChildViewHolder(View root)
        {
            assessment = (TextView) root.findViewById(R.id.assessment);
            range = (TextView) root.findViewById(R.id.range);
            timeAndPlace = (TextView) root.findViewById(R.id.timeAndPlace);
            prohibit = (TextView) root.findViewById(R.id.prohibit);
            remarks = (TextView) root.findViewById(R.id.remarks);
            btn_selectIt = (Button) root.findViewById(R.id.btn_selectIt);
        }
    }
    OnClassroomitemSelectListener listener = null;
    public interface OnClassroomitemSelectListener
    {
        void onClassroomitemSelected(Classroom item, Button button);
    }
}
