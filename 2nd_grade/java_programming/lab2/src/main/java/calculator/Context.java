package calculator;

import calculator.exceptions.UndefinedVariableException;

import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

public class Context {
    private final Stack<Double> stack = new Stack<>();
    private final Map<String, Double> variables = new HashMap<>();

    public int stackSize() {
        return stack.size();
    }

    public void push(double value) {
        stack.push(value);
    }

    public double pop() {
        return stack.pop();
    }

    public double peek() {
        return stack.peek();
    }

    public void define(String name, double value) {
        variables.put(name, value);
    }

    public double getVariable(String name) throws UndefinedVariableException {
        if (!variables.containsKey(name)) {
            throw new UndefinedVariableException(name);
        }
        return variables.get(name);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }
}