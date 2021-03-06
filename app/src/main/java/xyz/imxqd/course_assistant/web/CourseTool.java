package xyz.imxqd.course_assistant.web;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import xyz.imxqd.course_assistant.R;
import xyz.imxqd.course_assistant.application.CourseAssistant;
import xyz.imxqd.course_assistant.model.Classroom;
import xyz.imxqd.course_assistant.model.CourseItem;
import xyz.imxqd.course_assistant.model.SelectItem;

/**
 * Created by imxqd on 2016/3/2.
 */
public class CourseTool {
    //                                               http://222.195.8.201/pass.asp
    private static final String BASE_URL_XUANCHENG = "http://222.195.8.201/";
    private static final String BASE_URL_HEFEI = "http://bkjw.hfut.edu.cn/";

    private static final String LOGIN_HEFEI_URL = "http://ids1.hfut.edu.cn/amserver/UI/Login";
    private static final String LOGIN_HEFEI_URL2 = BASE_URL_HEFEI + "StuIndex.asp";
    private static final String LOGIN_XUANCHENG_URL = BASE_URL_XUANCHENG + "pass.asp";

    private static final String WELCOME_URL = "student/asp/s_welcome.asp";

    private static final String SELECTED_URL = "student/asp/select_down_f3.asp";
    private static final String ALL_URL = "student/asp/select_topLeft_f3.asp";
    private static final String CLASSES_F3_URL = "student/asp/select_topRight_f3.asp?kcdm=";
    private static final String CLASSES_URL = "student/asp/select_topRight.asp?kcdm=";
    private static final String SUBMIT_URL = "student/asp/selectKC_submit_f3.asp";
    private static final String GET_MEMBERS_URL = "student/asp/Jxbmdcx_1.asp";

    public static Map<String, String> cookie = null;
    public static String studentNo = null;
    public static String baseUrl = BASE_URL_HEFEI;

    public static String xqName = null;
    public static String xqValue = "030";

    public static boolean isOpened = false;

