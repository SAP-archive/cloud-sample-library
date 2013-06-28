package com.sap.hana.cloud.samples.book.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sap.hana.cloud.samples.adapters.ConnectivityAdapter;
import com.sap.hana.cloud.samples.util.BookDetailsUtil;

/**
 * This class uses the connectivity service to extract book details from Open Library.
 * 
 * If details are not available or an error occurs, it returns the response code that has been received from the OpenLibrary REST API.
 * If the OpenLibrary destintion is missing the response code is 400.
 * 
 * This servlet can be called by all users. 
 * */
@WebServlet(name="ExtractBookDetailsServlet",
urlPatterns={"/restricted/everyone/ExtractBookDetailsServlet"})
public class ExtractBookDetailsServlet extends HttpServlet {

	private static final long serialVersionUID = 1;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");

		String isbn = request.getParameter("isbn").trim();
		HttpClient httpClient = ConnectivityAdapter.getHttpClient();
		if (httpClient == null) {
			response.setStatus(400);
			return;
		}

		HttpGet getRequest = new HttpGet("api/books?bibkeys=ISBN:" + isbn + "&jscmd=data&format=json");
		HttpResponse detailsResponse = httpClient.execute(getRequest);

		JsonElement result;
		try{
			int statusCode = detailsResponse.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				response.setStatus(statusCode);
				return;
			}

			BookDetailsUtil util = new BookDetailsUtil(isbn, extractJsonElement(detailsResponse));
			result = util.extractOnlyNeededData();

		} finally {
			HttpEntity entity = detailsResponse.getEntity();
			if (entity.isStreaming()) {
	            InputStream instream = entity.getContent();
	            if (instream != null) {
	                instream.close();
	            }
	        }
		}


		response.getWriter().print(result.toString());

	}

	private JsonElement extractJsonElement(HttpResponse response) throws IOException {

		HttpEntity entity = response.getEntity();

		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
		try {
			return (new JsonParser()).parse(reader);
		} finally {
			reader.close();
		}
	}

}
