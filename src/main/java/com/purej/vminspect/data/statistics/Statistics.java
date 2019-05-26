// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.data.statistics;

import java.io.IOException;
import com.purej.vminspect.data.SystemData;
import com.purej.vminspect.data.statistics.rrd.Rrd;
import com.purej.vminspect.util.Utils;

/**
 * Represents a single statistics using a round-robin-data-base (RRD), for example JRobin or RRD4J.
 * See https://de.wikipedia.org/wiki/RRDtool for RRD background.
 *
 * @author Stefan Mueller
 */
public final class Statistics {
  private final String _label;
  private final String _unit;
  private final String _description;
  private final ValueProvider _valueProvider;
  private final Rrd _rrd;

  /**
   * Creates a new instance of this class.
   *
   * @param name the name of the statistics, must be a simple name without spaces and special characters
   * @param label the label to be shown on the generated statistics graphics
   * @param unit the unit to be shown on the generated statistics graphics
   * @param description the description to be shown on the UI
   * @param valueProvider the provider for statistics values
   * @param rrd the round-robin-database abstraction
   * @throws IOException if a JRobin file access error occurred
   */
  public Statistics(String name, String label, String unit, String description, ValueProvider valueProvider, Rrd rrd)
      throws IOException {
    if (name == null || name.length() > 20) {
      throw new IllegalArgumentException("RRD name should not be empty or exceed 20 characaters!");
    }
    _label = Utils.checkNotNull(label);
    _unit = Utils.checkNotNull(unit);
    _description = Utils.checkNotNull(description);
    _valueProvider = Utils.checkNotNull(valueProvider);
    _rrd = Utils.checkNotNull(rrd);
  }

  /**
   * Returns the technical name (JRobin datasource name).
   */
  public String getName() {
    return _rrd.getName();
  }

  /**
   * Returns the label for the UI.
   */
  public String getLabel() {
    return _label;
  }

  /**
   * Returns the description for the UI.
   */
  public String getDescription() {
    return _description;
  }

  /**
   * Collects the current value of this statistics using the configured {@link ValueProvider}.
   * This method will be called on a regular basis by the {@link StatisticsCollector}.
   */
  public void collectValue(SystemData data) throws IOException {
    double value = _valueProvider.getValue(data);
    _rrd.addValue(value);
  }

  /**
   * Creates a graphics binary of this statistics in PNG format.
   *
   * @param range the range to be shown
   * @param width the width of the created PNG
   * @param height the height of the created PNG
   * @return the created binary image data
   * @throws IOException if image creation failed
   */
  public byte[] createGraph(Range range, int width, int height) throws IOException {
    return _rrd.createPng(_label, _unit, range, width, height);
  }
}
