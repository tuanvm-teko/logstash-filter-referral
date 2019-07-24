package org.logstashplugins;

import com.snowplowanalytics.refererparser.Parser;
import com.snowplowanalytics.refererparser.Referer;

class Main {
  public static void main(String[] args) {
    // String refererUrl =
    // "http://www.google.com/search?q=gateway+oracle+cards+denise+linn&hl=en&client=safari";
    // String pageUrl = "http://www.google.com/shop"; // Our current URL
    String refererUrl = "http://www.chat-tool.com/search?q=gateway+oracle+cards+denise+linn&hl=en&client=safari";
    String pageUrl = "www.chat-tool.com"; // Our current URL

    try {
      Parser refererParser = new Parser();
      Referer r = refererParser.parse(refererUrl, pageUrl);
      System.out.println(r.medium); // => "search"
      System.out.println(r.source); // => "Google"
      System.out.println(r.term); // => "gateway oracle cards denise linn"
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }
  }
}
