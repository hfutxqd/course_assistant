package xyz.imxqd.course_assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.model.SelectItem;

/**
 * Created by imxqd on 2016/3/2.
 *
 */
public class SelectedAdapter extends BaseAdapter {

    ArrayList<SelectItem> list;

    public SelectedAdapter()
    {
        list = new ArrayList<>();
    }
    public void setList(ArrayList<SelectItem> li)
    {
        list.clear();
        list.addAll(li);
    }

    public void remove(int pos)
    {
        list.remove(pos);
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
                    .inflate(R.layout.selected_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        SelectItem item = list.get(position);
        holder = (ViewHolder) convertView.getTag();
        holder.courseNameAndClass.setText(
                String.format("%s%s班", item.getCourseName(), item.getClassNo()));
        holder.courseCode.setText(item.getCourseCode());
        holder.courseType.setText(item.getCoursetype());
        holder.creditAndPrice.setText(
                String.format("学分:%s    费用:%s", item.getCredit(), item.getPrice())
        );
        return convertView;
    }

    class ViewHolder
    {
        TextView courseNameAndClass, courseCode, creditAndPrice, courseType;
        public ViewHolder(View root)
        {
            courseNameAndClass = (TextView) root.findViewById(R.id.courseNameAndClass);
            courseCode = (TextView) root.findViewById(R.id.courseCode);
            creditAndPrice = (TextView) root.findViewById(R.id.creditAndPrice);
            courseType = (TextView) root.findViewById(R.id.courseType);
        }
    }
}
