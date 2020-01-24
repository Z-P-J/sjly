package com.zpj.shouji.market.model.article;

public class TextElement extends HtmlElement {

    String text;
    String textColor;
    String textIndent;
    boolean isStrong;
    String strongText;

    public String getText() {
        return text;
    }

    public boolean isStrong() {
        return isStrong;
    }

}
