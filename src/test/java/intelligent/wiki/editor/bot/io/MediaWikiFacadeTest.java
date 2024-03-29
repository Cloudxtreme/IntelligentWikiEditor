/*
 * Copyright (C) 2015 Myroslav Rudnytskyi
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

import intelligent.wiki.editor.bot.io.MediaWikiFacade.Language;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Class for testing {@link MediaWikiFacade} class.
 * 
 * @author Myroslav Rudnytskyi
 * @version 0.1 19.09.2015
 * @see Test
 * @see Assert
 */
@Deprecated
public class MediaWikiFacadeTest {

	@Test
	public void test() {
		String articleText = null, articleTextEmpty = null, 
				articleTextNull = null, articleTextWrong = null;
		String[] templates = null, templatesEmpty = null, 
				templatesNull = null, templatesWrong = null, 
				categories = null, categoriesEmpty = null, 
				categoriesNull = null, categoriesWrong = null;

		MediaWikiFacade.setLanguage(Language.ENGLISH);
		// yes, Ukrain right for testing
		String right = "Ukrain";
		String wrong = "Urkaine";
		try {
			articleText = MediaWikiFacade.getArticleText("Main");
			templates = MediaWikiFacade.getTemplatesStartingWith(right);
			categories = MediaWikiFacade.getCategoriesStartingWith(right);
			articleTextWrong = MediaWikiFacade.getArticleText(wrong);
			templatesWrong = MediaWikiFacade.getTemplatesStartingWith(wrong);
			categoriesWrong = MediaWikiFacade.getCategoriesStartingWith(wrong);
			try {
				articleTextNull = MediaWikiFacade.getArticleText(null);
			} catch (IllegalArgumentException e) {
				System.out.println("Expected: " + e.getMessage());
			}
			try {
				templatesNull = MediaWikiFacade.getTemplatesStartingWith(null);
			} catch (IllegalArgumentException e) {
				System.out.println("Expected: " + e.getMessage());
			}
			try {
				categoriesNull = MediaWikiFacade
						.getCategoriesStartingWith(null);
			} catch (IllegalArgumentException e) {
				System.out.println("Expected: " + e.getMessage());
			}
			try {
				articleTextEmpty = MediaWikiFacade.getArticleText("");
			} catch (IllegalArgumentException e) {
				System.out.println("Expected: " + e.getMessage());
			}
			try {
				templatesEmpty = MediaWikiFacade.getTemplatesStartingWith("");
			} catch (IllegalArgumentException e) {
				System.out.println("Expected: " + e.getMessage());
			}
			try {
				categoriesEmpty = MediaWikiFacade.getCategoriesStartingWith("");
			} catch (IllegalArgumentException e) {
				System.out.println("Expected: " + e.getMessage());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] emptyArray = new String[] {};
		Assert.assertNotNull(articleText);
		Assert.assertNotNull(templates);
		System.out.println("Found templates: " + templates.length);
		Assert.assertNotNull(categories);
		System.out.println("Found categories: " + categories.length);
		Assert.assertNull(articleTextWrong);
		Assert.assertArrayEquals(emptyArray, templatesWrong);
		Assert.assertArrayEquals(emptyArray, categoriesWrong);
		Assert.assertNull(articleTextNull);
		Assert.assertNull(templatesNull);
		Assert.assertNull(categoriesNull);
		Assert.assertNull(articleTextEmpty);
		Assert.assertNull(templatesEmpty);
		Assert.assertNull(categoriesEmpty);
	}
}