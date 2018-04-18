package com.markets.parser;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainClass {

	public static void main(String[] args) {
		WebScraper scrap = new WebScraper();
		Map<String, Object> resultJson = new LinkedHashMap<String, Object>();
		resultJson = scrap.getWebPageContent();
		System.out.println("ResultJson " + resultJson);
	}
}
