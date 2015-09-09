package com.angcyo.y2androidapi.moudle;

/**
 * Created by angcyo on 15-07-26-026.
 */
public class AndroidLinkBean {
    private String text;//链接的文本
    private String link;//链接的url

    public AndroidLinkBean(String text, String link) {
        this.text = text;
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
