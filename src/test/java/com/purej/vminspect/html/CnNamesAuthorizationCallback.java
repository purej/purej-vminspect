// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.html;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example {@link AuthorizationCallback} implementation that checks CN names. Two-way SSL must be enabled for this to work.
 *
 * @author Stefan Mueller
 */
public class CnNamesAuthorizationCallback implements AuthorizationCallback {
  private static final Logger LOGGER = LoggerFactory.getLogger(CnNamesAuthorizationCallback.class);
  private final Set<String> _authorizationPrincipals = new HashSet<String>();

  /**
   * Creates a new instance of this class.
   */
  public CnNamesAuthorizationCallback() {
    _authorizationPrincipals.add("hans dampf");
    _authorizationPrincipals.add("stefan mueller");
  }

  @Override
  public boolean isAuthorized(HttpServletRequest request) {
    try {
      // Load the certificate's DN name:
      X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
      if (certs == null || certs.length == 0) {
        LOGGER.debug("No client certificate provided, authentication failed.");
        return false;
      }
      Principal subjectDN = certs[0].getSubjectDN();

      // Parse out the CN name:
      String cn = "";
      for (Rdn rdn : new LdapName(subjectDN.getName()).getRdns()) {
        if (rdn.getType().equalsIgnoreCase("CN")) {
          cn = rdn.getValue().toString();
          break;
        }
      }
      boolean result = _authorizationPrincipals.contains(cn);
      if (result) {
        LOGGER.debug("Authentication for CN '" + cn + "' ok");
      }
      else {
        LOGGER.debug("Authentication for CN '" + cn + "' failed, CN is not in the configured list of authorization principals!");
      }
      return result;
    }
    catch (Exception e) {
      LOGGER.debug("Authentication failed!", e);
      return false;
    }
  }
}
