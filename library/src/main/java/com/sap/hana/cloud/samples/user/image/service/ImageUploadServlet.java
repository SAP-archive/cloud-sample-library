package com.sap.hana.cloud.samples.user.image.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.util.IOUtils;
import com.sap.hana.cloud.samples.adapters.DocumentAdapter;
import com.sap.hana.cloud.samples.adapters.IdentityAdapter;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;
import com.sap.hana.cloud.samples.util.AlertsUtil;


/**
 * This servlet is called when the user uploads a new image. It validates the uploaded file.
 * If the file is valid, it is saved into the document service repository. 
 * If the file is not valid, the user is notified with an alert.
 * 
 * This servlet can be called by all users.
 * */
@WebServlet(name="ImageUploadServlet",
urlPatterns={"/restricted/everyone/ImageUploadServlet"})
@MultipartConfig
public class ImageUploadServlet extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUploadServlet.class);

	private static final long serialVersionUID = 1;

	private final static int DEFAULT_MAX_SIZE = 1000 * 1024;
	private static final String HEADER_OF_BMP_FILE = "424d";
	private static final String HEADER_OF_JPEG_FILE = "ffffffffffffffd8ffffffffffffffe0";
	private static final String HEADER_OF_GIF_FILE = "47494638";
	private static final String HEADER_OF_PNG_FILE = "ffffff89504e47";
	private static final Set<String> ALLOWED_IMAGE_HEADERS = new HashSet<String>();
	static {
		Collections.addAll(ALLOWED_IMAGE_HEADERS, HEADER_OF_GIF_FILE, HEADER_OF_JPEG_FILE, HEADER_OF_PNG_FILE);
	}

 	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Part part = request.getPart("imageUploader");
		if (part == null) {
			String error = "Could not retrieve the uploaded content! Part is null.";
			LOGGER.error(error);
			throw new RuntimeException(error);
		}

		InputStream partInputStream = part.getInputStream();

		byte[] imageContent = IOUtils.toByteArray(partInputStream);

		String fileName = getFileName(part);

		if (fileName == null) {
			String error = "Could not retrieve the name of the uploaded file! File name is null.";
			LOGGER.error(error);
			throw new RuntimeException(error);
		}

		if (imageContent.length > DEFAULT_MAX_SIZE) {
			String message = "Uploaded file is too large! Limit is 1 MB.";
			LOGGER.debug(message);
			AlertsUtil.alert(request.getSession(), response, message);
			return;
		}

		if (!isImage(imageContent)) {
			String message = "Uploaded file is not an image from the allowed range! (allowed file types: gif, png, jpg, bmp)";
			LOGGER.debug(message);
			AlertsUtil.alert(request.getSession(), response, message);
			return;
		}

		String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
		String currentUserId = IdentityAdapter.getLoggedUserId(request);
		String newFileName = currentUserId + "." + fileExtension;

		EntityManager em = PersistenceAdapter.getEntityManager();
		LibraryUser userFromDatabase = (LibraryUser) em.createNamedQuery("getUserById").setParameter("userId", currentUserId).getSingleResult();
		
		DocumentAdapter adapter = new DocumentAdapter(newFileName);
		adapter.upload(imageContent);
		userFromDatabase.setImgSrc("restricted/everyone/VisualizeImageServlet" + "?filename=" + newFileName);

		em.getTransaction().begin();
		em.merge(userFromDatabase);
		em.getTransaction().commit();

		part.delete();

	}

	private String getFileName(Part part) {
        String[] components = part.getHeader("content-disposition").split(";");
        for (String singleComponent : components) {
               if (!singleComponent.contains("filename")) {
                     continue;
               }

               return singleComponent.substring(singleComponent.indexOf("\"") + 1,
                            singleComponent.lastIndexOf("\""));
        }
        return null;
	}

	private static boolean isImage(byte[] fileContent) {
        
		boolean result = false;
		
        int neededHeaderBytes = 4;
        
        if (fileContent.length < neededHeaderBytes) {
        	return false;
        }
        
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < neededHeaderBytes; i++) {
            header.append(Integer.toHexString(fileContent[i]));
        }

        if (ALLOWED_IMAGE_HEADERS.contains(header.toString()) || header.toString().startsWith(HEADER_OF_BMP_FILE)) {
            result = true;
        }

        return result;
    }

}