    private static boolean login(String sno, String pwd) throws IOException {
        baseUrl = BASE_URL_HEFEI;
        CourseAssistant.get().setLoggedIn(false);
        HashMap<String, String> login_data = new HashMap<>();
        login_data.put("IDToken0", "");
        login_data.put("IDToken1", sno);
        login_data.put("IDToken2", pwd);
        Connection.Response response = Jsoup.connect(LOGIN_HEFEI_URL)
                .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)")
                .timeout(20000)
                .data(login_data)
                .execute();
        cookie = response.cookies();
        Document doc = response.parse();
        if (doc.text().contains("failed")) {
            cookie = null;
            return false;
        } else {
            Connection.Response response2 = Jsoup.connect(LOGIN_HEFEI_URL2)
                    .cookies(cookie)
                    .execute();
            cookie = response2.cookies();
            Document doc2 = response2.parse();
            if (doc2.location().endsWith("s_index.htm")) {
                studentNo = sno;
                CourseAssistant.get().setLoggedIn(true);
                welcome();
                return true;
            } else {
                cookie = null;
            }
        }
        return false;
    }

    public static boolean login(String sno, String pwd, boolean isXuancheng) throws IOException {
        CourseAssistant.mLastLoginTime = System.currentTimeMillis();
        if (isXuancheng) {
            baseUrl = BASE_URL_XUANCHENG;
            HashMap<String, String> login_data = new HashMap<>();
            login_data.put("UserStyle", "student");
            login_data.put("user", sno);
            login_data.put("password", pwd);
            Connection.Response response = Jsoup.connect(LOGIN_XUANCHENG_URL)
                    .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)")
                    .timeout(20000)
                    .header("Referer", baseUrl)
                    .header("Origin", baseUrl)
                    .header("Upgrade-Insecure-Requests", "1")
                    .data(login_data)
                    .method(Connection.Method.POST)
                    .execute();
            cookie = response.cookies();
            Document doc = response.parse();
            if (doc.text().contains(CourseAssistant.getInstance()
                    .getString(R.string.login_failed_string))) {
                cookie = null;
                return false;
            }
            studentNo = sno;
            boolean success = cookie != null;
            CourseAssistant.get().setLoggedIn(success);
            if (success) {
                welcome();
            }
            return success;
        } else {
            return login(sno, pwd);
        }
    }

    public static void welcome() throws IOException {
        Document doc = Jsoup.connect(baseUrl + WELCOME_URL)
                .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)")
                .timeout(20000)
                .header("Referer", baseUrl)
                .header("Origin", baseUrl)
                .header("Upgrade-Insecure-Requests", "1")
                .cookies(cookie)
                .get();
        String now = doc.getElementsContainingOwnText("现在是").text();
        now = now.substring(3, now.indexOf("学期") + 2).trim();
        Elements nodes = doc.getElementsContainingOwnText("目前正在开放");
        if (nodes.size() > 0) {
            String str = nodes.text();
            str = str.substring(6, str.indexOf("学期") + 2);
            xqName = str;
            xqValue = getXqdmValue(xqName, 0);
            isOpened = true;
        } else {
            xqName = now;
            xqValue = getXqdmValue(xqName, 1);
            isOpened = false;
        }
    }

    public static String getXqdmValue(String xqdmName, int offset) {
        String[] names = CourseAssistant.get().getResources().getStringArray(R.array.xqdmName);
        String[] values = CourseAssistant.get().getResources().getStringArray(R.array.xqdmValue);
        int pos = 0;
        String value = null;
        for (String s : names) {
            if (s.contains(xqdmName)) {
                value = values[pos + offset];
                break;
            }
            pos++;
        }
        return value;
    }

    public static ArrayList<String> getMembers(String xqdm, String kcdm, String jxbh) {
        Document doc;
        ArrayList<String> list = new ArrayList<>();
        try {
            doc = Jsoup.connect(baseUrl + GET_MEMBERS_URL)
                    .data("xqdm", xqdm)
                    .data("kcdm", kcdm)
                    .data("jxbh", jxbh)
                    .post();
            Elements nodes = doc.select("tr[height=20]");
            for (Element e : nodes) {
                list.add(e.child(0).text() + " \t " + e.child(1).text() + " \t " + e.child(2).text());
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<SelectItem> getSelected() throws IOException {
        if (cookie != null) {
            Document doc = Jsoup.connect(baseUrl + SELECTED_URL)
                    .cookies(cookie)
                    .get();
            Log.d("http get", baseUrl + SELECTED_URL);
            Element tbody = doc.getElementById("TableXKJG").child(0);
            ArrayList<SelectItem> list = new ArrayList<>();
            for (int i = 1; i < tbody.children().size(); i++) {
                Element item = tbody.child(i);
                if (item.tagName().equals("script")) {
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

    public enum CourseType {
        Optional,
        Required,
        Planed
    }

    public static ArrayList<CourseItem> getCourseList(CourseType type) throws IOException {
        String URL = baseUrl + ALL_URL + "?kclx=";
        switch (type) {
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
        if (cookie != null) {
            Document doc;
            doc = Jsoup.connect(URL)
                    .cookies(cookie)
                    .get();
            Log.d("http get", URL);
            Element kcTable = doc.getElementById("KCTable");
            if (kcTable.children().size() > 0) {
                Element tbody = kcTable.child(0);
                ArrayList<CourseItem> list = new ArrayList<>();
                for (Element i : tbody.children()) {
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
        }
        return null;
    }

    public static ArrayList<Classroom> getClassList(String kcdm) throws IOException {
        if (cookie != null) {
            ArrayList<Classroom> list = new ArrayList<>();
            Document doc;
            doc = Jsoup.connect(baseUrl + CLASSES_F3_URL + kcdm)
                    .cookies(cookie)
                    .get();
            Log.d("http get", baseUrl + CLASSES_F3_URL + kcdm);
            Element JXBTable = doc.getElementById("JXBTable");
            if (JXBTable != null && JXBTable.children().size() > 0) {
                Element tbody = JXBTable.child(0);
                for (Element i : tbody.children()) {
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
        }
        return null;
    }

    public static Document submit(HashMap<String, String> data) throws IOException {
        Document doc;
        Connection conn = Jsoup.connect(baseUrl + SUBMIT_URL)
                .cookies(cookie);
        conn.data("xh", studentNo);
        Set<Map.Entry<String, String>> set = data.entrySet();
        for (Map.Entry<String, String> entry : set) {
            conn.data("kcdm", entry.getKey());
            conn.data("jxbh", entry.getValue());
        }
        doc = conn.post();
        return doc;
    }

    public static HashMap<String, Boolean> submit2(HashMap<String, String> data) throws IOException {
        Document doc;
        Connection conn = Jsoup.connect(baseUrl + SUBMIT_URL)
                .cookies(cookie);
        conn.data("xh", studentNo);
        Set<Map.Entry<String, String>> set = data.entrySet();
        for (Map.Entry<String, String> entry : set) {
            conn.data("kcdm", entry.getKey());
            conn.data("jxbh", entry.getValue());
        }
        doc = conn.post();
        if (doc.text().contains("选课结束")) {
            return null;
        }
        return null;
    }

}
