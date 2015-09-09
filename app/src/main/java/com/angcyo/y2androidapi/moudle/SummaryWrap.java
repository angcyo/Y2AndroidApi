package com.angcyo.y2androidapi.moudle;

import java.util.List;

/**
 * Created by angcyo on 15-07-28-028.
 */
public class SummaryWrap {
    public static int TYPE_PACK = 1;//是包的描述内容
    public static int TYPE_CLASS = 0;//是类的描述内容
    public int type = TYPE_CLASS;

    List<String> span;//类似 Nested Classes; Constants; Inherited Constants ; Inherited Fields; Public Constructors
    List<AndroidSummaryBean> spanDescr;// span 对应的数据
    List<Integer> spanStartPosition;//span 开始的位置

    public List<Integer> getSpanStartPosition() {
        return spanStartPosition;
    }

    public void setSpanStartPosition(List<Integer> spanStartPosition) {
        this.spanStartPosition = spanStartPosition;
    }

    public SummaryWrap() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getSpan() {
        return span;
    }

    public void setSpan(List<String> span) {
        this.span = span;
    }

    public List<AndroidSummaryBean> getSpanDescr() {
        return spanDescr;
    }

    public void setSpanDescr(List<AndroidSummaryBean> spanDescr) {
        this.spanDescr = spanDescr;
    }
}
