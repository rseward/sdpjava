package com.bestbuy.sdp.ejb;


/*
  ** This file was automatically generated by 
  ** EJBGen WebLogic Server 10.3.3.0  Fri Apr 9 00:05:28 PDT 2010 1321401 
*/


import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;
import java.util.Collection;

// BEGIN imports from bean class
import com.accenture.common.ex.ApplicationException;
import com.bestbuy.schemas.sdp.tpa.kcb.ReleaseKeyCodeResponseDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeResponseDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.UpdateKeyCodeResponseDocument;
import com.bestbuy.sdp.services.KeyCodeProviderService;
import javax.ejb.SessionBean;
import org.apache.xmlbeans.XmlObject;
import weblogic.ejb.GenericSessionBean;
import weblogic.ejbgen.Constants;
import weblogic.ejbgen.FileGeneration;
import weblogic.ejbgen.JndiName;
import weblogic.ejbgen.LocalMethod;
import weblogic.ejbgen.RemoteMethod;
import weblogic.ejbgen.Session;
// END imports from bean class


/**
 * * 
 * GenericSessionBean subclass automatically generated by OEPE.
 * Please complete the ejbCreate method as needed to properly initialize new instances of your bean and add
 * all required business methods. Also, review the Session, JndiName and FileGeneration annotations 
 * to ensure the settings match the bean's intended use.
 
 */

public interface KeyCodeBankServiceLocalHome extends EJBLocalHome {



  public KeyCodeBankServiceLocal create()     throws CreateException, javax.ejb.CreateException;



}
