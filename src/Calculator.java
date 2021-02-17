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
        ArrayDeque<String> stack = new ArrayDeque<>();
        for(String token: postfix) {
            if(isNum(token)) {
                //DO SOMETHING
            }
        }
        return 0;
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
            if(isNum(token)) {
                postfix.add(token);
            } else if("(".equals(token)) {
                stack.push(token);
            } else if(OPERATORS.contains(token)) {
                addOperator(token,stack,postfix);

            } else if (")".equals(token)) {
                handleParen(stack,postfix);
            }
        }
        while(!stack.isEmpty()) {
            postfix.add(stack.pop());
        }
        return postfix;
    }

    boolean isNum(String token) {
        return !(OPERATORS.contains(token) || "()".contains(token));
    }
    void addOperator(String op, Deque<String> stack, List<String> result) {
        while(!(stack.isEmpty()) && popNext(op,stack.peek())) {
            result.add(stack.pop());
        }
        stack.push(op);
    }



    boolean popNext(String op, String stackTop) {
        if("(".equals(stackTop)) {
            return false;
        }

        int assoc = 0;
        if(getAssociativity(op).equals(Assoc.LEFT)) { assoc = 1; }
        return (getPrecedence(op)) < getPrecedence(stackTop) + assoc;
    }

    void  handleParen(Deque<String> stack, List<String> postfix) {
        try {
            while (!"(".equals(stack.peek())) {
                postfix.add(stack.pop());
            }
            stack.pop();
        } catch (EmptyStackException e) {
            //TODO Figure out what to do with mismatched brackets
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
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    enum Assoc {
        LEFT,
        RIGHT
    }

    // ---------- Tokenize -----------------------

    // List String (not char) because numbers (with many chars)
    List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        for(char c: expr.toCharArray()) { // It seems we can't iterate over a string without first converting it to a char allay.
            String cs = String.valueOf(c); //This is highly unfortunate since we must immediately convert the chars back to strings.
            if((OPERATORS.contains(cs) || "()".contains(cs))) {
                if(current.length() > 0) {
                    tokens.add(current.toString());
                }
                current = new StringBuilder();
                tokens.add(cs);
            } else if(Character.isDigit(c)) {
                current.append(cs);
            }
        }
        if(current.length() > 0) {
            tokens.add(current.toString());
        }
        return tokens;
    }

}
