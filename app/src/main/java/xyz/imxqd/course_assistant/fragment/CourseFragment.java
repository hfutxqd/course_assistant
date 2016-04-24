package xyz.imxqd.course_assistant.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
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
import xyz.imxqd.course_assistant.adapter.SelectedAdapter;
import xyz.imxqd.course_assistant.model.ClassesItem;
import xyz.imxqd.course_assistant.model.Classroom;
import xyz.imxqd.course_assistant.model.CourseItem;
import xyz.imxqd.course_assistant.model.SelectItem;
import xyz.imxqd.course_assistant.web.CourseTool;

/**
 * Created by imxqd on 2016/3/3.
 *
 */
public class CourseFragment extends Fragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    public CourseFragment()
    {

    }
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CourseFragment newInstance() {
        CourseFragment fragment = new CourseFragment();
        return fragment;
    }

    CourseAdapter adapter;
    Spinner spinner;

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

    public void loadData()
    {
        CourseTool.CourseType courseType;
        switch (spinner.getSelectedItemPosition())
        {
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
        Fragment fragmentTo =  ClassroomFragment.newInstance(item.getCourseCode());
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.courseContainer,fragmentTo, "ClassroomFragment")
                .addToBackStack("ClassroomFragment")
                .commit();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getActivity() != null && isVisibleToUser)
        {
            if(getFragmentManager().findFragmentByTag("ClassroomFragment") != null)
            {
                ((MainActivity)getActivity()).setHomeAsUp(true);
            }else {
                ((MainActivity)getActivity()).setHomeAsUp(false);
            }
        }
    }



    class GetCourseTask extends AsyncTask<Void, Void, Boolean>
    {
        CourseTool.CourseType courseType;
        public GetCourseTask(CourseTool.CourseType type)
        {
            courseType = type;
        }
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getContext(),getString(R.string.loading_string),
                    getString(R.string.loading_string));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<CourseItem> list = CourseTool.getCourseList(courseType);
                if(list != null)
                {
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
            if(success)
            {
                adapter.notifyDataSetChanged();
            }else {
                if(CourseTool.studentNo != null)
                {
                    Toast.makeText(getContext(), getString(R.string.network_error_string),Toast.LENGTH_SHORT).show();
                }
            }
            progressDialog.dismiss();
            task = null;
        }
    }

}
