// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.server;

import javax.servlet.http.HttpServletRequest;
import com.purej.vminspect.data.MBeanAttribute;
import com.purej.vminspect.data.MBeanData;
import com.purej.vminspect.data.MBeanOperation;
import com.purej.vminspect.http.MBeanAccessControl;
import com.purej.vminspect.http.servlet.MBeanAccessControlFactory;

/**
 * A custom impl of the {@link MBeanAccessControlFactory} for test purposes.
 *
 * @author Stefan Mueller
 */
public class CustomMBeanAccessControlFactory implements MBeanAccessControlFactory {
  @Override
  public MBeanAccessControl create(final HttpServletRequest request) {
    System.out.println("Creating a custom MBeanAccessControl for request " + request.hashCode());
    return new MBeanAccessControl() {

      @Override
      public boolean isChangeAllowed(MBeanData mbean, MBeanAttribute attribute) {
        System.out.println("MBeanAccessControl.isChangeAllowed for MBean " + mbean.getName().getObjectNameString() + "." + attribute.getName()
            + " and request " + request.hashCode());
        return true;
      }

      @Override
      public boolean isCallAllowed(MBeanData mbean, MBeanOperation operation) {
        System.out.println("MBeanAccessControl.isCallAllowed for MBean " + mbean.getName().getObjectNameString() + "." + operation.getName()
            + " and request " + request.hashCode());
        return true;
      }

      @Override
      public boolean needsChangeConfirmation(MBeanData mbean, MBeanAttribute attribute) {
        System.out.println("MBeanAccessControl.needsChangeConfirmation for MBean " + mbean.getName().getObjectNameString() + "."
            + attribute.getName() + " and request " + request.hashCode());
        return false;
      }

      @Override
      public boolean needsCallConfirmation(MBeanData mbean, MBeanOperation operation) {
        System.out.println("MBeanAccessControl.needsCallConfirmation for MBean " + mbean.getName().getObjectNameString() + "." + operation.getName()
            + " and request " + request.hashCode());
        return true;
      }

      @Override
      public void attributeChanged(MBeanData mbean, MBeanAttribute attribute, Object newValue) {
        System.out.println("MBeanAccessControl.attributeChanged for MBean " + mbean.getName().getObjectNameString() + "." + attribute.getName()
            + " and request " + request.hashCode());
      }

      @Override
      public void operationCalled(MBeanData mbean, MBeanOperation operation, String[] params, Object result) {
        System.out.println("MBeanAccessControl.operationCalled for MBean " + mbean.getName().getObjectNameString() + "." + operation.getName()
            + " and request " + request.hashCode());
      }
    };
  }
}
