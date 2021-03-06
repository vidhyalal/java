package com.markets.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.markets.parser.WebScraper;

@RunWith(MockitoJUnitRunner.class)
public class BerriesTest {
	@Mock
	WebScraper ws;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test_scrap() throws IOException {
		BigDecimal expected = new BigDecimal("4.72");
		double value = 4.72222;
		Mockito.when(ws.getRoundedValue(value)).thenReturn(expected);
	}

	/**
	 * Test the connection to a valid url
	 */
	@Test
	public void test_conn() throws IOException {
		final String URL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
		Connection con = Jsoup.connect(URL);
		Document doc = con.get();
		assertTrue(doc.title().contains("Sainsbury"));

	}

	/**
	 * For an image link,below method checks the connection
	 */
	@Test
	public void test_type() throws IOException {
		final String URL = "https://s2.googleusercontent.com/s2/favicons?domain=http://facebook.com/";
		Connection con = Jsoup.connect(URL).ignoreContentType(true);
		Document doc = con.get();
		assertEquals(doc.title(), "");
	}

}
