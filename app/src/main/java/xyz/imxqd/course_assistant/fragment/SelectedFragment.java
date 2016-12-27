package xyz.imxqd.course_assistant.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.adapter.SelectedAdapter;
import xyz.imxqd.course_assistant.application.CourseAssistant;
import xyz.imxqd.course_assistant.model.SelectItem;
import xyz.imxqd.course_assistant.web.CourseTool;

/**
 * Created by imxqd on 2016/3/2.
 * 已选中的课程
 */
public class SelectedFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "SelectedFragment";
    private SwipeRefreshLayout swipeLayout;
    private ListView listView;
    private SelectedAdapter adapter;
    private GetSelectedTask task = null;

    public SelectedFragment() {
        adapter = new SelectedAdapter();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SelectedFragment newInstance() {
        SelectedFragment fragment = new SelectedFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_selected, container, false);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        listView = (ListView) rootView.findViewById(R.id.selected_list);
        listView.setEmptyView(rootView.findViewById(R.id.no_selected));

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        swipeLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getMembersTask = new GetMembersTask();
        SelectItem item = (SelectItem) adapter.getItem(position);
        getMembersTask.execute(CourseTool.xqValue, item.getCourseCode(), item.getClassNo());
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
        final SelectItem item = (SelectItem) parent.getItemAtPosition(position);
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.remove_confirm_title_string))
                .setMessage(getString(R.string.remove_confirm_message_string, item.getCourseName()))
                .setPositiveButton(getString(R.string.remove_confirm_button_positive_string), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        adapter.remove(position);
                        adapter.notifyDataSetChanged();
                        CourseAssistant.getInstance().remove(item.getCourseCode());
                    }
                })
                .setNegativeButton(getString(R.string.remove_confirm_button_negative_string), null)
                .show();
        return true;
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    class GetSelectedTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            swipeLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "获取已选课程...");
            try {
                ArrayList<SelectItem> list = CourseTool.getSelected();
                if (list != null) {
                    Log.d(TAG, "获取课程: " + list);
                    adapter.setList(list);
                    CourseAssistant.getInstance().setSelected(list);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.d(TAG, "获取已选课程成功.");
                adapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "获取已选课程失败.");
                Toast.makeText(getContext(), "获取数据失败,请尝试重新登录", Toast.LENGTH_SHORT).show();
            }
            swipeLayout.setRefreshing(false);
            task = null;
        }
    }

    GetMembersTask getMembersTask = null;
    class GetMembersTask extends AsyncTask<String, Void, ArrayList<String>> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getContext(),
                    getString(R.string.loading_string),
                    getString(R.string.loading_string));
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Log.d(TAG, "获取教学班成员...");
            return CourseTool.getMembers(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            dialog.dismiss();
            if (list != null) {
                Log.d(TAG, "获取教学班成员成功.");
                StringBuilder str = new StringBuilder();
                for (String s : list) {
                    str.append(s);
                    str.append("\n");
                }
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.classroom_members)
                        .setMessage(str)
                        .setPositiveButton(R.string.string_confirm, null)
                        .show();
            } else {
                Log.d(TAG, "获取教学班成员失败.");
                Toast.makeText(getContext(), "获取教学班成员失败", Toast.LENGTH_SHORT).show();
            }
            getMembersTask = null;
        }
    }

    public void loadData() {
        Log.d(TAG, "loadData");
        if (CourseAssistant.get().isLoggedIn()) {
            task = new GetSelectedTask();
            task.execute();
        }
    }
}
