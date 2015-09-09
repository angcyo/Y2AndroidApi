package com.angcyo.y2androidapi.moudle;

/**
 * Created by angcyo on 15-07-26-026.
 */
public class AndroidClassLinkBean extends AndroidLinkBean {
    private String classify;//属于那个分类的链接

    public AndroidClassLinkBean(String text, String link, String classify) {
        super(text, link);
        this.classify = classify;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }
}
