package calculator.commands;

public class AddCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Add executed");
    }
}