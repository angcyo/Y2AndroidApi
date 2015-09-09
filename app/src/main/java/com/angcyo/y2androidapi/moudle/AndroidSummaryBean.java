package com.angcyo.y2androidapi.moudle;

/**
 * Created by angcyo on 15-07-28-028.
 */
public class AndroidSummaryBean {
    private String type;//返回值 类型
    private String link;//类
    private String descr;// 描述

    public AndroidSummaryBean(String type, String link, String descr) {
        this.type = type;
        this.link = link;
        this.descr = descr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
