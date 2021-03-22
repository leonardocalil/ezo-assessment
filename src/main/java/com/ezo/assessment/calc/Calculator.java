package com.ezo.assessment.calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public abstract class Calculator {

	protected static List<Character> macroSplit = new ArrayList<>(Arrays.asList('+', '-'));
	protected static List<Character> microSplit = new ArrayList<>(Arrays.asList('*', '/'));
	protected static List<Character> nanoSplit = new ArrayList<>(Arrays.asList('^'));
	
	protected abstract void checkCharacters(String expression) throws Exception;
	protected abstract void checkSintaxOperation(String expression) throws Exception;
	protected abstract List<String> splitExpressions(String expression, List<Character> operators);
	
	public Double calculate(String expression) throws Exception {
		expression = checkSintax(expression);
		return calculationEngine(expression, macroSplit);		
	}
	
	protected String checkSintax(String expression) throws Exception {
		expression = removeAllSpaces(expression.toLowerCase());
		checkParenteses(expression);
		checkCharacters(expression);	
		checkSintaxOperation(expression);	
		return expression;
	}
	
	
	protected Double calculate(Double valueA, Double valueB, Character operator) throws Exception {
		switch(operator) {
			case '^' : return Math.pow(valueA, valueB);
			case 's' : return Math.sqrt(valueA);
			case '*' : return valueA * valueB;
			case '/' : {
				if(valueB == 0) {
					throw new Exception("Error, division by 0");
				}
				return valueA / valueB;
			}
			case '+' : return valueA + valueB;
			case '-' : return valueA - valueB;			
		} 
		return 0.0;
	}
	/**
	 * 
	 * Here the magic happens
	 * Recursive method which execute operations in order and also dealing parentheses expressions: 
	 * 	 - 1: ^, sqrt
	 *   - 2: *, /
	 *   - 3: +, -   
	 */
	protected Double calculationEngine(String strExpression, List<Character> split) throws Exception {
		Pattern prio0 = Pattern.compile("[\\^]");
		Pattern prio1 = Pattern.compile("[\\*\\/]");		
		
		Double result = 0.0;
		List<String> listExpressions = splitExpressions(strExpression, split);
		
		String lastOperator = "";
		Double valueA = 0.0;
		for(String expression: listExpressions) {
			
			Double expressonValue = 0.0;
			String [] parseValue = expression.split("\\|");			

			StringBuilder value = new StringBuilder(parseValue[0]);				
			
			
			if(value.toString().contains("sqrt(")) {
				int begin = value.toString().indexOf("sqrt(");				
				String content = getParenthesesContent(value.toString(), begin+4);
				int end = begin + content.length()+6;
				expressonValue = calculationEngine(content, macroSplit);				
				expressonValue = Math.sqrt(expressonValue);				
				value.delete(begin, end);
				value.insert(begin, expressonValue);
			} else if(value.toString().contains("(")) {
				int begin = value.toString().indexOf("(");				
				String content = getParenthesesContent(value.toString(), begin);
				int end = begin + content.length()+2;						
				expressonValue = calculationEngine(content, macroSplit);
				value.delete(begin, end);
				value.insert(begin, expressonValue);
			} 
			if(prio1.matcher(value).find()) {
				expressonValue = calculationEngine(value.toString(), microSplit);
			} else if(prio0.matcher(value).find()) {
				expressonValue = calculationEngine(value.toString(), nanoSplit);
			} else {
				expressonValue = Double.valueOf(value.toString().replaceAll(",", "."));
			}
			
			if(lastOperator != null && !lastOperator.isEmpty()) {
				result = calculate(valueA, expressonValue, lastOperator.charAt(0));
				valueA = result;
			} else {
				result = expressonValue;
				valueA = result;
			}
			if(parseValue.length > 1) {
				lastOperator = expression.split("\\|")[1];
			}						
		}
		return result;
		
	}

	
	protected String getParenthesesContent(String expression, int begin) {
		/**
		 * I have to did this way in order to get the right content 
		 * in the scenario  with multiple parentheses ((())())
		 */
		
		Stack<Character> stack = new Stack<>();
		for(int end = begin;end < expression.length(); end++) {
			Character c = expression.charAt(end);
			if(c == '(') {
				stack.push('(');
			} else if(c == ')') {
				stack.pop();
				if(stack.empty()) {
					return expression.substring(begin+1, end);
				} 
			}
		}
		return "";		
	}
	protected String removeAllSpaces(String expression) {
		return expression.replaceAll("\\s+","");
	}
	
	protected void checkParenteses(String expression) throws Exception {
		Stack<Character> stack = new Stack<>();
		for(Character c : expression.toCharArray()) {		
			if(c.equals('(')) {
				stack.push('(');
			} else if(c.equals(')')) {
				if(stack.size() == 0) {
					throw new Exception("Check syntax: parentheses mistake, doesn't open/close properly"); 					
				} else  {
					stack.pop();
				}
			}
		}
		if(stack.size() > 0) {
			throw new Exception("Check syntax: parentheses mistake, doesn't open/close properly");
		}		 
	}
	

}
