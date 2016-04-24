package xyz.imxqd.course_assistant.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.web.CourseTool;

/**
 * Created by imxqd on 2016/3/2.
 *
 */
public class LoginDialog {
    private AlertDialog.Builder builder;
    private Context context;
    View content;
    CheckBox checkBox;
    LoginTask task = null;
    public LoginDialog(Context context)
    {
        this.context = context;
        SharedPreferences data = context.getSharedPreferences("user", 1);
        if(data.getBoolean("auto", false))
        {
            String sno = data.getString("sno", "");
            String pwd = data.getString("pwd", "");
            task = new LoginTask(sno, pwd);
            task.execute();
        }else {
            dialogCreate();
            show();
        }

    }
    public void dialogCreate()
    {
        content = LayoutInflater.from(context)
                .inflate(R.layout.login_dialog_content, null);
        checkBox = (CheckBox) content.findViewById(R.id.checkBox);
        builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.login_title_string))
                .setView(content)
                .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        task = new LoginTask();
                        task.execute();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(callBack != null)
                        {
                            callBack.oncCancel();
                        }
                    }
                })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(callBack != null)
                {
                    callBack.oncCancel();
                }
            }
        });
    }
    AlertDialog dialog;
    public void show()
    {
        dialog = builder.show();
    }

    class LoginTask extends AsyncTask<Void, Void, Boolean>
    {
        int errorCode = 0;
        String sno, pwd;
        boolean auto = false;
        ProgressDialog progressDialog;
        public LoginTask()
        {
            AutoCompleteTextView snoView = (AutoCompleteTextView) content.findViewById(R.id.sno);
            EditText pwdView = (EditText) content.findViewById(R.id.password);
            sno = snoView.getText().toString();
            pwd = pwdView.getText().toString();
            auto = checkBox.isChecked();
        }

        public LoginTask(String sno, String pwd)
        {
            if(builder == null)
            {
                dialogCreate();
            }
            this.sno = sno;
            this.pwd = pwd;
            auto = true;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context,context.getString(R.string.loggingin_string),
                    context.getString(R.string.loggingin_wait_string));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if(CourseTool.login(sno, pwd))
                {
                    return true;
                }
            } catch (IOException e) {
                errorCode = 1;
                return false;
            }
            errorCode = 2;
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success)
            {
                Toast.makeText(context, context.getString(R.string.login_success_string),Toast.LENGTH_SHORT).show();
                SharedPreferences sharedata = context.getSharedPreferences("user", 1);
                sharedata.edit()
                        .putString("sno", sno)
                        .putString("pwd", pwd)
                        .putBoolean("auto", auto)
                        .apply();
                if(callBack != null)
                {
                    callBack.onSuccess();
                }
            }else{
                if(errorCode == 1)
                {
                    Toast.makeText(context, context.getString(R.string.network_error_string),Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, context.getString(R.string.pwd_error_string),Toast.LENGTH_SHORT).show();
                }
                if(dialog == null)
                {
                    show();
                }else {
                    dialog.show();
                }
            }
            task = null;
            progressDialog.dismiss();
        }
    }

    LoginCallBack callBack = null;

    public void setCallBack(LoginCallBack callBack)
    {
        this.callBack = callBack;
    }

    public interface LoginCallBack
    {
        void onSuccess();
        void oncCancel();
    }
}
