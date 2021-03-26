package com.ezo.assessment.calc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorSimple extends Calculator {
	
	public Double calculate(String expression) throws Exception {
		expression = checkSintax(expression);
		return calculationEngine(expression, macroSplit);		
	}
	protected List<String> splitExpressions(String expression, List<Character> operators) {
		List<String> result = new ArrayList<>();
		StringBuilder strValue = new StringBuilder();
		Boolean hasPreviousOperator = false;
		for(int ix = 0; ix < expression.length(); ix++) {
			Character c = expression.charAt(ix);			
			if(c.equals('s') 
					|| c.equals('(') 
					|| (operators.contains(c) 
							&& !hasPreviousOperator)) {
				/*hasPreviousOperator is an idicator of expressions like this: -500*-1
				 * */
				if(strValue.length() == 0 
						&& (operators.contains(c))) {
					strValue.append(c);
				} else if(strValue.length() > 0  
					&& operators.contains(c)) {					
					strValue.append("|").append(c);	
					result.add(strValue.toString());
					strValue.delete(0, strValue.length());
				} else if(c.equals('s')) {
					String content = getParenthesesContent(expression, ix+4); 
					ix+= 5+content.length();
					strValue.append("sqrt(").append(content).append(")");
				} else if(c.equals('(')) {
					String content = getParenthesesContent(expression, ix);
					ix+= content.length()+1;
					strValue.append("(").append(content).append(")");
				}
				hasPreviousOperator = false;
			} else {
				switch(c) {
					case '^':
					case '*':
					case '/': hasPreviousOperator = true; break;
					default:  hasPreviousOperator = false;
				}
				
				strValue.append(c);
			}			 
		}
		if(strValue.length() > 0) {
			result.add(strValue.toString());
		}
				
		return result;
	}
	
	protected void checkSintaxOperation(String expression) throws Exception {
		StringBuilder valueA = new StringBuilder();
		int ix = 0;		
		Character lastOperator = 0;
		while(ix < expression.length()) {
			Character c = expression.charAt(ix);
			switch(c) {
				case 's': {
					if(ix+4 >= expression.length()) {
						throw new Exception("Check syntax: sqrt operation is missing parentheses");
					}
					if(!"sqrt(".equals(expression.substring(ix, ix+5))) {
						throw new Exception("Check syntax: sqrt operation is missing parentheses");
					}
					if(lastOperator == 0 && ix > 0) {
						throw new Exception("Check syntax: sqrt operation is missing operator (+, -, /, ^, *) nearby on previous value");
					}
					String content = getParenthesesContent(expression, ix+4); 
					ix+= 5+content.length();
					lastOperator = 0;
					valueA.append("sqrt()");
					checkSintaxOperation(content);
				}; break;
				case '(': {
					if(lastOperator == 0 && ix > 0) {
						throw new Exception("Check syntax: missing operator (+, -, /, ^, *) nearby on previous value");
					}
					String content = getParenthesesContent(expression, ix);
					ix+= content.length()+1;
					lastOperator = 0;
					valueA.append("sqrt()");
					checkSintaxOperation(content);					
				}; break;
				case '^': 
				case '*': 
				case '/': {
					if(ix+1 >= expression.length()) {
						throw new Exception("Check syntax: operation (^, *, /) in wrong position");
					}
					if(valueA.length() == 0) {
						throw new Exception("Check syntax: operation (^, *, /) in wrong position");
					}
					valueA.delete(0, valueA.length());
					lastOperator = c;
				}; break;
				case '+':
				case '-': {
					if(ix+1 >= expression.length()) {
						throw new Exception("Check syntax: operation (-, +) in wrong position");
					}					
					if(lastOperator != 0 || ix == 0) {
						String next = expression.substring(ix+1,ix+2);
						Pattern pattern = Pattern.compile("[0-9\\(]");
						Matcher matcher = pattern.matcher(next);
						if(!matcher.find()) {
							throw new Exception("Check syntax: operation (-, +) in wrong position");
						}
					}
					
					valueA.delete(0, valueA.length());
					lastOperator = c;
				}; break;
				default: {
					lastOperator = 0;
					valueA.append(c);
				}					
			}
			ix++;
		}
		
	}
	
	
	/**
	 * Check if there are any invalid characteres
	 */
	protected void checkCharacters(String expression) throws Exception {
		String aux = expression
				.replaceAll("[sqrt\\(\\)\\s+\\*\\^\\/\\+\\-0-9]","");				
		if(aux != null && aux.length() > 0) {
			throw new Exception("Check syntax: there are invalid characters");
		} 
	}
	
}
