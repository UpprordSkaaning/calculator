import java.util.*;

import static java.lang.Double.NaN;
import static java.lang.Math.pow;


/*
 *   A calculator for rather simple arithmetic expressions
 *
 *   This is not the program, it's a class declaration (with methods) in it's
 *   own file (which must be named Calculator.java)
 *
 *   NOTE:
 *   - No negative numbers implemented
 */
public class Calculator {

    // Here are the only allowed instance variables!
    // Error messages (more on static later)
    final static String MISSING_OPERAND = "Missing or bad operand";
    final static String DIV_BY_ZERO = "Division with 0";
    final static String MISSING_OPERATOR = "Missing operator or parenthesis";
    final static String OP_NOT_FOUND = "Operator not found";

    // Definition of operators
    final static String OPERATORS = "+-*/^";

    // Method used in REPL
    double eval(String expr) {
        if (expr.length() == 0) {
            return NaN;
        }
        List<String> tokens = tokenize(expr);
        List<String> postfix = infix2Postfix(tokens);
        return evalPostfix(postfix);
    }

    // ------  Evaluate RPN expression -------------------

    double evalPostfix(List<String> postfix) {
        ArrayDeque<Double> stack = new ArrayDeque<>();
        for(String token: postfix) {
            if(isNum(token)) {  //This time we add tokens that are numbers onto the stack
                stack.push(Double.valueOf(token));
            } else if("(".equals(token)) { //If an open bracket managed to sneak into postfix
                //it was not deleted in the handleParen method, meaning we have mismatched brackets.
                //Throw exception.
                throw new IllegalArgumentException(MISSING_OPERATOR);

            } else if(stack.size() > 1) { //When we encounter an operator we pop 2 numbers from the stack and
                //apply the operator, then we write the result back to the stack.
                stack.push(applyOperator(token,stack.pop(),stack.pop()));
            } else { //If the stack was empty an operator was missing, throw exception
                throw new IllegalArgumentException(MISSING_OPERAND);
            }
        }
        //The result should be on the top of the stack. If there are any more elements on the stack
        //We did not have enough operators, throw exception
       if(stack.size() > 1){
           throw new IllegalArgumentException(MISSING_OPERATOR);

       }
       return stack.pop();

    }



    double applyOperator(String op, double d1, double d2) {
        switch (op) {
            case "+":
                return d1 + d2;
            case "-":
                return d2 - d1;
            case "*":
                return d1 * d2;
            case "/":
                if (d1 == 0) {
                    throw new IllegalArgumentException(DIV_BY_ZERO);
                }
                return d2 / d1;
            case "^":
                return pow(d2, d1);
        }
        throw new RuntimeException(OP_NOT_FOUND);
    }

    // ------- Infix 2 Postfix ------------------------

    List<String> infix2Postfix(List<String> infix) {

        Deque<String> stack = new ArrayDeque<>();
        List<String> postfix = new ArrayList<>();
        for(String token: infix) {
            if(isNum(token)) {  //Numbers should be added instantly
                postfix.add(token);
            } else if(")".equals(token)) {  //For everything else we need helper methods
                handleParen(stack,postfix);
            } else {
                handleOperator(token,stack,postfix);

            }
        }
        while(!stack.isEmpty()) { //Add the remaining operators to the postfix
            postfix.add(stack.pop());
        }
        return postfix;
    }

    boolean isNum(String token) {
        return !(OPERATORS.contains(token) || "()".contains(token));
    }

    void handleOperator(String op, Deque<String> stack, List<String> result) {
        while(!(stack.isEmpty()) && popNext(op,stack.peek())) {
            result.add(stack.pop());
        }
        stack.push(op);
    }


    //According to the algorithm we should keep popping the stack as long as the operator on the stack has greater
    //Precedence than the current operator and neither of them are parentheses.
    boolean popNext(String op, String stackTop) {
        return !"(".equals(op) && (!"(".equals(stackTop) && (getPrecedence(op)) < getPrecedence(stackTop) + assocVal(stackTop));
    }

    //It would be nicer to have this in tho handleOperator method, but I can't figure out hov
    void handleParen(Deque<String> stack, List<String> postfix) {
        try {
            while (!"(".equals(stack.peek())) {
                postfix.add(stack.pop());
            }
            stack.pop();
        } catch (NoSuchElementException e) {
            //We catch the exception, only to throw another. This is highly competent programming
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }

    }

    int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    Assoc getAssociativity(String op) {
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            System.out.println(op);
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    /*Assigns the value 1 to left associate operators and the value 0 to right associate operators and parentheses
    This allows us take associativity into account when comparing operator precedence.
     */
    int assocVal(String op) {
       if(Assoc.LEFT.equals(getAssociativity(op))) {
            return 1;
        }
        return 0;
    }

    enum Assoc {
        LEFT,
        RIGHT
    }

    // ---------- Tokenize -----------------------

    // List String (not char) because numbers (with many chars)
    List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for(char c: expr.toCharArray()) { // It seems we can't iterate over a string without first converting it to a char allay.
            String cs = String.valueOf(c); //This is highly unfortunate since we must immediately convert the chars back to strings.
            if(Character.isDigit(c)) {
                current.append(cs);
                continue;
            }
            if(current.length() > 0) { //End the current token when any non-digit is encountered...
                tokens.add(current.toString());
                current = new StringBuilder();
            }
            if(OPERATORS.concat("()").contains(cs)) { //... But only append operators and brackets to the final list
                tokens.add(cs);
            }
        }
        if(current.length() > 0) {
            tokens.add(current.toString()); //if there is something left in the stringbuilder after the last
            // Iteration, we want to append it
        }
        return tokens;
    }

}
