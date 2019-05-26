package com.purej.vminspect.data.statistics.rrd;

import java.io.IOException;
import com.purej.vminspect.data.statistics.Range;

/**
 * Defines the abstraction for a single round-robin-database. This allows switching
 * between different providers (for example JRobin, RRD4J, etc.)
 *
 * @author Stefan Mueller
 */
public interface Rrd {
  /**
   * Returns the technical name (RRD datasource name).
   */
  public String getName();

  /**
   * Adds a new value to this graph.
   * @throws IOException if adding the value failed
   */
  void addValue(double value) throws IOException;

  /**
   * Creates a graphics binary of this statistics in PNG format.
   *
   * @param label the label to be shown on the generated statistics graphics
   * @param unit the unit to be shown on the generated statistics graphics
   * @param range the range to be shown
   * @param width the width of the created PNG
   * @param height the height of the created PNG
   * @return the created binary image data
   * @throws IOException if image creation failed
   */
  byte[] createPng(String label, String unit, Range range, int width, int height) throws IOException;
}
