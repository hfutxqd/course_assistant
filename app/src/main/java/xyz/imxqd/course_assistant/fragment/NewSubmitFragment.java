package xyz.imxqd.course_assistant.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.activity.MainActivity;
import xyz.imxqd.course_assistant.adapter.NewSubmitAdapter;
import xyz.imxqd.course_assistant.application.CourseAssistant;
import xyz.imxqd.course_assistant.model.Classroom;

/**
 * Created by imxqd on 2016/3/3.
 *
 */
public class NewSubmitFragment extends Fragment implements AdapterView.OnItemLongClickListener {
    public NewSubmitFragment()
    {

    }
    public static NewSubmitFragment newInstance()
    {
        return new NewSubmitFragment();
    }

    private NewSubmitAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_submit, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.selected_list);
        listView.setEmptyView(rootView.findViewById(R.id.no_new_submit_data));
        listView.setOnItemLongClickListener(this);
        adapter = new NewSubmitAdapter();
        listView.setAdapter(adapter);
        return rootView;
    }

    public void updateUI() {
        adapter.update();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Classroom item = (Classroom) adapter.getItem(position);
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.remove_confirm_title_string)
                .setMessage(getString(R.string.remove_confirm_message_string, item.getCourseName()))
                .setPositiveButton(R.string.remove_confirm_button_positive_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.remove(position);
                        adapter.notifyDataSetChanged();
                        CourseAssistant assistant = CourseAssistant.getInstance();
                        ((MainActivity)getActivity()).setBadgeCount(assistant.getNewSubmitMap().size());
                    }
                })
                .setNegativeButton(R.string.remove_confirm_button_negative_string, null)
                .show();
        return true;
    }
}
