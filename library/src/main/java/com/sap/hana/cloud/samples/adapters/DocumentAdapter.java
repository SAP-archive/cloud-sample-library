package com.sap.hana.cloud.samples.adapters;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.util.IOUtils;

import com.sap.ecm.api.EcmService;
import com.sap.ecm.api.RepositoryOptions;

/**
 * This class accesses functionalities of the document service provided by
 * SAP HANA Cloud Platform. It is used for storing and retrieving document
 * content in a folder-like structure. In the current application, the document
 * service is used for storing profile images.
 *
 * @see <a href="https://help.hana.ondemand.com/help/frameset.htm?e60b7e45bb57101487a881c7c5487778.html">SAP HANA Cloud Document Service</a>
 */
public class DocumentAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentAdapter.class);

    private static Session cmisSession = null;
    private static final String UNIQUE_NAME = "com.sap.library.ecm.document.repository.01";
    private static final String UNIQUE_KEY = "com.sap.library.ecm.h14PJthdmpHGHdskFFjlk";

    private String name;
    

    public DocumentAdapter(String fileName) {
    	if (fileName == null) {
    		throw new IllegalArgumentException("Parameter fileName cannot be null.");
    	}
    	this.name = fileName;
    }
    
    /**
     * This method uploads a document represented as a byte array into the document service repository.
     * If a document with the same name exists then it is replaced with the new one.
     *
     * @param documentContent - the content of the uploaded document represented as a byte array
     * */
    public void upload(byte[] documentContent) {
    	
    	Session session = getCmisSession();
    	if (session == null) {
            LOGGER.error("ECM not found, Session is null.");
            return;
         }
    	
    	Document document = getDocument(session, this.name);
    	if (document != null) {
    		deleteDocument(document);
    	}
    	
    	
    	Folder root = session.getRootFolder();
        Map<String, Object> properties = getProperties(this.name);
        ContentStream contentStream = getContentStream(session, documentContent);
        
        root.createDocument(properties, contentStream, VersioningState.NONE);
    }
    
    /**
     * This method retrieves the content of a document (as a byte array) in the document service repository.
     * This method returns null if the document does not exist.
     *
     * @return the document content as a byte array
     * */
    public byte[] getAsByteArray() throws IOException{
    	
    	Session session = getCmisSession();
    	if (session == null) {
             LOGGER.error("ECM not found, Session is null.");
             return null;
         }
    	
    	Document document = getDocument(session, this.name);
    	if (document == null) {
    		return null;
    	}
    	
    	InputStream stream = document.getContentStream().getStream();
    	
    	return IOUtils.toByteArray(stream);
    } 
    
    private Document getDocument(Session session, String documentName) {
    	
    	Document document = null;
    	
    	 try {
             document = (Document) session.getObjectByPath("/" + documentName);
         } catch (ClassCastException exc) {
         	LOGGER.error("The path does not point to a Document.", exc);
         } catch (CmisObjectNotFoundException exc) {
         	LOGGER.error("The document " + documentName + " cannot be found.", exc);
         }
    	
    	return document;
    }
    
    private void deleteDocument(Document document) {
    	document.deleteAllVersions();	
    }
    
    
    /**
     * These properties contain the object type we are going to create in the repository. In this case -
     * a document with the name specified.
     *
     * @param documentName - the name of the document to be created
     * */
    private static Map<String, Object> getProperties(String documentName) {
      
    	Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, documentName);
        
        return properties;
    }

    private ContentStream getContentStream(Session session, byte[] documentContent) {
        InputStream stream = new ByteArrayInputStream(documentContent);
        
        String documentExtension = this.name.substring(this.name.lastIndexOf('.') + 1);
        String mimeType = "image/" + documentExtension;
        
        ContentStream contentStream = session.getObjectFactory().createContentStream(this.name, 
        		documentContent.length, mimeType, stream);
        
        return contentStream;
    }
    

    private static Session getCmisSession() {

    	if (cmisSession == null) {

        	try {
                InitialContext ctx = new InitialContext();
                EcmService ecmSvc = (EcmService) ctx.lookup("java:comp/env/EcmService");
                try {
                    // connect to my repository
                	cmisSession = ecmSvc.connect(UNIQUE_NAME, UNIQUE_KEY);
                } catch (CmisObjectNotFoundException e) {
                    // repository does not exist, so try to create it
                    createRepository(ecmSvc);
                    // should be created now, so connect to it
                    cmisSession = ecmSvc.connect(UNIQUE_NAME, UNIQUE_KEY);
                }
            } catch (NamingException exc) {
            	LOGGER.error("Could not find the ECM service.", exc);
            }

        }
    	
        return cmisSession;
    }

	private static void createRepository(EcmService ecmSvc) {
		RepositoryOptions options = new RepositoryOptions();
		options.setUniqueName(UNIQUE_NAME);
		options.setRepositoryKey(UNIQUE_KEY);
		options.setVisibility(com.sap.ecm.api.RepositoryOptions.Visibility.PROTECTED);
		ecmSvc.createRepository(options);
	}
}

