package calculator.commands;


public class PushCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Push executed");
    }
}