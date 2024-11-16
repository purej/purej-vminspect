// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import org.jrobin.core.FetchData;
import org.jrobin.core.FetchRequest;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.Sample;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;
import org.junit.jupiter.api.Test;

/**
 * Tests some JRobin stuff.
 *
 * @author Stefan Mueller
 */
public class JRobinTest {

  /**
   * Tests the named functionality.
   */
  @Test
  public void testBasics1() throws Exception {
    // Create db:
    String myDb = "target/jrobin/test.rrd";
    new File(myDb).getParentFile().mkdirs();
    RrdDef rrdDef = new RrdDef(myDb);
    rrdDef.setStartTime(920804400L);
    rrdDef.addDatasource("speed", "COUNTER", 600, Double.NaN, Double.NaN);
    rrdDef.addArchive("AVERAGE", 0.5, 1, 24);
    rrdDef.addArchive("AVERAGE", 0.5, 6, 10);
    RrdDb rrdDb = new RrdDb(rrdDef);
    rrdDb.close();

    // Add data:
    rrdDb = new RrdDb(myDb);
    Sample sample = rrdDb.createSample();
    sample.setAndUpdate("920804700:12345");
    sample.setAndUpdate("920805000:12357");
    sample.setAndUpdate("920805300:12363");
    sample.setAndUpdate("920805600:12363");
    sample.setAndUpdate("920805900:12363");
    sample.setAndUpdate("920806200:12373");
    sample.setAndUpdate("920806500:12383");
    sample.setAndUpdate("920806800:12393");
    sample.setAndUpdate("920807100:12399");
    sample.setAndUpdate("920807400:12405");
    sample.setAndUpdate("920807700:12411");
    sample.setAndUpdate("920808000:12415");
    sample.setAndUpdate("920808300:12420");
    sample.setAndUpdate("920808600:12422");
    sample.setAndUpdate("920808900:12423");
    rrdDb.close();

    // Fetch data:
    rrdDb = new RrdDb(myDb);
    FetchRequest fetchRequest = rrdDb.createFetchRequest("AVERAGE", 920804400L, 920809200L);
    FetchData fetchData = fetchRequest.fetchData();
    System.out.println("FetchData Dump: " + fetchData.dump());
    rrdDb.close();

    // Create graph:
    RrdGraphDef graphDef = new RrdGraphDef();
    graphDef.datasource("myspeed", myDb, "speed", "AVERAGE");
    graphDef.line("myspeed", new Color(0xFF, 0, 0), null, 2);
    graphDef.setImageFormat("png");
    graphDef.setWidth(300);
    graphDef.setHeight(200);
    byte[] bytes = new RrdGraph(graphDef).getRrdGraphInfo().getBytes();
    FileOutputStream output = new FileOutputStream("target/jrobin/test.png");
    try {
      output.write(bytes);
    }
    finally {
      output.close();
    }
  }

}
