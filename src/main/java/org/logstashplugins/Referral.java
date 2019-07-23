package org.logstashplugins;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.Filter;
import co.elastic.logstash.api.FilterMatchListener;
import co.elastic.logstash.api.LogstashPlugin;
import co.elastic.logstash.api.PluginConfigSpec;
import org.apache.commons.lang3.StringUtils;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.snowplowanalytics.refererparser.CorruptYamlException;
import com.snowplowanalytics.refererparser.Parser;
import com.snowplowanalytics.refererparser.Referer;

// class name must match plugin name
@LogstashPlugin(name = "referral")
public class Referral implements Filter {

  public static final PluginConfigSpec<String> SOURCE_CONFIG = PluginConfigSpec.stringSetting("source", "message");

  private String id;
  private String sourceField;
  private Parser refererParser;

  public Referral(String id, Configuration config, Context context) throws CorruptYamlException {
    // constructors should validate configuration options
    this.id = id;
    this.sourceField = config.get(SOURCE_CONFIG);
    this.refererParser = new Parser();
  }

  @Override
  public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {
    for (Event e : events) {
      Object f = e.getField(sourceField);
      if (f instanceof String) {
        try {
          Referer referral = this.refererParser.parse((String) f, "");
          Map<String, String> map = new HashMap<String, String>();
          map.put("source", referral.source);
          map.put("term", referral.term);
          try {
            map.put("medium", referral.medium.name().toLowerCase());
          } catch (Exception ex) {
            map.put("medium", null);
          }

          e.setField("referralParser", map);
          matchListener.filterMatched(e);
        } catch (URISyntaxException e1) {
          e1.printStackTrace();
        }
      }
    }
    return events;
  }

  @Override
  public Collection<PluginConfigSpec<?>> configSchema() {
    // should return a list of all configuration options for this plugin
    return Collections.singletonList(SOURCE_CONFIG);
  }

  @Override
  public String getId() {
    return this.id;
  }
}
