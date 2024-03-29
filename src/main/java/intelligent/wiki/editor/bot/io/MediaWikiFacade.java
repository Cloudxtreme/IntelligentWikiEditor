/*
 * Copyright (C) 2014 Myroslav Rudnytskyi
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */
package intelligent.wiki.editor.bot.io;

import intelligent.wiki.editor.bot.io.wiki.templatedata.TemplateParameter;
import intelligent.wiki.editor.bot.io.wiki.templatedata.TemplateParameterBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Class, created to get access to data, stored on <a href=http://https://www.mediawiki.org/wiki/API:Main_page>MediaWiki</a>
 * servers, such as Wikipedia articles, templates, categories and so on. Note,
 * that all it's methods can throw {@link IOException}.
 * 
 * @author Myroslav Rudnytskyi
 * @version 19.09.2015
 * @deprecated singleton, use {@link intelligent.wiki.editor.bot.io.wiki.WikiFacade} instead
 */
@Deprecated
public final class MediaWikiFacade {

	private static final String QUERY_LINK = "?action=query";
	// https is necessary!
	private static String WIKIPEDIA_API = "https://uk.wikipedia.org/w/api.php";
	private static Language lang = Language.UKRAINIAN;

	/**
	 * do not instantiate this class
	 */
	private MediaWikiFacade() {
	}

	public static Language getLanguage() {
		return MediaWikiFacade.lang;
	}

	/**
	 * Sets specified language. Note, that Ukrainian is used by default.
	 *
	 * @param lang
	 *            language to set
	 */
	public static void setLanguage(Language lang) {
		MediaWikiFacade.lang = Objects.requireNonNull(lang, "Language null!");
		// https is necessary!
		MediaWikiFacade.WIKIPEDIA_API =
				String.join("", "https://", lang.getCode(),
						".wikipedia.org/w/api.php");
	}

	/**
	 * Checks if {@link String} arguments are not null or empty. Note, that
	 * every method in this class <strong>must</strong> make this check,
	 * otherwise method results can be unexpected ("null" is correct String
	 * value) or even harmful for Web servers ("" stands for all pages).
	 *
	 * @param args
	 *            parameters to check
	 */
	private static void checkArguments(String... args) {
		for (String s : args) {
			if ((s == null) || s.isEmpty()) {
				throw new IllegalArgumentException(
						"Parameter can not be null or empty!");
			}
		}
	}

	private static String normalize(String arg) {
		return arg.replace(' ', '_');
	}

