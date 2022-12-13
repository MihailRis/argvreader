package mihailris.argv;

public class ArgvUsage {
    private final String command;
    private final String description;

    public ArgvUsage(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }
}
