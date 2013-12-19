package com.bestbuy.sdp.ejb;

import javax.ejb.SessionBean;

import org.apache.xmlbeans.XmlObject;

import weblogic.ejb.GenericSessionBean;
import weblogic.ejbgen.Constants;
import weblogic.ejbgen.FileGeneration;
import weblogic.ejbgen.JndiName;
import weblogic.ejbgen.LocalMethod;
import weblogic.ejbgen.RemoteMethod;
import weblogic.ejbgen.Session;

import com.accenture.common.ex.ApplicationException;
import com.bestbuy.schemas.sdp.tpa.kcb.ReleaseKeyCodeResponseDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeResponseDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.UpdateKeyCodeResponseDocument;
import com.bestbuy.sdp.services.KeyCodeProviderService;

/**
 * GenericSessionBean subclass automatically generated by OEPE.
 *
 * Please complete the ejbCreate method as needed to properly initialize new instances of your bean and add
 * all required business methods. Also, review the Session, JndiName and FileGeneration annotations 
 * to ensure the settings match the bean's intended use.
 */
@Session(ejbName = "KeyCodeBankService")
@JndiName(remote = "ejb.KeyCodeBankServiceRemoteHome", local = "ejb.KeyCodeBankServiceLocalHome")
@FileGeneration(remoteClass = Constants.Bool.TRUE, remoteHome = Constants.Bool.TRUE, 
		localClass = Constants.Bool.TRUE, localHome = Constants.Bool.TRUE)
public class KeyCodeBankService extends GenericSessionBean implements
		SessionBean {
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see weblogic.ejb.GenericSessionBean#ejbCreate()
	 */
	public void ejbCreate() {
		// IMPORTANT: Add your code here
	}

	// IMPORTANT: Add business methods
	@LocalMethod
	@RemoteMethod
	public ReserveKeyCodeResponseDocument reserveKeyCode(
			XmlObject requestXmlObject) throws ApplicationException {
		return new KeyCodeProviderService().reserveKeyCode(requestXmlObject);
	}

	@RemoteMethod
	@LocalMethod
	public UpdateKeyCodeResponseDocument updateKeyCode(
			XmlObject requestXmlObject) throws ApplicationException {
		return new KeyCodeProviderService().updateKeyCode(requestXmlObject);
	}

	@RemoteMethod
	@LocalMethod
	public ReleaseKeyCodeResponseDocument releaseKeyCode(
			XmlObject requestXmlObject) throws ApplicationException {
		return new KeyCodeProviderService().releaseKeyCode(requestXmlObject);
	}
	
	@RemoteMethod
	@LocalMethod
	public ReserveKeyCodeResponseDocument reserveKeyCodeByMasterItemId(
			XmlObject requestXmlObject) throws ApplicationException {
		return new KeyCodeProviderService().reserveKeyCodeByMasterItemId(requestXmlObject);
	}


}