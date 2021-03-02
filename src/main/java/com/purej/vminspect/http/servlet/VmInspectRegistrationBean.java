package com.purej.vminspect.http.servlet;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.core.env.Environment;
import com.purej.vminspect.http.MBeanAccessControl;

/**
 * A spring-boot registration bean to simplify adding
 * the VM inspection to a spring-boot application.
 *
 * @author Stefan Mueller
 */
public class VmInspectRegistrationBean extends ServletRegistrationBean<VmInspectionServlet> {
  @Autowired
  private Environment env;

  /**
   * Create a new instance.
   */
  public VmInspectRegistrationBean() {
    super(new VmInspectionServlet());
  }

  /**
   * Auto-initializes this instance based on spring-bean lifecycle and spring-properties.
   * Does nothing if already initialized.
   */
  @PostConstruct
  public void init() {
    if (!getServlet().isInitialized()) {
      // Load configuration from init parameters:
      String path = env.getProperty("vminspect.path", "/inspect/*");
      boolean mbeansReadonly = env.getProperty("vminspect.mbeans.readonly", Boolean.class, false);
      boolean mbeansWriteConfirmation = env.getProperty("vminspect.mbeans.write-confirmation", Boolean.class, false);
      String accessControlFactoryClz = env.getProperty("vminspect.mbeans.access-control-factory");
      int collectionFrequency = env.getProperty("vminspect.statistics.collection.frequency-ms", Integer.class, 60000);
      String storageDir = env.getProperty("vminspect.statistics.storage.dir");
      addUrlMappings(path);
      getServlet().init(accessControlFactoryClz, mbeansReadonly, mbeansWriteConfirmation, collectionFrequency, storageDir);
    }
  }

  /**
   * Initializes this VM inspection instance programmatically.
   * Note: Initialize can only be called once for this instance!
   *
   * @param path the path to bind the VM inspection to, typically /inspect/*
   * @param mbeanAccessControlFactory the factory to create {@link MBeanAccessControl} instances for fine-grained MBeans access control
   * @param statisticsCollectionFrequencyMs the statistics collection frequency in milliseconds (60'000 recommended)
   * @param statisticsStorageDir the optional statistics storage directory
   */
  public void init(String path, MBeanAccessControlFactory mbeanAccessControlFactory, int statisticsCollectionFrequencyMs,
      String statisticsStorageDir) {
    addUrlMappings(path);
    getServlet().init(mbeanAccessControlFactory, statisticsCollectionFrequencyMs, statisticsStorageDir);
  }
}
