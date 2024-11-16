package com.purej.vminspect.http.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.core.env.Environment;
import com.purej.vminspect.http.MBeanAccessControl;
import jakarta.annotation.PostConstruct;

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
      addUrlMappings(env.getProperty("vminspect.path", "/inspect/*"));
      var defaultDomainFilter = env.getProperty("vminspect.mbeans.default-domain-filter");
      var mbeansReadonly = env.getProperty("vminspect.mbeans.readonly", Boolean.class, false);
      var mbeansWriteConfirmation = env.getProperty("vminspect.mbeans.write-confirmation", Boolean.class, false);
      var accessControlFactoryClz = env.getProperty("vminspect.mbeans.access-control-factory");
      var collectionFrequency = env.getProperty("vminspect.statistics.collection.frequency-ms", Integer.class, 60000);
      var storageDir = env.getProperty("vminspect.statistics.storage.dir");
      getServlet().init(accessControlFactoryClz, defaultDomainFilter, mbeansReadonly, mbeansWriteConfirmation, collectionFrequency, storageDir);
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
  public void init(String path, MBeanAccessControlFactory mbeanAccessControlFactory, int statisticsCollectionFrequencyMs, String statisticsStorageDir) {
    addUrlMappings(path);
    getServlet().init(mbeanAccessControlFactory, statisticsCollectionFrequencyMs, statisticsStorageDir);
  }
}
