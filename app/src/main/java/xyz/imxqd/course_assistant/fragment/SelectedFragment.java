package xyz.imxqd.course_assistant.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
 *
 */
public class SelectedFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

    class GetSelectedTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute() {
            swipeLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<SelectItem> list = CourseTool.getSelected();
                if(list != null)
                {
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
            if(success)
            {
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(getContext(), "获取数据失败,请尝试重新登录", Toast.LENGTH_SHORT).show();
            }
            swipeLayout.setRefreshing(false);
            task = null;
        }
    }

    public void loadData()
    {
        task = new GetSelectedTask();
        task.execute();
    }
}
