package org.logstashplugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.snowplowanalytics.refererparser.Parser;
import com.snowplowanalytics.refererparser.Referer;

import co.elastic.logstash.api.PluginConfigSpec;

class Main {

  public static final PluginConfigSpec<String> REFERRER_CONFIG = PluginConfigSpec.stringSetting("referrer", "message");
  public static final PluginConfigSpec<String> SOURCE_CONFIG = PluginConfigSpec.stringSetting("host", "message");

  public static void main(String[] args) {
    // String refererUrl =
    // "http://www.google.com/search?q=gateway+oracle+cards+denise+linn&hl=en&client=safari";
    // String pageUrl = "http://www.google.com/shop"; // Our current URL
    String refererUrl = "http://www.chat-tool.com/search?q=gateway+oracle+cards+denise+linn&hl=en&client=safari";
    String pageUrl = "www.test.com"; // Our current URL

    try {

      List<PluginConfigSpec<?>> array = new ArrayList<PluginConfigSpec<?>>();
      array.add(REFERRER_CONFIG);
      array.add(SOURCE_CONFIG);
      List a = Collections.synchronizedList(array);
      System.out.println(a.size());

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
