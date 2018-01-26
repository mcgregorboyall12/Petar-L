package com.jpmorgan.messageprocessor.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.jpmorgan.messageprocessor.Operation;

class OperationTest {

	@Test
	void add() {
		assertEquals(7, Operation.ADD.calculate(2, 5));

		assertEquals(0, Operation.ADD.calculate(-3, 3));

		assertEquals(-55, Operation.ADD.calculate(-10, -45));
	}

	@Test
	void subtract() {
		assertEquals(-3, Operation.SUBTRACT.calculate(2, 5));

		assertEquals(-6, Operation.SUBTRACT.calculate(-3, 3));

		assertEquals(35, Operation.SUBTRACT.calculate(-10, -45));
	}
	
	@Test
	void multiply() {
		assertEquals(10, Operation.MULTIPLY.calculate(2, 5));

		assertEquals(-9, Operation.MULTIPLY.calculate(-3, 3));

		assertEquals(450, Operation.MULTIPLY.calculate(-10, -45));
	}
}
