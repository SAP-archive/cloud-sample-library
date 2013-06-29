package com.sap.hana.cloud.samples.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This utility class is used for extracting a certain book's details from a Web service, provided by Open Library.
 * The ISBN number is used as an ID. This property is not mandatory when creating a book, so it is possible to have
 * a book without the 'View Details' option available in the UI.
 * <p>
 * Regarding ISBN: Details are extracted only from Open Library and, in order the book's details to be extracted, its ISBN should be defined.
 * */
public class BookDetailsUtil {

	private String isbn;
	private JsonObject details;

	// this Map defines the relation of the keys of properties we are interested in
	// and strings that are going to be displayed in the user interface
	private static final Map<String, String> map = new HashMap<String, String>();
	static{
		map.put("title", "Title");
		map.put("authors", "Authors");
		map.put("publishers", "Publishers");
		map.put("publish_date", "Publish Date");
		map.put("publish_places", "Published in");
		map.put("number_of_pages", "Number of pages");
	}

	/**
	 * This constructor creates an {@link com.sap.hana.cloud.samples.util.BookDetailsUtil BookDetailsUtil} instance for a book corresponding to the specified ISBN.
	 *
	 * @param isbn - ISBN of the book.
	 * @param detailsFromOpenLibraryWebService - the response of the Open Library's Web service, parsed as
	 * {@link com.google.gson.JsonElement JsonElement}.
	 * */
	public BookDetailsUtil (String isbn, JsonElement detailsFromOpenLibraryWebService) {
		this.isbn = isbn;
		this.details = detailsFromOpenLibraryWebService.getAsJsonObject();
	}

	/**
	 * This method extracts some of the book's properties. The case when a particular property
	 * does not exist is also covered.
	 *
	 * @return {@link com.google.gson.JsonElement JsonElement} that is displayed into the UI.
	 * */
	public JsonElement extractOnlyNeededData() {

		JsonObject result = new JsonObject();

		JsonArray pairs = new JsonArray();
		pairs.add(generateArrayElement("ISBN", isbn));

		if (isEmptyJson(details)) {
			// the Open Library's REST returns '{}' if a book
			// with the given ISBN was not found
			result.add("pairs", pairs);
			return result;
		}

		String coverUrl = getBookCoverAttribute();
		if (coverUrl != null){
			result.addProperty("imageLink", coverUrl);
		}

		String bookUrl = getProperty("url");
		if (bookUrl != null) {
			result.addProperty("openLibraryUrl", bookUrl);
		}

		for (Entry<String, String> singleEntry : map.entrySet()) {

			String value = getProperty(singleEntry.getKey());
			if (value != null) {
				pairs.add(generateArrayElement(singleEntry.getValue(), value));
			}
		}

		result.add("pairs", pairs);

		return result;
	}



	private String getBookCoverAttribute(){

		JsonElement imageLink = null;
		JsonElement cover = details.get("ISBN:"+isbn).getAsJsonObject().get("cover");
		if (cover != null) {
			imageLink = cover.getAsJsonObject().get("medium");
			return imageLink.getAsString();
		}

		return null;
	}

	private String getProperty(String propertyKey) {

		JsonElement propertyElement = details.get("ISBN:"+isbn).getAsJsonObject().get(propertyKey);

		if (propertyElement == null){
			return null;
		}

		if (propertyElement.isJsonPrimitive()) {
			return propertyElement.getAsString();
		}

		if (propertyElement.isJsonArray()) {
			return arrayToString(propertyElement.getAsJsonArray());
		}

		return null;
	}


	private JsonElement generateArrayElement(String key, String value) {

		JsonObject result = new JsonObject();
		result.addProperty("key", key);
		result.addProperty("value", value);

		return result;
	}

	private String arrayToString(JsonArray array) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < array.size(); i++) {
			builder.append(array.get(i).getAsJsonObject().get("name").getAsString());
			if ((array.size() - i) > 1) {
				builder.append(", ");
			}
		}

		return builder.toString();
	}

	private boolean isEmptyJson(JsonElement element) {

		JsonElement emptyJson = new JsonParser().parse("{}");

		return element.equals(emptyJson);
	}
}
