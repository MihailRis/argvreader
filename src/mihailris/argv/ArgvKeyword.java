package mihailris.argv;

public class ArgvKeyword {
    private final String name;
    private final String description;
    private final String[] aliases;
    private final KeywordConsumer consumer;

    public ArgvKeyword(String name, KeywordConsumer consumer, String description, String... aliases) {
        this.name = name;
        this.consumer = consumer;
        this.description = description;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public KeywordConsumer getConsumer() {
        return consumer;
    }

    public String[] getAliases() {
        return aliases;
    }

    public interface KeywordConsumer {
        void perform(ArgvKeyword keyword, ArgvParser parser);
    }
}
