package com.jpmorgan.messageprocessor;

public class Sale {
	
	private String saleType;
	
	private int salePrice;

	public Sale(String saleType, int salePrice) {

		this.saleType = saleType;
		this.salePrice = salePrice;
	}

	public String getType() {
		return saleType;
	}

	public void setType(String saleType) {
		this.saleType = saleType;
	}

	public int getPrice() {
		return salePrice;
	}

	public void setPrice(int salePrice) {
		this.salePrice = salePrice;
	}
	
}
