package com.ezo.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ezo.assessment.calc.Calculator;
import com.ezo.assessment.calc.CalculatorSimple;

public class CalculatorTest {
	@Test
	public void corretSintax() {
		Calculator calc = new CalculatorSimple();
		try {
			calc.calculate("sqrt(9) ^ (sqrt(121) * 15) + 10 - (-500*-1)");
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}		
	}
	@Test
	public void incorretSintax() {
		Calculator calc = new CalculatorSimple();
		try {
			calc.calculate("-sqrt(9) ^ (sqrt(121) * 15) + 10 - (-500*-1)");			
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}		
	}
	@Test
	public void divisonByZero() {
		Calculator calc = new CalculatorSimple();
		try {
			calc.calculate("100/0");
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}		
	}
	@Test
	public void result() {
		Calculator calc = new CalculatorSimple();
		try {
			Double value = calc.calculate("100 ^ 2 * ((sqrt(9)*2/3)) - -500 + (-500 * -1)");			
			assertTrue(value.equals(21000.0));
		} catch (Exception e) {
			assertTrue(false);
		}		
	}

}
