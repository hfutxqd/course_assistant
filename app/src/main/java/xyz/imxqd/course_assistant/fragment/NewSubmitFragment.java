package xyz.imxqd.course_assistant.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import xyz.imxqd.course_assistant.customListener.SwipeDismissListViewTouchListener;
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

    NewSubmitAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_selected, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.selected_list);
        listView.setEmptyView(rootView.findViewById(R.id.no_new_submit_data));
        listView.setOnItemLongClickListener(this);
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(listView, new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            adapter.remove(position);
                            CourseAssistant assistant = CourseAssistant.getInstance();
                            ((MainActivity)getActivity()).setBadgeCount(assistant.getNewSubmitMap().size());
                        }
                    }
                });

        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());


        adapter = new NewSubmitAdapter();
        listView.setAdapter(adapter);
        return rootView;
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
