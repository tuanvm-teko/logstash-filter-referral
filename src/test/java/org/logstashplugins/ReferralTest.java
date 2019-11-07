package org.logstashplugins;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.FilterMatchListener;
import org.junit.Assert;
import org.junit.Test;
import org.logstash.plugins.ConfigurationImpl;
import org.logstash.plugins.ContextImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.snowplowanalytics.refererparser.CorruptYamlException;

public class ReferralTest {
  String sourceField = "sourceField";
  String referrerField = "referrerField";
  @Test
  public void internal() throws CorruptYamlException {

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("host", sourceField);
    map.put("referrer", referrerField);

    Configuration config = new ConfigurationImpl(map);
    // Context context = new ContextImpl(null);
    Referral filter = new Referral("test-id", config, null);

    Event e = new org.logstash.Event();
    TestMatchListener matchListener = new TestMatchListener();
    e.setField(sourceField, "http://www.chat-tool.com/search");
    e.setField(referrerField, "http://www.chat-tool.com/search?q=gateway+oracle+cards+denise+linn&hl=en&client=safari");
    Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);

    Assert.assertEquals(1, results.size());
    Assert.assertEquals("internal", ((Map) e.getField("referralParser")).get("medium"));
    Assert.assertEquals("unknown", ((Map) e.getField("referralParser")).get("source"));
    Assert.assertEquals("unknown", ((Map) e.getField("referralParser")).get("term"));
    Assert.assertEquals(1, matchListener.getMatchCount());
  }

  @Test
  public void search() throws CorruptYamlException {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("host", sourceField);
    map.put("referrer", referrerField);


    Configuration config = new ConfigurationImpl(map);
    // Context context = new ContextImpl(null);
    Referral filter = new Referral("test-id", config, null);

    Event e = new org.logstash.Event();
    TestMatchListener matchListener = new TestMatchListener();
    e.setField(sourceField, "http://phongvu.com");
    e.setField(referrerField, "http://google.com");
    Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);

    Assert.assertEquals(1, results.size());
    Assert.assertEquals("search", ((Map) e.getField("referralParser")).get("medium"));
    Assert.assertEquals("Google", ((Map) e.getField("referralParser")).get("source"));
    Assert.assertEquals(1, matchListener.getMatchCount());
  }

  @Test
  public void string_empty() throws CorruptYamlException {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("host", sourceField);
    map.put("referrer", referrerField);


    Configuration config = new ConfigurationImpl(map);
    // Context context = new ContextImpl(null);
    Referral filter = new Referral("test-id", config, null);

    Event e = new org.logstash.Event();
    TestMatchListener matchListener = new TestMatchListener();
    e.setField(sourceField, "");
    e.setField(referrerField, "");
    Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);

    Assert.assertEquals(1, results.size());
    Assert.assertEquals("unknown", ((Map) e.getField("referralParser")).get("medium"));
    Assert.assertEquals("unknown", ((Map) e.getField("referralParser")).get("source"));
    Assert.assertEquals("unknown", ((Map) e.getField("referralParser")).get("term"));
  }

  @Test
  public void null_() throws CorruptYamlException {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("host", sourceField);
    map.put("referrer", referrerField);


    Configuration config = new ConfigurationImpl(map);
    // Context context = new ContextImpl(null);
    Referral filter = new Referral("test-id", config, null);

    Event e = new org.logstash.Event();
    TestMatchListener matchListener = new TestMatchListener();
    e.setField(sourceField, null);
    e.setField(referrerField, null);
    Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);

    Assert.assertEquals(1, results.size());
    Assert.assertEquals("unknown", ((Map) e.getField("referralParser")).get("medium"));
    Assert.assertEquals("unknown", ((Map) e.getField("referralParser")).get("source"));
    Assert.assertEquals("unknown", ((Map) e.getField("referralParser")).get("term"));
  }
}

class TestMatchListener implements FilterMatchListener {

  private AtomicInteger matchCount = new AtomicInteger(0);

  @Override
  public void filterMatched(Event event) {
    matchCount.incrementAndGet();
  }

  public int getMatchCount() {
    return matchCount.get();
  }
}
