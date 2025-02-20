package calculator.commands;


public class PrintCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Print executed");
    }
}
