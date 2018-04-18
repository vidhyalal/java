package com.markets.parser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markets.model.Berries;

public class WebScraper {

	public static void main(String[] args) {
		Document doc;
		Berries item = new Berries();
		WebScraper ws=new WebScraper();
		Map<String,Object> resultMap=new HashMap<String,Object>();
		List<String> resultList=new ArrayList<String>();
		List<BigDecimal> priceList=new ArrayList<BigDecimal>();
		try {
			doc = Jsoup
					.connect(
							"https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html")
					.get();

			Elements products = doc.select("div.productInfo");
			Elements product_link_list = products.select("a[href]");
			for (Element link : product_link_list) {

				// get the value from href attribute
				String product_link = link.attr("href");
				String product_title = link.text();

					System.out.println("\n link : " + product_link);
					System.out.println("title : " + product_title);
					item.setTitle(product_title);
					
					String[] output=product_link.split("/shop");
					System.out.println("2nd string"+"/shop"+output[1]);
					String checkUrl="/shop"+output[1];
					final String base_url="https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk";
					String newUrl = "";
							if (checkUrl.startsWith("/"))
							{
							    newUrl = base_url + checkUrl;
							    System.out.println("URL is "+newUrl);
							}
							if (checkUrl.startsWith("http://"))
							{
							    newUrl = checkUrl;
							}
							if (checkUrl.startsWith("www"))
							{						    
								newUrl = "http://" + checkUrl;
							}
				// Follow each product link to get its details
				Document specificDoc = Jsoup.connect(newUrl).get();
				
				//Each product description
				Element each_product1 = specificDoc.select("div.productText").first()==null?null:specificDoc.select("div.productText").first();
				String each_product_desc = "";
				if(null!=each_product1 && !"".equals(each_product1))
				{
				each_product_desc = each_product1.select("p").first().text();
				System.out.println("desc : " + each_product_desc);
				}	
				Element each_product2 = specificDoc.select("div.itemTypeGroupContainer productText").first()==null?null:specificDoc.select("div.itemTypeGroupContainer productText").first();
				if(null!=each_product2 && !"".equals(each_product2))
				{
				each_product_desc = each_product2.select("p").first().text();
				System.out.println("desc : " + each_product_desc);
				}
				item.setDescription(each_product_desc);
				
				//Each product kcal
				Element each_pdt_kcal = specificDoc.select("table.nutritionTable").first()==null?null:specificDoc.select("table.nutritionTable").first();
				int kcal_per_100g=0;
				if(null!=each_pdt_kcal && !"".equals(each_pdt_kcal))
				{
					 Element table = specificDoc.select("table").get(0); 
					    Elements rows = table.select("tr");
					    for (int i = 1; i < rows.size(); i++) { 
					    	Element row = rows.get(i);
					        Elements cols = row.select("td");
					        Elements th = row.select("th");
					        if(cols.first().text().contains("kcal"))
					        {
					        	String[] kcalList=cols.first().text().split("kcal");
					        	System.out.println("kcal is "+kcalList[0]);
					        	kcal_per_100g=Integer.parseInt(kcalList[0]);
					        	item.setKcal_per_100g(kcal_per_100g);
					        	
					        }
					        else if(th.first().text().contains("kcal"))
					        {
					        	String[] kcalList=cols.first().text().split("kcal");
					        	System.out.println("kcal is "+kcalList[0]);
					        	kcal_per_100g=Integer.parseInt(kcalList[0]);
					        	item.setKcal_per_100g(kcal_per_100g);
					        }
					    }
				}	
				
				//Each product price
				String price_per_unit = specificDoc.select("p.pricePerUnit").first().text();
				price_per_unit=price_per_unit.substring(1);
				String[] price_list=price_per_unit.split("/unit");
				System.out.println("Price per unit "+price_list[0]);
				double unit_price=Double.parseDouble(price_list[0]);
				BigDecimal price=ws.getRoundedValue(unit_price);
				item.setUnit_price(price);
				priceList.add(price);
				
				//Json conversion
				ObjectMapper mapper = new ObjectMapper();
				String jsonString = mapper.writeValueAsString(item) ;
				System.out.println(jsonString);
				resultList.add(jsonString);
				
		} 
			BigDecimal total=ws.getSumValue(priceList);
			total=ws.getRoundedValue(Double.parseDouble(total.toString()));
			resultMap.put("results",resultList);
			resultMap.put("total",total);
			System.out.println("ResultJson "+resultMap);
		}
		catch (Exception e) {
			System.out.println("Exception is " + e);
		}
	}
	
	
	public BigDecimal getRoundedValue(double value) {
		int places = 2;
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd;
	}

	public BigDecimal getSumValue(List<BigDecimal> priceList) {
		BigDecimal sum = new BigDecimal(0);
		for (BigDecimal d : priceList) {
			sum = sum.add(d);
		}
		return sum;
	}

}
