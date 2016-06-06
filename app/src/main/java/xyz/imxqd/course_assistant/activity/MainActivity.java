package xyz.imxqd.course_assistant.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.adapter.ConfirmListAdapter;
import xyz.imxqd.course_assistant.application.CourseAssistant;
import xyz.imxqd.course_assistant.fragment.CourseFragment;
import xyz.imxqd.course_assistant.fragment.NewSubmitFragment;
import xyz.imxqd.course_assistant.fragment.SelectedFragment;
import xyz.imxqd.course_assistant.view.BadgeView;
import xyz.imxqd.course_assistant.view.LoginDialog;
import xyz.imxqd.course_assistant.web.CourseTool;

public class MainActivity extends AppCompatActivity implements LoginDialog.LoginCallBack, ViewPager.OnPageChangeListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private BadgeView badgeView = null;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private Fragment  selected, course, selecting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViewPage();
        initFragments();
        initBadgeView();
        initFab();
    }


    @Override
    protected void onResume() {
        super.onResume();
        login();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_swich_user)
        {
            SharedPreferences preferences = getSharedPreferences("user", 1);
            preferences.edit()
                    .putBoolean("auto", false)
                    .apply();
            login();
        }else if(id == android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void initViewPage()
    {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void initFragments()
    {
        selected = SelectedFragment.newInstance();
        course = CourseFragment.newInstance();
        selecting = NewSubmitFragment.newInstance();
    }

    public void initFab()
    {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        fab.hide();
    }

    public void initBadgeView()
    {
        TabLayout.Tab tab = tabLayout.getTabAt(2);
        View view = LayoutInflater.from(this).inflate(R.layout.tab_title_layout, null);
        ((TextView) view.findViewById(R.id.tv_title)).setText(mSectionsPagerAdapter.getPageTitle(2));
        if(tab != null)
        {
            tab.setCustomView(view);
            if(badgeView == null)
            {
                badgeView = new BadgeView(this);
            }
            badgeView.setTargetView(view);
            CourseAssistant assistant = CourseAssistant.getInstance();
            setBadgeCount(assistant.getNewSubmitMap().size());
        }
    }
    int current_page = 0;
    @Override
    public void onBackPressed() {
        if(current_page == 0 || current_page == 2)
        {
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSuccess() {
        ((SelectedFragment)selected).loadData();
        ((CourseFragment)course).loadData();
    }

    @Override
    public void oncCancel() {
        Snackbar.make(mViewPager, "你取消了登录,无法继续使用,请登录", Snackbar.LENGTH_INDEFINITE)
                .setAction("登录", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login();
                    }
                })
                .show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position)
        {
            case 0:
                fab.hide();
                setHomeAsUp(false);
                break;
            case 1:
                fab.hide();
                break;
            case 2:
                setHomeAsUp(false);
                fab.show();
        }
        current_page = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return selected;
                case 1:
                    return course;
                case 2:
                    return selecting;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.section_1);
                case 1:
                    return getString(R.string.section_2);
                case 2:
                    return getString(R.string.section_3);
            }
            return null;
        }
    }

    public void setHomeAsUp(boolean enable)
    {
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        }

    }

    public void setBadgeCount(int count)
    {
        badgeView.setBadgeCount(count);
    }

    public void login()
    {
        LoginDialog dialog = new LoginDialog(this);
        dialog.setCallBack(this);
    }

    public void submit()
    {
        if(CourseTool.cookie != null && CourseTool.studentNo != null)
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.submit_confirm_title)
                    .setAdapter(new ConfirmListAdapter(), null)
                    .setNegativeButton(R.string.string_confirm, null)
                    .setPositiveButton(R.string.string_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            task = new SubmitTask();
                            task.execute();
                        }
                    })
                    .show();
        }
    }

    SubmitTask task = null;

    class SubmitTask extends AsyncTask<Void, Void, String>
    {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this,getString(R.string.loading_string),
                    getString(R.string.loading_string));
        }

        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> map = CourseAssistant.getInstance().getToSubmitMap();
            try {
                return CourseTool.submit(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String success) {
            progressDialog.dismiss();
            if(success != null)
            {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(Jsoup.parse(success).title())
                        .setMessage(Jsoup.parse(success).body().text())
                        .show();
            }
            task = null;
        }
    }
}
