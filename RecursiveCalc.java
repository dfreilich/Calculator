package calculator;

import java.util.Scanner;
import java.util.StringTokenizer;

/*
 * @author David Freilich
 * @date 11/24/14
 * This is Lab 5 for Data Structures, with Professor Breban - and under the esteemed 
 * leadership of the honorable Reb Ike the Sultan, shlit"a. 
 * This lab requests a mathematical expression, and then solves it recursively. 
 */
public class RecursiveCalc {
	public static void main(String[] args) {
		System.out.println("Type the expression you wish to calculate and press enter:");
		Scanner scan = new Scanner(System.in);
		String exp = scan.nextLine();

		//This calls the adjustString method to ensure there are no white spaces, and to 
		//add in parenthesis when necessary. 
		exp = adjustString (exp);
		System.out.println(evaluate(exp));
		scan.close();
	}

	/*
	 * This method evaluates a string, taking in a mathematical expression as a parameter
	 * and returning the solution of the expression. 
	 * 
	 * @param exp The expression to be solved
	 * @return The solution of the expression
	 */ 
	 public static int evaluate(String exp) {
		int result = 0;
		if (exp.length() < 1) {
			System.out.print("You haven't entered an expression!\nAnswer: ");
		}
		//Calls parenRecursion if it sees parenthesis
		else if (exp.contains("(")) {
			result = parenRecursion(exp);
		}
		else {
			//Splits it up by + and -, the lowest priority operators
			StringTokenizer st = new StringTokenizer(exp, "+-", true);
			// Calls the recEval method to recursively get the solution of the equation
			// for the first part of the equation
			result += recEval(st.nextToken());
			while(st.hasMoreTokens()) {
				String temp = st.nextToken();
				if (temp.equals("+") || temp.equals("-")) {
					//Calls the recEval function to get the solutions before adding them to the sum
					int next = recEval(st.nextToken());
					switch(temp) {
						case "+":
							result += next;
							break;
						case "-":
							result -= next;
							break;
					}
				}
			}	
		}
		return result;
	}
	
	/*
	 * This function is used to recursively solve an expression handed to it. 
	 * 
	 * @param exp The (part of the) expression to be solved
	 * @return The solution of the (part of) expression
	 */
	private static int recEval(String exp) {
		int result = 0;
		//Calls the isInt() function to see if it's an integer, and returns it. 
		if (isInt(exp)) {
			result = Integer.parseInt(exp);
		}
		else {
			//Calls parenthesis recursion if there is one. 
			if (exp.contains("(")) {
				result = parenRecursion(exp);
			}
			//If it containts exponent, calls the exponentRecursion function
			else if (exp.contains("^")) {
				result = exponentRecursion(exp);
			}
			else {
				StringTokenizer st = new StringTokenizer(exp, "*/%", true);
				//Recursively solves the parts of the equation
				result += recEval(st.nextToken());
				while (st.hasMoreTokens()) {
					String operator = st.nextToken();
					int next = recEval(st.nextToken());
					switch(operator) {
						case "*":
							result *= next;
							break;
						case "/":
							result /= next;
							break;
						case "%":
							result %= next;
							break;
					}
				}
			}
		}
		return result;
	}
	
	/*
	 * This method is used for parenthesis recursion. It recursively solves the middle of
	 * the parenthesis, and then uses that to solve the rest of the function using the recEval
	 * function
	 * 
	 * @param parenString The expression with the parenthesis string
	 * @return The solution of the equation, recursively solved with recEval. 
	 */
	@SuppressWarnings("resource")
	private static int parenRecursion(String parenString) {
		int result = 0;
		if (isInt(parenString)) {
			result = Integer.parseInt(parenString);
		}
		else {
			//The part of the expression prior to the parenthesis
			String nonParen = "";
			//Used to scan through the string, letter by letter
			Scanner scan = new Scanner(parenString).useDelimiter("");
			while (scan.hasNext()) {
				String paren = "";
				String temp = scan.next();
				while (!temp.equals("(") && scan.hasNext()) {
					nonParen += temp;
					temp = scan.next();
				}
				//Checks to ensure that the string isn't done
				if (!scan.hasNext()) {
					nonParen += temp;
				}
				//It's stopped bc there is a parenthesis
				else {
					//Counts the number of open and closed parenthesis
					int parenOpen = 1;
					int parenClose = 0;
					while (parenClose < parenOpen) {
						temp = scan.next();
						if (temp.equals("(")) {
							parenOpen++;
						}
						if (temp.equals(")")) {
							parenClose++;
						}
						paren += temp;
					}
					//Shaves off the last character of the string, the ")" sign
					paren = paren.substring(0, paren.length() -1);
					//Recursively solves the parenthesis, and then concatenates it onto the 
					//rest of the expression.
					nonParen += parenRecursion(paren);
				}
			}
			//Evaluates the entirety of the expression thus far.
			result += evaluate(nonParen);
			scan.close();
		}
		return result;
	}
	
