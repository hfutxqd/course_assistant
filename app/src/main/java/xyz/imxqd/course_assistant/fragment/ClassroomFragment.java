package xyz.imxqd.course_assistant.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.activity.MainActivity;
import xyz.imxqd.course_assistant.adapter.ClassroomAdapter;
import xyz.imxqd.course_assistant.application.CourseAssistant;
import xyz.imxqd.course_assistant.model.Classroom;
import xyz.imxqd.course_assistant.web.CourseTool;

/**
 * Created by imxqd on 2016/3/3.
 *
 */
public class ClassroomFragment extends Fragment implements ClassroomAdapter.OnClassroomItemSelectListener {

    public ClassroomFragment()
    {

    }
    private static final String ARG_COURSE_CODE = "courseCode";
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ClassroomFragment newInstance(String courseCode) {
        ClassroomFragment fragment = new ClassroomFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COURSE_CODE, courseCode);
        fragment.setArguments(args);
        return fragment;
    }

    private ClassroomAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_classroom, container, false);
        ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.classList);
        adapter = new ClassroomAdapter();
        adapter.setOnClassroomItemSelectListener(this);
        ((MainActivity)getActivity()).setHomeAsUp(true);
        listView.setAdapter(adapter);
        loadData();
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity)getActivity()).setHomeAsUp(false);
    }

    GetClassListTask task = null;

    @Override
    public void onClassroomItemSelected(Classroom item, Button button) {
        CourseAssistant assistant = CourseAssistant.getInstance();
        assistant.add(getArguments().getString(ARG_COURSE_CODE), item);
        ((MainActivity)getActivity()).setBadgeCount(assistant.getNewSubmitMap().size());
        adapter.notifyDataSetChanged();
    }

    class GetClassListTask extends AsyncTask<Void, Void, Boolean>
    {
        int error_code = 0;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(),getString(R.string.loading_string),
                    getString(R.string.loading_string));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Classroom> list = CourseTool.getClassList(getArguments().getString(ARG_COURSE_CODE));
                if(list != null)
                {
                    adapter.setList(list);
                    return true;
                }
            } catch (IOException e) {
                error_code = 1;
                e.printStackTrace();
                return false;
            }
            error_code = 2;
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success)
            {
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(getContext(), getString(R.string.string_get_data_failed), Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            task = null;
        }
    }


    public void loadData()
    {
        task = new GetClassListTask();
        task.execute();
    }
}