	/**
	 * Gets plain wiki text of specified article. Note, that article name
	 * depends on chosen language. See {@link #setLanguage(Language)
	 * setLanguage()} method for details.
	 *
	 * @param articleName
	 *            full article name in chosen language
	 * @return wiki text in {@link String} representation
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static String getArticleText(String articleName) throws IOException {
		MediaWikiFacade.checkArguments(articleName);
		String text = null;
		URL url =
				new URL(String.join("", MediaWikiFacade.WIKIPEDIA_API,
						MediaWikiFacade.QUERY_LINK,
						"&format=xml&prop=revisions&titles=",
						MediaWikiFacade.normalize(articleName),
						"&rvprop=content"));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			Document doc = factory.newDocumentBuilder().parse(url.openStream());
			XPath xpath = XPathFactory.newInstance().newXPath();
			String allPagesPath = "/api/query/pages/page/revisions/rev";
			Node result =
					(Node) xpath.compile(allPagesPath).evaluate(doc,
							XPathConstants.NODE);
			if (result != null) {
				text = result.getFirstChild().getNodeValue();
			}
		} catch (ParserConfigurationException | XPathExpressionException
				| SAXException e) {
			// can not catch smth, but if yes, rethrow as checked exception
			throw new RuntimeException(e);
		}
		return text;
	}

	public static List<String> getAriclesStartingWith(String preffix) throws IOException {
		// TODO write with limit (10). Now it is hardcoded
		return getPagesStartingWith(preffix, null, WikiNamespace.ARTICLE);
	}

	public static boolean existArticle(String name) throws IOException {
		return getArticleText(name) != null;
	}

	/**
	 * Gets list of all pages from specified namespace, starting with specified
	 * preffix.
	 *
	 * @param preffix      preffix
	 * @param continueFrom page name, on which query was stopped, if result has more than
	 *                     500 occurrences
	 * @param namespace    {@link WikiNamespace namespace} object
	 * @return list of pages, starting with specified prefix
	 * @throws IOException if an I/O error occurs
	 */
	private static List<String> getPagesStartingWith(String preffix,
													 String continueFrom, WikiNamespace namespace) throws IOException {

		MediaWikiFacade.checkArguments(preffix);
		List<String> pages = new ArrayList<>();
		URL url =
				new URL(String.join("", MediaWikiFacade.WIKIPEDIA_API,
						MediaWikiFacade.QUERY_LINK,
						"&list=allpages&format=xml&apprefix=",
						MediaWikiFacade.normalize(preffix), "&apnamespace=",
						namespace.toString(), "&aplimit=10&apfrom=",    //500
						continueFrom != null ? continueFrom : ""));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			Document doc = factory.newDocumentBuilder().parse(url.openStream());
			XPath xpath = XPathFactory.newInstance().newXPath();
			String allPagesPath = "/api/query/allpages";
			Node allPages =
					(Node) xpath.compile(allPagesPath).evaluate(doc,
							XPathConstants.NODE);
			for (int i = 0; i < allPages.getChildNodes().getLength(); i++) {
				pages.add(allPages.getChildNodes().item(i).getAttributes()
						.getNamedItem("title").getNodeValue());
			}

			String continueAllPagesPath = "/api/query-continue/allpages";
			Node continueAllPages =
					(Node) xpath.compile(continueAllPagesPath).evaluate(doc,
							XPathConstants.NODE);
			if (continueAllPages != null) {
				String nextContinueFrom =
						continueAllPages.getAttributes()
								.getNamedItem("apcontinue").getNodeValue();
				pages.addAll(MediaWikiFacade.getPagesStartingWith(preffix,
						nextContinueFrom, namespace));
			}
		} catch (ParserConfigurationException | XPathExpressionException
				| SAXException e) {
			// can not catch smth, but if yes, rethrow as checked exception
			throw new RuntimeException(e);
		}
		return pages;
	}

	/**
	 * Gets array of templates names, starting with specified preffix.
	 *
	 * @param preffix
	 *            preffix
	 * @return array of pages, starting with specified prefix
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static String[] getTemplatesStartingWith(String preffix)
			throws IOException {

		List<String> result =
				MediaWikiFacade.getPagesStartingWith(preffix, null,
						WikiNamespace.TEMPLATE);
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Gets array of categories names, starting with specified prefix.
	 *
	 * @param preffix preffix
	 * @return list of pages, starting with specified prefix
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static String[] getCategoriesStartingWith(String preffix)
			throws IOException {

		List<String> result =
				MediaWikiFacade.getPagesStartingWith(preffix, null,
						WikiNamespace.CATEGORY);
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Gets array of article names, starting with specified prefix.
	 *
	 * @param preffix  preffix
	 * @return list of pages, starting with specified prefix
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static String[] getArticlesStartingWith(String preffix)
			throws IOException {

		List<String> result =
				MediaWikiFacade.getPagesStartingWith(preffix, null,
						WikiNamespace.ARTICLE);
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Gets array of template parameters, using it's name.
	 *
	 * @param templateName template name
	 * @return array of template parameters
	 * @throws IOException if an I/O error occurs
	 */
	public static TemplateParameter[] getTemplateParameters(String templateName)
			throws IOException {

		MediaWikiFacade.checkArguments(templateName);
		URL url =
				new URL(
						String.join("", MediaWikiFacade.WIKIPEDIA_API,
								"?action=templatedata&titles=",
								MediaWikiFacade.lang.getTemplatePreffix(),
								MediaWikiFacade.normalize(templateName),
								"&format=json"));
		TemplateParameter[] parameters = new TemplateParameter[]{};
		try (InputStream is = url.openStream()) {
			JsonReader reader = Json.createReader(is);
			JsonObject pages = reader.readObject().getJsonObject("pages");
			if (pages.size() > 0) {
				JsonObject firstChild =
						(JsonObject) pages.entrySet().iterator().next()
								.getValue();
				JsonObject params = firstChild.getJsonObject("params");
				parameters = new TemplateParameter[params.size()];
				int i = 0;
				for (Entry<String, JsonValue> entry : params.entrySet()) {
					JsonObject object = (JsonObject) entry.getValue();
					String description =
							object.get("description").getValueType() == ValueType.OBJECT ? object
									.getJsonObject("description").getString(
											"uk") : "null";
					parameters[i] = TemplateParameterBuilder.parameterWithName(entry.getKey())
							.withDescription(description)
							.withDefaultValue(object.get("default").toString())
							.withType(object.getString("type"))
							.withRequiredFlag(object.getBoolean("required"))
							.withSuggestedFlag(object.getBoolean("suggested"))
							.withDeprecatedFlag(object.getBoolean("deprecated"))
							.build();
					i++;
				}
			}
		}
		return parameters;
	}

	/**
	 * Enumeration, containing language codes, such as "uk" stands for
	 * Ukrainian, "en" for English and so on.
	 *
	 * @author Mir4ik
	 * @version 0.1 27.10.2014
	 */
	@Deprecated
	public enum Language {
		UKRAINIAN("uk", "Файл:", "Категорія:", "Шаблон:"), ENGLISH("en",
				"File:", "Category:", "Template:");

		private final String code;

		private final String filePreffix;

		private final String categoryPreffix;

		private final String templatePreffix;

		Language(String code, String file, String category,
				 String template) {
			this.code = code;
			filePreffix = file;
			categoryPreffix = category;
			templatePreffix = template;
		}

		public String getCode() {
			return code;
		}

		public String getFilePreffix() {
			return filePreffix;
		}

		public String getCategoryPreffix() {
			return categoryPreffix;
		}

		public String getTemplatePreffix() {
			return templatePreffix;
		}
	}

	/**
	 * Enumeration, containing Mediawiki namespace constants, such as "0" stands
	 * for Main or article namespace, "10" for templates namespace (starting
	 * with "Template:" preffix).
	 *
	 * @author Myroslav Rudnytskyi
	 * @version 0.1 23.04.2015
	 * @deprecated use {@link intelligent.wiki.editor.bot.io.wiki.WikiNamespace} instead
	 */
	@Deprecated
	private enum WikiNamespace {
		ARTICLE(0), MAIN(0), FILE(6), TEMPLATE(10), CATEGORY(14);

		private final int code;

		WikiNamespace(int code) {
			this.code = code;
		}

		/**
		 * Returns namespace constants in {@link String} representation.
		 *
		 * @return namespace constant for this enum constant
		 */
		@Override
		public String toString() {
			return String.valueOf(code);
		}
	}
}