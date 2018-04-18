package com.markets.model;

import java.math.BigDecimal;

public class Berries {
	String title;
	String description;
	int kcal_per_100g;
	BigDecimal unit_price;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getKcal_per_100g() {
		return kcal_per_100g;
	}
	public void setKcal_per_100g(int kcal_per_100g) {
		this.kcal_per_100g = kcal_per_100g;
	}
	public BigDecimal getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(BigDecimal unit_price) {
		this.unit_price = unit_price;
	}
	
	
	
}
