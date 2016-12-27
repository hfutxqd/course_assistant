package xyz.imxqd.course_assistant.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.activity.MainActivity;
import xyz.imxqd.course_assistant.adapter.CourseAdapter;
import xyz.imxqd.course_assistant.application.CourseAssistant;
import xyz.imxqd.course_assistant.model.CourseItem;
import xyz.imxqd.course_assistant.web.CourseTool;

/**
 * Created by imxqd on 2016/3/3.
 * 选课主界面
 */
public class CourseFragment extends Fragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    private static final String TAG = "CourseFragment";

    public CourseFragment() {

    }

    public static CourseFragment newInstance() {
        CourseFragment fragment = new CourseFragment();
        return fragment;
    }

    CourseAdapter adapter;
    Spinner spinner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_course, container, false);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ListView listView = (ListView) rootView.findViewById(R.id.courseList);
        listView.setOnItemClickListener(this);
        adapter = new CourseAdapter();
        listView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.course, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.onActionViewExpanded();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadData() {
        Log.d(TAG, "loadData");
        if(!CourseAssistant.get().isLoggedIn()) {
            return;
        }
        CourseTool.CourseType courseType;
        switch (spinner.getSelectedItemPosition()) {
            case 0:
                courseType = CourseTool.CourseType.Optional;
                break;
            case 1:
                courseType = CourseTool.CourseType.Required;
                break;
            case 2:
                courseType = CourseTool.CourseType.Planed;
                break;
            default:
                courseType = CourseTool.CourseType.Planed;
        }
        task = new GetCourseTask(courseType);
        task.execute();
    }

    GetCourseTask task = null;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loadData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CourseItem item = (CourseItem) adapter.getItem(position);
        Fragment fragmentTo = ClassroomFragment.newInstance(item.getCourseCode());
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.courseContainer, fragmentTo, "ClassroomFragment")
                .addToBackStack("ClassroomFragment")
                .commit();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getActivity() != null && isVisibleToUser) {
            if (getFragmentManager().findFragmentByTag("ClassroomFragment") != null) {
                ((MainActivity) getActivity()).setHomeAsUp(true);
            } else {
                ((MainActivity) getActivity()).setHomeAsUp(false);
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.setFilter(query);
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.setFilter(newText);
        adapter.notifyDataSetChanged();
        return true;
    }


    class GetCourseTask extends AsyncTask<Void, Void, Boolean> {
        CourseTool.CourseType courseType;

        public GetCourseTask(CourseTool.CourseType type) {
            courseType = type;
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getContext(), getString(R.string.loading_string),
                    getString(R.string.loading_string));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "正在获取课程列表...");
            try {
                ArrayList<CourseItem> list = CourseTool.getCourseList(courseType);
                if (list != null) {
                    Log.d(TAG, "获取课程列表:" + list);
                    adapter.setList(list);
                    return true;
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.d(TAG, "获取课程列表成功.");
                adapter.notifyDataSetChanged();
            } else {
                if (CourseTool.studentNo != null) {
                    Toast.makeText(getContext(), getString(R.string.network_error_string), Toast.LENGTH_SHORT).show();
                }
            }
            progressDialog.dismiss();
            task = null;
        }
    }

}
