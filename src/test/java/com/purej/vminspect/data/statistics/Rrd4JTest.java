// Copyright (c), 2019, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import org.junit.jupiter.api.Test;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

/**
 * Tests some RRD4J stuff.
 *
 * @author Stefan Mueller
 */
public class Rrd4JTest {

  /**
   * Tests the named functionality.
   */
  @Test
  public void testBasics1() throws Exception {
    // Create db:
    String myDb = "target/rrd4j/test.rrd";
    new File(myDb).getParentFile().mkdirs();

    // first, define the RRD
    RrdDef rrdDef = new RrdDef(myDb, 300);
    rrdDef.addDatasource("speed", DsType.COUNTER, 600, Double.NaN, Double.NaN);
    rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 1, 600); // 1 step, 600 rows
    rrdDef.addArchive(ConsolFun.AVERAGE, 0.5, 6, 700); // 6 steps, 700 rows
    rrdDef.addArchive(ConsolFun.MAX, 0.5, 1, 600);

    // then, create a RrdDb from the definition and start adding data
    try (RrdDb rrdDb = RrdDb.getBuilder().setRrdDef(rrdDef).build()) {
      Sample sample = rrdDb.createSample();
      sample.setValue("speed", 22.7);
      sample.update();
    }

    // then create a graph definition
    RrdGraphDef gDef = new RrdGraphDef(1, 2);
    gDef.setWidth(500);
    gDef.setHeight(300);
    gDef.setFilename("speed.png");
    gDef.setTitle("My Title");
    gDef.setVerticalLabel("bytes");
    gDef.datasource("speed-average", myDb, "speed", ConsolFun.AVERAGE);
    gDef.line("speed-average", Color.BLUE, "Speed In");
    gDef.hrule(2568, Color.GREEN, "hrule");
    gDef.setImageFormat("png");
    gDef.setFilename("-"); // Important for in-memory generation!

    // then actually draw the graph
    byte[] bytes = new RrdGraph(gDef).getRrdGraphInfo().getBytes();
    FileOutputStream output = new FileOutputStream("target/rrd4j/test.png");
    try {
      output.write(bytes);
    } finally {
      output.close();
    }
  }

}
