//package com.zpj.http.examples;
//
//import com.zpj.http.ZHttp;
//import com.zpj.http.parser.html.nodes.Document;
//import com.zpj.http.parser.html.nodes.Element;
//import com.zpj.http.parser.html.select.Elements;
//
//import java.io.IOException;
//
///**
// * A simple example, used on the jsoup website.
// */
//public class Wikipedia {
//    public static void main(String[] args) throws IOException {
//        Document doc = ZHttp.connect("http://en.wikipedia.org/").get();
//        log(doc.title());
//
//        Elements newsHeadlines = doc.select("#mp-itn b a");
//        for (Element headline : newsHeadlines) {
//            log("%s\n\t%s", headline.attr("title"), headline.absUrl("href"));
//        }
//    }
//
//    private static void log(String msg, String... vals) {
//        System.out.println(String.format(msg, vals));
//    }
//}
