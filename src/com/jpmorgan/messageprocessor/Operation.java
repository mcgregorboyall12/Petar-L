package com.jpmorgan.messageprocessor;

/** 
 * Enum to represent all supported operations on {@link Adjustment}s to past {@link Sale}s.
 */
public enum Operation {

	ADD {
		@Override
		public int calculate(int a, int b) {
			return a + b;
		}
	},
	
	SUBTRACT {
		@Override
		public int calculate(int a, int b) {
			return a - b;
		}
	},
	
	MULTIPLY {
		@Override
		public int calculate(int a, int b) {
			return a * b;
		}
	};
	
	/**
	 * Performs the operation on the two given numbers
	 * @param a the first number
	 * @param b the second number
	 * @return
	 */
	public abstract int calculate(int a, int b);
	
};
