package xyz.imxqd.course_assistant.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by imxqd on 2016/12/27.
 */

public class MyToast {
    public static void myTosat(Context context, int imageId, int duration) {
        //new一个toast传入要显示的activity的上下文
        Toast toast = new Toast(context);
        //显示的时间
        toast.setDuration(duration);
        //显示的位置
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        //重新给toast进行布局
        LinearLayout toastLayout = new LinearLayout(context);
        toastLayout.setBackgroundColor(Color.TRANSPARENT);
        toastLayout.setOrientation(LinearLayout.HORIZONTAL);
        toastLayout.setGravity(Gravity.CENTER_VERTICAL);

        ImageView imageView = new ImageView(context);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        imageView.setImageResource(imageId);
        //把imageView添加到toastLayout的布局当中
        toastLayout.addView(imageView);
        //把toastLayout添加到toast的布局当中
        toast.setView(toastLayout);
        toast.show();
    }
}
