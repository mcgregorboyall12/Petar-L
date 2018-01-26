package com.jpmorgan.messageprocessor;

/**
 * Class to store all needed information for {@link Sale} adjustments.
 */
public class Adjustment {

	private String saleType;
	
	private Operation adjustmentOperation;
	
	private int adjustmentAmount;

	public Adjustment(String saleType, Operation adjustmentOperation, int adjustmentAmount) {
		
		this.saleType = saleType;
		this.adjustmentOperation = adjustmentOperation;
		this.adjustmentAmount = adjustmentAmount;
	}

	public String getSaleType() {
		return saleType;
	}

	public void setSaleType(String saleType) {
		this.saleType = saleType;
	}

	public Operation getOperation() {
		return adjustmentOperation;
	}

	public void setOperation(Operation adjustmentOperation) {
		this.adjustmentOperation = adjustmentOperation;
	}

	public int getAmount() {
		return adjustmentAmount;
	}

	public void setAmount(int adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}
	
}
