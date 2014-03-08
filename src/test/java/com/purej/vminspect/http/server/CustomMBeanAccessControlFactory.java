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
        printInfo("MBeanAccessControl.isChangeAllowed", mbean, "." + attribute.getName() + " and request " + request.hashCode());
        return true;
      }

      @Override
      public boolean isCallAllowed(MBeanData mbean, MBeanOperation operation) {
        printInfo("MBeanAccessControl.isCallAllowed", mbean, "." + operation.getName() + " and request " + request.hashCode());
        return true;
      }

      @Override
      public boolean needsChangeConfirmation(MBeanData mbean, MBeanAttribute attribute) {
        printInfo("MBeanAccessControl.needsChangeConfirmation", mbean, "." + attribute.getName() + " and request " + request.hashCode());
        return false;
      }

      @Override
      public boolean needsCallConfirmation(MBeanData mbean, MBeanOperation operation) {
        printInfo("MBeanAccessControl.needsCallConfirmation", mbean, "." + operation.getName() + " and request " + request.hashCode());
        return true;
      }

      @Override
      public void attributeChanged(MBeanData mbean, MBeanAttribute attribute, Object newValue) {
        printInfo("MBeanAccessControl.attributeChanged", mbean, "." + attribute.getName() + " and request " + request.hashCode());
      }

      @Override
      public void attributeChangeFailed(MBeanData mbean, MBeanAttribute attribute, Exception exception) {
        printInfo("MBeanAccessControl.attributeChangeFailed", mbean, "." + attribute.getName() + " and request " + request.hashCode());
      }

      @Override
      public void operationCalled(MBeanData mbean, MBeanOperation operation, String[] params, Object result) {
        printInfo("MBeanAccessControl.operationCalled", mbean, "." + operation.getName() + " and request " + request.hashCode());
      }

      @Override
      public void operationCallFailed(MBeanData mbean, MBeanOperation operation, String[] params, Exception exception) {
        printInfo("MBeanAccessControl.operationCallFailed", mbean, "." + operation.getName() + " and request " + request.hashCode());
      }

      private void printInfo(String method, MBeanData mbean, String addOnInfo) {
        System.out.println(method + " for MBean " + mbean.getName().getObjectNameString() + addOnInfo);
      }
    };
  }
}