	/*
	 * This method is used for recursively solving equations with exponents. 
	 * 
	 * @param exp The expression to be solved
	 * @return The solution of the expression. 
	 */
	private static int exponentRecursion(String exp) {
		int result = 0;
		//The solution of the exponent alone
		int expSoFar = 0;
		//The equation, without the exponent yet
		String noExp = "";
		StringTokenizer st = new StringTokenizer(exp, "*/%^", true);
		while (st.hasMoreTokens()) {
			String first = st.nextToken();
			String operator = "";
			if (st.hasMoreTokens()) {
				operator = st.nextToken();				
			}
			if(operator.equals("^")) {
				String second = st.nextToken();
				//Transforms the numbers into doubles, and uses the Math.pow function to get the solution.
				double num = Integer.parseInt(first);
				double pow = Integer.parseInt(second);
				expSoFar = (int) Math.pow(num, pow);
				//Adds the solved exponent part to the rest of the equation
				noExp += expSoFar;
			}
			else {
				noExp += first + operator;	
			}
		}
		//Recursively solves the equation, using the recEval function.
		result = recEval(noExp);
		return result;
	}
	
	/*
	 * This function is used to check whether a string value is an int, catching the exception
	 * if it isn't and replying false.
	 * 
	 * @param test The String to be tested
	 * @return The boolean value - true if it is an integer, and false if it  isn't. 
	 */
	private static boolean isInt(String test) {
		try {
			Integer.parseInt(test);
		}
		catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/*
	 * This function is used in the preparation of the equation, ensuring that 
	 * the proper parenthesis are there, and excising all white-spaces within it. 
	 * 
	 * @param expresison The expression to be treated.
	 * @return The adjusted expression.
	 */
	@SuppressWarnings("resource")
	private static String adjustString (String expression) {
		String result = "";
		if (expression.contains("^")) {
			Scanner scan = new Scanner(expression).useDelimiter("");
			while (scan.hasNext()) {
				String temp = scan.next();
				//If we get to an exponent
				if (temp.equals("^")) {
					result += temp;
					temp = scan.next();
					//If it is parenthesis'd already, we're good :)
					if (temp.equals("(")) {
						result += temp;
					}
					//If there isn't a parenthesis, we add in at the beginning and end of it.
					else {
						result += "(";
						while((isInt(temp) || temp.equals("^")) && scan.hasNext()) {
							result += temp;
							temp = scan.next();
							//If there is a parenthesis, we wait until after it is finished
							if (temp.equals("(")) {
								while(!temp.equals(")")) {
									result += temp;
									temp = scan.next();
								}
							}
						}
						//If it is the last value, we first add in the number (bc it wouldn't have
						//been added in by the method prior to it) and then close it with a ).
						if (!scan.hasNext()) {
							result += temp + ")";
						}
						else {
							//Adds in an extra closing parenthesis, and then continues the loop.
							result +=  ")" + temp;
						}
					}
				}
				//If we don't find an exponent, we just continue to add as normal to the result string.
				else{
					result += temp;
				}
			}
			scan.close();
			//Uses a regex to excise all white-spaces.
			result = result.replaceAll("\\s+", "");
		}
		else {
			//If there are no exponents, just excises white spaces.
			result = expression.replaceAll("\\s+", "");
		}
		return result;
	}
}