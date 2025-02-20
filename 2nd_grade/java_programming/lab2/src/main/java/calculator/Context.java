package calculator;

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

    public void pop() {
        if (stack.isEmpty()) {
            throw new RuntimeException("Error: stack is empty");
        }
        stack.pop();
    }

    public double peek() {
        if (stack.isEmpty()) {
            throw new RuntimeException("Error: stack is empty");
        }
        return stack.peek();
    }

    public void define(String name, double value) {
        variables.put(name, value);
    }

    public double getVariable(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Error: variable " + name + " is not defined");
        }
        return variables.get(name);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }
}