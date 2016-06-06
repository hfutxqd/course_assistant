package xyz.imxqd.course_assistant.web;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import xyz.imxqd.course_assistant.model.Classroom;
import xyz.imxqd.course_assistant.model.CourseItem;
import xyz.imxqd.course_assistant.model.SelectItem;

/**
 * Created by imxqd on 2016/3/2.
 *
 */
public class CourseTool {
    public static final String LOGIN_URL = "http://ids1.hfut.edu.cn/amserver/UI/Login";
    public static final String LOGIN_URL2 = "http://bkjw.hfut.edu.cn/StuIndex.asp";
    public static final String SELECTED_URL = "http://bkjw.hfut.edu.cn/student/asp/select_down_f3.asp";
    public static final String ALL_URL = "http://bkjw.hfut.edu.cn/student/asp/select_topLeft_f3.asp";
    public static final String CLASSES_URL = "http://bkjw.hfut.edu.cn/student/asp/select_topRight_f3.asp?lb=1&kcdm=";
    public static final String SUBMIT_URL = "http://bkjw.hfut.edu.cn/student/asp/selectKC_submit_f3.asp";
    public static final String TEACHER_URL = "http://bkjw.hfut.edu.cn/teacher/asp/teacher_info.asp?jsh=2004800061";
    public static final String CLASSTIME_URL = "http://bkjw.hfut.edu.cn/student/asp/xqkb1_1.asp?kcdm=1201061B&jxbh=0004&xqdm=028";
    public static final String CLASSMATES_URL = "http://bkjw.hfut.edu.cn/student/asp/Jxbmdcx_1.asp?xqdm=028&kcdm=0200015X&jxbh=0001";

    public static Map<String, String> cookie = null;
    public static String studentNo = null;
    public static boolean login(String sno, String pwd) throws IOException {
        HashMap<String, String> login_data = new HashMap<>();
        login_data.put("IDToken0", "");
        login_data.put("IDToken1", sno);
        login_data.put("IDToken2",pwd);
        Connection.Response response =  Jsoup.connect(LOGIN_URL)
                .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)")
                .timeout(20000)
                .data(login_data)
                .execute();
        cookie = response.cookies();
        Document doc = response.parse();
        if(doc.text().contains("failed"))
        {
            cookie = null;
            return false;
        }else {
            Connection.Response response2 = Jsoup.connect(LOGIN_URL2)
                    .cookies(cookie)
                    .execute();
            cookie = response2.cookies();
            Document doc2 = response2.parse();
            if(doc2.location().endsWith("s_index.htm"))
            {
                studentNo = sno;
                return true;
            }else {
                cookie = null;
            }
        }
        return false;
    }

    public static ArrayList<SelectItem> getSelected() throws IOException {
        if(cookie != null)
        {
            Document doc = Jsoup.connect(SELECTED_URL)
                    .cookies(cookie)
                    .get();
            Element tbody = doc.getElementById("TableXKJG").child(0);
            ArrayList<SelectItem> list = new ArrayList<>();
            for(int i = 1; i < tbody.children().size(); i++)
            {
                Element item = tbody.child(i);
                if(item.tagName().equals("script"))
                {
                    continue;
                }
                SelectItem selectItem = new SelectItem();
                selectItem.setCourseCode(item.child(0).text());
                selectItem.setCourseName(item.child(1).text());
                selectItem.setClassNo(item.child(2).text());
                selectItem.setCredit(item.child(3).text());
                selectItem.setPrice(item.child(4).text());
                selectItem.setCoursetype(item.child(5).text());
                list.add(selectItem);
            }
            return list;
        }
        return null;
    }
    public enum CourseType
    {
        Optional,
        Required,
        Planed
    }
    public static ArrayList<CourseItem> getCourseList(CourseType type) throws IOException {
        String URL = ALL_URL + "?kclx=";
        switch (type)
        {
            case Optional:
                URL += "x";
                break;
            case Required:
                URL += "b";
                break;
            case Planed:
                URL += "jh";
                break;
        }
        if(cookie != null) {
            Document doc;
            doc = Jsoup.connect(URL)
                    .cookies(cookie)
                    .get();
            Element tbody = doc.getElementById("KCTable").child(0);
            ArrayList<CourseItem> list = new ArrayList<>();
            for(Element i: tbody.children())
            {
                CourseItem item = new CourseItem();
                item.setCourseCode(i.child(0).text());
                item.setCourseName(i.child(1).text());
                item.setCourseType(i.child(2).text());
                item.setShcool(i.child(3).text());
                item.setCredit(i.child(4).text());
                item.setPlanType(i.child(5).text());
                list.add(item);
            }
            return list;
        }
        return null;
    }

    public static ArrayList<Classroom> getClassList(String kcdm) throws IOException {
        if(cookie != null)
        {
            ArrayList<Classroom> list = new ArrayList<>();
            Document doc;
            doc = Jsoup.connect(CLASSES_URL+kcdm)
                    .cookies(cookie)
                    .get();
            Element tbody = doc.getElementById("JXBTable").child(0);
            for (Element i: tbody.children())
            {
                Classroom item = new Classroom(kcdm);
                item.setCourseName(i.child(0).text());
                String htmlStr = i.child(1).attr("alt");
                Document html = Jsoup.parse(htmlStr);
                Element table = html.getElementsByTag("tbody").first();
                item.setClassNo(i.child(1).text());
                item.setCampus(table.child(1).child(0).text());
                item.setCourseName(i.child(0).child(0).attr("value"));
                item.setAssessment(table.child(1).child(2).text());
                item.setProhibit(table.child(1).child(3).text());
                item.setWeeks(table.child(1).child(1).text());
                item.setSelectedCount(table.child(2).child(0).text());
                item.setTotalCount(table.child(2).child(1).text());
                item.setTimeAndPlace(table.child(4).child(0).text());
                item.setRemarks(table.child(5).child(0).text());
                item.setTeacher(i.child(2).text());
                item.setRange(i.child(3).text());
                list.add(item);
            }
            return list;
        }
        return null;
    }

    public static String submit(HashMap<String, String> data) throws IOException {
        Document doc;
        Connection conn = Jsoup.connect(SUBMIT_URL)
                .cookies(cookie);
        conn.data("xh", studentNo);
        Set<Map.Entry<String, String>> set =  data.entrySet();
        for (Map.Entry<String, String> entry : set) {
            conn.data("kcdm", entry.getKey());
            conn.data("jxbh", entry.getValue());
        }
        doc = conn.post();
        return doc.html();
    }

}
