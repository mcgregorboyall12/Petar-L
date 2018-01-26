package com.jpmorgan.messageprocessor.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.jpmorgan.messageprocessor.MessageProcessor;
import com.jpmorgan.messageprocessor.Operation;

class MessageProcessorTest {

	@Test
	void processSingleSale() {

		MessageProcessor processor = new MessageProcessor();
		
		processor.processSingleSale("apple", 10);
		processor.processSingleSale("apple", 2);
		processor.processSingleSale("banana", -20);
		processor.processSingleSale("banana", 5);
		processor.processSingleSale("cucumber", 30);
		processor.processSingleSale("cucumber", -67);

		assertEquals(12, processor.getTotalValue("apple"));
		assertEquals(-15, processor.getTotalValue("banana"));
		assertEquals(-37, processor.getTotalValue("cucumber"));
		assertEquals(0, processor.getTotalValue("dandelion"));
	}

	@Test
	void processMultipleSales() {

		MessageProcessor processor = new MessageProcessor();
		
		processor.processMultipleSales("apple", 10, 5);
		processor.processMultipleSales("apple", 15, 2);
		processor.processMultipleSales("banana", 20, 0);
		processor.processMultipleSales("banana", 25, 1);
		processor.processMultipleSales("cucumber", -30, 5);
		processor.processMultipleSales("cucumber", 35, 3);

		assertEquals(80, processor.getTotalValue("apple"));
		assertEquals(25, processor.getTotalValue("banana"));
		assertEquals(-45, processor.getTotalValue("cucumber"));
		assertEquals(0, processor.getTotalValue("dandelion"));
		
		assertThrows(RuntimeException.class, () -> {processor.processMultipleSales("dandelion", 1, -1);});

	}

	@Test
	void makeSaleAdjustments() {

		MessageProcessor processor = new MessageProcessor();

		processor.processSingleSale("apple", 10);
		processor.processSingleSale("apple", 2);
		processor.processSingleSale("banana", -20);
		processor.processSingleSale("banana", 5);
		processor.processSingleSale("cucumber", 30);
		processor.processSingleSale("cucumber", 0);
		processor.processSingleSale("cucumber", -67);

		// Make sure the sale of the cucumber also gets modified by the adjustment
		processor.processSingleSaleAndPriceAdjustment("cucumber", 3, "cucumber", Operation.MULTIPLY, 10);

		processor.processMultipleSales("banana", 12, 5);
		processor.processSingleSale("cucumber", 50);

		// Make sure multiple sales in a single message are properly adjusted as well
		processor.processSingleSaleAndPriceAdjustment("cucumber", 3, "banana", Operation.SUBTRACT, 11);

		assertEquals(12, processor.getTotalValue("apple"));
		assertEquals(-32, processor.getTotalValue("banana"));
		assertEquals(-287, processor.getTotalValue("cucumber"));
		assertEquals(0, processor.getTotalValue("dandelion"));

	}
	
	@Test
	void terminateProcessor() {
		
		MessageProcessor processor = new MessageProcessor();

		// Send 500 messages, assume only the first 50 passed through
		for (int i = 0; i < 100; i++) {
			processor.processSingleSale("apple", 1);
			processor.processMultipleSales("banana", 1, 5);
			processor.processSingleSaleAndPriceAdjustment("cucumber", 3, "dandelion", Operation.MULTIPLY, 1);;
			processor.processMultipleSales("eclair", 100, 0);
			processor.processSingleSale("fondue", 2);
		}
		
		// Make sure that price adjustments for apples do not get processed after the initial 50
		processor.processSingleSaleAndPriceAdjustment("cucumber", 3, "apple", Operation.MULTIPLY, 100);;

		
		assertEquals(10, processor.getTotalValue("apple"));
		assertEquals(50, processor.getTotalValue("banana"));
		assertEquals(30, processor.getTotalValue("cucumber"));
		assertEquals(0, processor.getTotalValue("eclair"));
		assertEquals(20, processor.getTotalValue("fondue"));
	}
}
