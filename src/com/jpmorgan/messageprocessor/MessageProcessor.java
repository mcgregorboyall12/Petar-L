package com.jpmorgan.messageprocessor;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The main class to process incoming messages and manipulate the data structures.
 */
public class MessageProcessor {
	
	/**	Message number counter */
	int counter;
	
	/** A variable to state whether the processor is still enabled and can process messages */
	boolean terminated;
	
	/** A map from {@link Sale} type to all the {@link Adjustment}s done to it. Used for displaying history of sale adjustments by type */
	private Map<String, List<Adjustment>> typeToAdjustments;

	/** A map from {@link Sale} type to all the {@link Sale}s of that type. Used for displaying sale summary by type */
	private Map<String, List<Sale>> typeToSales;

	/** Creates a new instance of {@link MessageProcessor} */
	public MessageProcessor() {
		
		this.counter = 0;

		this.typeToAdjustments = new HashMap<>();
		this.typeToSales = new HashMap<>();
	}

	/**
	 * Adds a new {@link Sale}
	 * @param saleType the sale type
	 * @param salePrice the price of that sale
	 */
	private void addSale(String saleType, int salePrice) {
		assert saleType != null;
		
		Sale sale = new Sale(saleType, salePrice);
				
		if (typeToSales.containsKey(saleType)) {
			typeToSales.get(saleType).add(sale);
		} else {
			typeToSales.put(saleType, new LinkedList<>(Arrays.asList(sale)));
		}
	}
	
	/**
	 * Adds an {@link Adjustment}
	 * @param saleType the sale type
	 * @param adjustmentOperation the operation to be performed
	 * @param adjustmentAmount the number used for the adjustment operation
	 */
	private void addAdjustment(String saleType, Operation adjustmentOperation, int adjustmentAmount) {
		assert saleType != null;

		Adjustment adjustment = new Adjustment(saleType, adjustmentOperation, adjustmentAmount);
		
		if (typeToAdjustments.containsKey(saleType)) {
			typeToAdjustments.get(saleType).add(adjustment);
		} else {
			typeToAdjustments.put(saleType, new LinkedList<>(Arrays.asList(adjustment)));
		}

		if (typeToSales.containsKey(saleType)) {
			for (Sale sale : typeToSales.get(saleType)) {
				// For all sales of the given type, modify their price to reflect the needed adjustment
				sale.setPrice(adjustmentOperation.calculate(sale.getPrice(), adjustmentAmount));
			}	
		}
	}
	
	/**
	 * Processes a message that gives the details of a single {@link Sale}
	 * @param type the sale type
	 * @param price the price of that sale
	 */
	public void processSingleSale(String saleType, int salePrice) {
		assert saleType != null;

		if (terminated) {
			return;
		}

		addSale(saleType, salePrice);

		registerMessageProcessed();
	}
	
	/**
	 * Processes a message that gives details of multiple {@link Sale}s
	 * @param saleType the sale type
	 * @param salePrice the price of a single sale
	 * @param numberOfSales the number of sales made
	 */
	public void processMultipleSales(String saleType, int salePrice, int numberOfSales) {
		assert saleType != null;

		if (numberOfSales < 0) {
			throw new RuntimeException("There can not be a negative number of sales made");
		}
		
		if (terminated) {
			return;
		}

		for (int i = 0; i < numberOfSales; i++) {
			addSale(saleType, salePrice);
		}

		registerMessageProcessed();
	}
	
	/**
	 * Processes a message that gives the details of a single {@link Sale} and the details of a single {@link Adjustment} to be applied to all sales up till now
	 * @param saleType the sale type
	 * @param salePrice the price of that sale
	 * @param adjustmentOperation the operation to be performed
	 * @param adjustmentAmount the number used for the adjustment operation
	 */
	public void processSingleSaleAndPriceAdjustment(String saleType, int salePrice, String adjustmentSaleType, Operation adjustmentOperation, int adjustmentAmount) {
		assert saleType != null;
		assert adjustmentSaleType != null;

		if (terminated) {
			return;
		}
		
		addSale(saleType, salePrice);
		
		addAdjustment(adjustmentSaleType, adjustmentOperation, adjustmentAmount);
		
		registerMessageProcessed();
	}
	
	public int getTotalValue(String saleType) {
		if (typeToSales.containsKey(saleType)) {
			return typeToSales.get(saleType).stream().mapToInt(Sale::getPrice).sum();
		}
		return 0;
	}
	
	/**
	 * A call to hold all operations common to all messages. To be called once by every message type.
	 * 
	 * The call checks whether it needs to output a log of some kind, specifying what has been done to this moment.
	 * Also it currently holds the logic for stopping the program based on the given condition.
	 */
	private void registerMessageProcessed() {
		counter++;
		
		// If it's the 10th, 20th, 30th, 40th or 50th message...
		if (counter%10 == 0) {
			
			// ...give a summary of all sales by type and the overall total value paid.
			System.out.println("Sale summary:");
			
			// For every sale type...
			for (String saleType : typeToSales.keySet()) {
				
				// ...calculate the overall value paid for all sales of this type and print the result
				System.out.println(MessageFormat.format("    Sale type: {0}; Total value: {1, number, currency}", saleType, getTotalValue(saleType) / 100.0));
			}
			
			// If it's the 50th message...
			if (counter%50 == 0) {
				
				// ...print the adjustment summary.
				System.out.println("Adjustment summary:");

				// For every sale type...
				for (String saleType : typeToAdjustments.keySet()) {
					
					System.out.println(MessageFormat.format("    Sale type: {0}", saleType));
					
					for (Adjustment adjustment : typeToAdjustments.get(saleType)) {
						// ...print the sequence of adjustments made to it.
						System.out.println(MessageFormat.format("        Operation: {0}; Anount: {1}", adjustment.getOperation(), adjustment.getAmount()));
					}
				}
				
				// Final action is to warn the system will accept no more messages...
				System.out.println("Limit of 50 messages reached. System will now terminate.");

				// ...and terminate.
				this.terminated = true;
			}
		}
	}
}
