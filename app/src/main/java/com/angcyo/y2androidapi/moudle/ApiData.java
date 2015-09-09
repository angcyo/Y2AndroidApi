package com.angcyo.y2androidapi.moudle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by angcyo on 15-07-26-026.
 */
public class ApiData {

    private static String URL = "http://android.xsoftlab.net/reference/packages.html";
    private static String BASEURL = "http://android.xsoftlab.net/";

    public static Document getDocument(String url)
            throws IOException {
        return Jsoup.connect(url).get();
    }

    public static Document getDocument(File file)
            throws IOException {
        return Jsoup.parse(file, null);
    }


    /**
     * 返回文档中所有的包名
     *
     * @param document the document
     * @return the packages
     */
    public static List<AndroidClassLinkBean> getPackages(Document document) {
        Element element = document.getElementById("packages-nav");
        Elements elements = element.select("a");
        List<AndroidClassLinkBean> links = new ArrayList<>();
        for (Element a : elements) {
            links.add(new AndroidClassLinkBean(a.text(), BASEURL + a.attr("href"), "Packages"));
        }
        return links;
    }

    /**
     * 包括 Annotations Interfaces Classes Enums Exceptions
     */
    public static List<AndroidClassLinkBean> getClasses(Document document) {
        Element element = document.getElementById("classes-nav");
        Element firstChild = element.child(0);

        List<AndroidClassLinkBean> links = new ArrayList<>();
        String className;
        for (int i = 0; i < firstChild.select("h2").size(); i++) {//得到所有子标签
            Element e = firstChild.child(i);
            className = e.select("> h2").first().text();//分类名
            for (Element a : e.select("a")) {//分类下,所有链接
                links.add(new AndroidClassLinkBean(a.text(), BASEURL + a.attr("href"), className));
            }
        }
        return links;
    }

    /**
     * Gets header title. 类似 :BaseAdapter
     *
     * @param document the document
     * @return the header title
     */
    public static String getHeaderTitle(Document document) {
        Element element = document.getElementById("jd-header");
        return element.select("> h1").first().text();
    }

    /**
     * Gets header full title. 类似: public abstract class BaseAdapter extends Object implements ListAdapter SpinnerAdapter
     *
     * @param document the document
     * @return the header full title
     */
    public static String getHeaderFullTitle(Document document) {
        Element element = document.getElementById("jd-header");
        return element.text();
    }

    /**
     * Gets descr.或者整个文档的描述内容
     *
     * @param document the document
     * @return the descr
     */
    public static SummaryWrap getDescr(Document document) {
        Element content = document.getElementById("jd-content");//文档内容包裹div
        Elements descrs = content.select("> .jd-descr");//div 下的描述 class
        Element descr;
        Elements tables;
        SummaryWrap summaryWrap = new SummaryWrap();
        List<String> span = new ArrayList<>();//分段标题
        List<AndroidSummaryBean> spanDescr = new ArrayList<>();//分段内容描述
        List<Integer> spanPosition = new ArrayList<>();//分段开始的位置

        //如果是1, 就是 package 的描述文档
        if (descrs.size() != 2) {
            summaryWrap.setType(SummaryWrap.TYPE_PACK);
            tables = content.select("> h2");
            for (Element title : tables) {//分段标题
                span.add(title.text());
            }
            descrs = content.select("> .jd-sumtable");
            int position = 0;
            for (Element des : descrs) {//分段描述
                Elements trs = des.select("tbody").first().select("> tr");//所有的描述
//                Elements trs = des.select("> table > tbody > tr");//所有的描述
                spanPosition.add(position);
                for (Element t : trs) {//单条描述, 包括返回值, 链接, 说明
                    position++;
                    spanDescr.add(new AndroidSummaryBean("", t.select("> .jd-linkcol").first().text(), t.select("> .jd-descrcol").first().text()));
                }
            }
        }
        //如果是2, 就是 class 类的 描述文档
        else if (descrs.size() == 2) {
            summaryWrap.setType(SummaryWrap.TYPE_CLASS);

            descr = descrs.get(1);//class的描述
            tables = descr.select("> table");//所有的表格, 描述全部都是用的表格标签

            String spanString = "";
            for (Element e : tables) {
                spanString = e.select("th").first().text().replaceAll("\\[\\S*\\]\\s", "");
                span.add(spanString);//span 值

                Elements ds = e.select("> tbody > tr");//所有的描述
                Element tr;//单条描述, 包括返回值, 链接, 说明

                for (int i = 1; i < ds.size(); i++) {
                    tr = ds.get(i);
                    if (tr.select("> td").size() == 2) {//描述内容是2列的话, 以方法为主
                        spanDescr.add(new AndroidSummaryBean(getType(tr), getMethodLink(tr), getMethodDescr(tr)));
                    } else {
                        spanDescr.add(new AndroidSummaryBean(getType(tr), getLink(tr), getDescr(tr)));
                    }
                }
            }
        }
        //其他情况不分析
        summaryWrap.setSpan(span);
        summaryWrap.setSpanDescr(spanDescr);
        summaryWrap.setSpanStartPosition(spanPosition);
        return summaryWrap;
    }

    /**
     * 获取函数返回值
     */
    private static String getType(Element e) {
        try {
            String res = "";
            res = e.select("> td").get(0).text();
            return res;
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
    }

    /**
     * 获取函数文本
     */
    private static String getLink(Element e) {
        try {
            String res = "";
            res = e.select("> td").get(1).text();
            return res;
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
    }

    /**
     * 获取函数描述
     */
    private static String getDescr(Element e) {
        try {
            String res = "";
            res = e.select("> td").get(2).text();
            return res;
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
    }

    /**
     * 获取函数文本,第二种方法
     */
    private static String getMethodLink(Element e) {
        try {
            String res = "";
            res = e.select("> td").get(1).child(0).text();
            return res;
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
    }

    /**
     * 获取函数描述,第二种方法
     */
    private static String getMethodDescr(Element e) {
        try {
            String res = "";
            res = e.select("> td").get(1).child(1).text();
            return res;
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
    }

}
