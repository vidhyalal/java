package com.markets.parser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markets.model.Berries;

/**
 * This class generates a Json content for a given Webpage
 * 
 * @author Vidhya
 * @version 1.0
 */
public class WebScraper {

	public String getWebPageContent() {
		Document doc;
		String jsonResultString = "";
		Berries item = new Berries();
		WebScraper webScrap = new WebScraper();
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		List<String> resultList = new ArrayList<String>();
		List<BigDecimal> priceList = new ArrayList<BigDecimal>();
		String newUrl = "";
		final String URL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
		try {
			doc = Jsoup.connect(URL).get();

			Elements products = doc.select("div.productInfo");
			Elements productLinkList = products.select("a[href]");
			for (Element link : productLinkList) {

				/**
				 * Getting each product link
				 */
				String productLink = link.attr("href");
				String productTitle = link.text();

				item.setTitle(productTitle);

				newUrl = getLink(productLink);
				/**
				 * Parse the link to get each product details
				 */
				Document specificDoc = Jsoup.connect(newUrl).get();
				String eachProductDesc = "";
				eachProductDesc = getProductDescription(specificDoc);
				item.setDescription(eachProductDesc);
				getKcal(specificDoc, item);
				priceList = getPriceList(specificDoc, item, webScrap);

				/**
				 * Json conversion
				 */
				ObjectMapper mapper = new ObjectMapper();
				String jsonString = mapper.writeValueAsString(item);
				resultList.add(jsonString);

			}
			BigDecimal total = webScrap.getSumValue(priceList);
			total = webScrap.getRoundedValue(Double.parseDouble(total
					.toString()));
			resultMap.put("results", resultList);
			resultMap.put("total", total);
			ObjectMapper mapping = new ObjectMapper();
			jsonResultString = mapping.writeValueAsString(resultMap);

		} catch (Exception e) {
			System.out.println("Exception is " + e);
		}

		return jsonResultString;
	}

	/**
	 * This method is to get the formatted link for each product
	 * 
	 * @param productLink
	 * @return newUrl Formatted link for each product
	 */
	private String getLink(String productLink) {
		String[] output = productLink.split("/shop");
		String checkUrl = "/shop" + output[1];
		final String baseUrl = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk";
		String newUrl = "";
		if (checkUrl.startsWith("/")) {
			newUrl = baseUrl + checkUrl;
		}
		if (checkUrl.startsWith("http://")) {
			newUrl = checkUrl;
		}
		if (checkUrl.startsWith("www")) {
			newUrl = "http://" + checkUrl;
		}
		return newUrl;
	}

	/**
	 * This method is to get the product description
	 * 
	 * @param specificDoc
	 *            the url of each product
	 * @return eachProcutDesc Each product description
	 */
	public String getProductDescription(Document specificDoc) {
		Element eachProduct1 = specificDoc.select("div.productText").first() == null ? null
				: specificDoc.select("div.productText").first();
		String eachProductDesc = "";
		if (null != eachProduct1 && !"".equals(eachProduct1)) {
			eachProductDesc = eachProduct1.select("p").first().text();
		}
		Element eachProduct2 = specificDoc.select(
				"div.itemTypeGroupContainer productText").first() == null ? null
				: specificDoc.select("div.itemTypeGroupContainer productText")
						.first();
		if (null != eachProduct2 && !"".equals(eachProduct2)) {
			eachProductDesc = eachProduct2.select("p").first().text();
		}
		return eachProductDesc;
	}

	/**
	 * This method is to get the Kcal for each product
	 * 
	 * @param specificDoc
	 *            the url of each product
	 */
	private void getKcal(Document specificDoc, Berries item) {
		Element each_pdt_kcal = specificDoc.select("table.nutritionTable")
				.first() == null ? null : specificDoc.select(
				"table.nutritionTable").first();
		int kcal_per_100g = 0;
		if (null != each_pdt_kcal && !"".equals(each_pdt_kcal)) {
			Element table = specificDoc.select("table").get(0);
			Elements rows = table.select("tr");
			for (int i = 1; i < rows.size(); i++) {
				Element row = rows.get(i);
				Elements cols = row.select("td");
				Elements th = row.select("th");
				if (cols.first().text().contains("kcal")) {
					String[] kcalList = cols.first().text().split("kcal");
					kcal_per_100g = Integer.parseInt(kcalList[0]);
					item.setKcal_per_100g(kcal_per_100g);

				} else if (th.first().text().contains("kcal")) {
					String[] kcalList = cols.first().text().split("kcal");
					kcal_per_100g = Integer.parseInt(kcalList[0]);
					item.setKcal_per_100g(kcal_per_100g);
				}
			}
		}
	}

	/**
	 * This method is to get the price per unit of each product
	 * 
	 * @param specificDoc
	 *            the url of each product
	 * @return priceList List of all product prices
	 */
	private List<BigDecimal> getPriceList(Document specificDoc, Berries item,
			WebScraper webScrap) {
		List<BigDecimal> priceList = new ArrayList<BigDecimal>();
		String pricePerUnit = specificDoc.select("p.pricePerUnit").first()
				.text();
		pricePerUnit = pricePerUnit.substring(1);
		String[] price_list = pricePerUnit.split("/unit");
		double unitPrice = Double.parseDouble(price_list[0]);
		BigDecimal price = webScrap.getRoundedValue(unitPrice);
		item.setUnit_price(price);
		priceList.add(price);
		return priceList;
	}

	/**
	 * This method is to get the price rounded to 2 decimals
	 * 
	 * @param value
	 * @return roundValue price rounded to 2 decimals
	 */
	public BigDecimal getRoundedValue(double value) {
		int places = 2;
		BigDecimal roundValue = new BigDecimal(value);
		roundValue = roundValue.setScale(places, RoundingMode.HALF_UP);
		return roundValue;
	}

	/**
	 * This method is to calculate the total price
	 * 
	 * @param priceList
	 *            List of the product prices
	 * @return sum Total price
	 */
	public BigDecimal getSumValue(List<BigDecimal> priceList) {
		BigDecimal sum = new BigDecimal(0);
		for (BigDecimal price : priceList) {
			sum = sum.add(price);
		}
		return sum;
	}
}