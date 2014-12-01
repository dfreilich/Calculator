package calculator;

import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

/*
 * @author David Freilich
 * @date 11/24/14
 * This is Lab 5 for Data Structures, with Professor Breban - and under the esteemed 
 * leadership of the honorable Reb Ike the Sultan, shlit"a. 
 * This lab requests a mathematical expression, transforms it into postfix format using a stack, 
 * and then computes the sum using the stack. 
 * 
 */
public class PostfixCalc {
	private static String output;
	private static Stack stack;
	
	/*
	 * The main method. Prompts for a string, calls postfix, and then evaluating it. 
	 */
	public static void main(String[] args) {
		System.out.println("Type the expression you wish to calculate and press enter:");
		Scanner scan = new Scanner(System.in);
		String exp = scan.nextLine();
		String posf = convert(exp);
		for (int i = 0; i < stack.size(); i++) {
			System.out.println(stack.get(i));
		}
		System.out.println(output);
		System.out.println(evaluate(posf));
		scan.close();
	}
	
	/*converts expression into postfix notation
	 * 
	 * @param String norm The string that you want to convert to postfix
	 * @return The string converted into postfix format
	 */
	public static String convert(String norm) {
		//Initializes the fields
		output = "";
		stack = new Stack();
		//Creates a StringTokenizer object to break the string by the operators
		StringTokenizer st = new StringTokenizer(norm, "+-()*/^%", true);
		while(st.hasMoreTokens()) {
			String temp = st.nextToken();
			//Uses a regex to check whether it's a digit of any length
			if (temp.matches("\\d*")) {
				//Adds the digit straight to the output string
				output += temp + " ";
			}
			//It isn't a digit, and therefore is an operator
			else {
				//Calls the adjustStack method to take care of the operator
				adjustStack(temp);
			}
		}
		//Pops all the members of the stack onto the output string to complete the postfix transfer
		while(!stack.isEmpty()) {
			output+= stack.pop() + " ";
		}
		return output;
	}
			
	/*
	 *evaluates postfix notation into final answer 
	 *
	 *@param posf The equation converted into postfix format (output in the last method)
	 *@return The sum of the equation
	 */
	@SuppressWarnings("unchecked")
	public static int evaluate(String posf) {
		//Creates a new String Tokenizer object, breaking up the given string by spaces
		StringTokenizer st = new StringTokenizer(posf, " ");
		while(st.hasMoreTokens()) {
			String temp = st.nextToken();
			//Uses a regex to check whether it's a digit of any length 
			if (temp.matches("\\d*")) {
				//Converts the number from String format to int, and pushes it on to the stack
				stack.push(Integer.parseInt(temp));
			}
			//We have a mathematical operator
			else {
				//Gets the past two digits to perform the operation upon
				int b = (int) stack.pop();
				int a = (int) stack.pop();
				//Calls the evalStack method to get the answer, and pushes it on to the stack
				stack.push(evalStack(a, b, temp));
			}
		}
		//Returns the sum of the equation, which will be the only thing on the stack
		return (int) stack.pop();
	}
	
	/*
	 * This method, used to help with postfix conversion, adjusts the stack depending upon the
	 * operator given, and the elements in the stack already
	 * 
	 * @param operator The operator we are up to in the reading of the string
	 */
	private static void adjustStack(String operator) {
		//Given a close parenthesis, we pop everything onto the stack until we find the open parenthesis
		if(operator.equals(")")) {
			while(!stack.peek().equals("(")) {
				output+= stack.pop() + " ";
			}
			//Pops the open parenthesis in order to remove it from the string
			stack.pop();
		}
		//If the number given isn't a lower precedence than the one immediately beneath it on the stack,
		//pushes it on to the stack.
		else if (!isLowerPrec(operator)) {
			stack.push(operator);
		}
		//If the numbers underneath it in the stack are of higher precedence, and you can't put the
		//low one down, pops to the output until we can put the operator given onto the stack. 
		else {
			while(isLowerPrec(operator) ) {
				output+= stack.pop() + " ";
			}
			//Pushes the operator onto the stack
			stack.push(operator);
		}
	}
	
	/*
	 * This method is used in converting to postfix. It checks whether the operator we are dealing
	 * with is of lower precedence than the highest number of the stack
	 */
	private static boolean isLowerPrec(String operator) {
		boolean opIsLower = true;
		//If the stack is empty, then the operator is by definition not lower than what is underneath it
		// and the operator can safely be put into the stack.
		if (stack.isEmpty()) {
			opIsLower = false;
		}
		else {
			String peek = (String) stack.peek();
			//Uses a switch statement to judge, based on what the operator is
			switch (operator) {
				//If it's an open parenthesis, it can be put upon everything
				case "(":
					opIsLower = false;
					break;
				//If it's + or -, it can be put upon an open parenthesis, but everything else
				// will be a higher precedence than the + and -, and it will therefore not be able
				//to be upon the stack until they are removed
				case "+": case "-":
					if (peek.equals("(")) {
						opIsLower = false;
					}
					else {
						opIsLower = true;
					}
					break;
				 //If it's *, /, or %, it is higher than (, + and -, and therefore can be put upon
				 //them, but it is lower than ^, as well as any of the operators themselves, and 
				 //it therefore wouldn't be able to put placed upon it.
				case "*": case "/": case "%":
					if (peek.equals("^") || peek.equals("*") || peek.equals("/") || peek.equals("%")) {
						opIsLower = true;
					}
					else {
						opIsLower = false;
					}
					break;
				 //If it's a ^, it is higher precedence than anything, and it can certainly 
				 //be placed upon the stack
				case "^":
					opIsLower = false;
					break;
			}
		}
		return opIsLower;
	}
	
	/*
	 * This method is used in evaluating the postfix equation. It is fed two integers, as well
	 * as a mathematical operator in String form, and it uses a switch statement to compute the
	 * answer to the mini-equation handed to it. 
	 */
	private static int evalStack(int a, int b, String operator) {
		int answer = 0;
		switch (operator) {
			case "+":
				answer = a + b;
				break;
			case "-":
				answer = a - b;
				break;
			case "*":
				answer = a * b;
				break;
			case "/":
				answer = a / b;
				break;
			case "%":
				answer = a % b;
				break;
			 //To compute exponents, we convert the ints to doubles and use the Math.pow() function.
			 //The function requires doubles, so we convert it to double, and then cast it as an int
			 //when we use the function pow. 
			 //
			case "^":
				double a1 = a;
				double b1 = b;
				answer = (int) Math.pow(a1, b1);
				break;
		}
		return answer;
	}
}
