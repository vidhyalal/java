package com.markets.parser;

/**
 * This class invokes another class to get a web page content
 * 
 * @author Vidhya
 * @version 1.0
 */

public class MainClass {

	public static void main(String[] args) {
		WebScraper scrap = new WebScraper();
		String resultJson = "";
		resultJson = scrap.getWebPageContent();
		System.out.println("ResultJson " + resultJson);
	}
}
