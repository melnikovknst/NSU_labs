package calculator.commands;


public class PopCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Pop executed");
    }
}
