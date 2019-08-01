package org.logstashplugins;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.Filter;
import co.elastic.logstash.api.FilterMatchListener;
import co.elastic.logstash.api.LogstashPlugin;
import co.elastic.logstash.api.PluginConfigSpec;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.snowplowanalytics.refererparser.CorruptYamlException;
import com.snowplowanalytics.refererparser.Parser;
import com.snowplowanalytics.refererparser.Referer;

// class name must match plugin name
@LogstashPlugin(name = "referral")
public class Referral implements Filter {

  public static final PluginConfigSpec<String> REFERRER_CONFIG = PluginConfigSpec.stringSetting("referrer", "message");
  public static final PluginConfigSpec<String> SOURCE_CONFIG = PluginConfigSpec.stringSetting("host", "message");

  private String id;
  private String referrerField;
  private String sourceField;
  private Parser refererParser;

  public Referral(String id, Configuration config, Context context) throws CorruptYamlException {
    // constructors should validate configuration options
    this.id = id;
    this.referrerField = config.get(REFERRER_CONFIG);
    this.sourceField = config.get(SOURCE_CONFIG);
    this.refererParser = new Parser();
  }

  private void setEvent(Event e, FilterMatchListener matchListener, String referrer, String hostName) {
    if (referrer == "" | hostName == "" | referrer == null | hostName == null)
      return;
    try {
      URI hostUri = new URI(hostName);
      Referer referral = this.refererParser.parse(referrer, hostUri.getHost());
      if (referral == null)
        return;

      Map<String, String> map = new HashMap<String, String>();
      if (referral.source != null) {
        map.put("source", referral.source);
      }
      if (referral.term != null) {
        map.put("term", referral.term);
      }
      try {
        if (referral.medium != null) {
          map.put("medium", referral.medium.name().toLowerCase());
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      try {
        e.setField("referralParser", map);
        matchListener.filterMatched(e);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } catch (URISyntaxException e1) {
      e1.printStackTrace();
    }
  }

  private String _getValueFromField(Event e, String field) {
    Object input = e.getField(field);
    if (input instanceof List) {
      return (String) ((List) input).get(0);
    } else if (input instanceof String) {
      return (String) input;
    } else {
      return "";
    }
  }

  @Override
  public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {
    for (Event e : events) {
      String referrerValue = this._getValueFromField(e, this.referrerField);
      String sourceValue = this._getValueFromField(e, this.sourceField);
      if (referrerValue != "" & sourceValue != "" & referrerValue != null & sourceValue != null) {
        this.setEvent(e, matchListener, referrerValue, sourceValue);
      }

    }
    return events;
  }

  @Override
  public Collection<PluginConfigSpec<?>> configSchema() {
    List<PluginConfigSpec<?>> array = new ArrayList<PluginConfigSpec<?>>();
    array.add(REFERRER_CONFIG);
    array.add(SOURCE_CONFIG);
    return Collections.synchronizedList(array);
  }

  @Override
  public String getId() {
    return this.id;
  }
}
