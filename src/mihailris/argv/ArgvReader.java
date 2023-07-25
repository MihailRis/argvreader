package mihailris.argv;

import java.io.PrintStream;
import java.util.*;

public class ArgvReader {
    private final ArgvParser parser;
    private final Map<String, ArgvKeyword> keywordsMap;
    private final List<String> headlines = new ArrayList<>();
    private final Set<ArgvKeyword> keywords = new HashSet<>();
    private final List<ArgvUsage> usage = new ArrayList<>();
    private final List<ArgvUsage> examples = new ArrayList<>();
    private int helpCommandWidth = 5;
    private int examplesCommandWidth = 5;
    private final PrintStream out;

    public ArgvReader(String[] argv) {
        parser = new ArgvParser(argv);
        keywordsMap = new HashMap<>();
        createHelp();
        out = System.out;
    }

    public void addHelpHeadLine(String line) {
        headlines.add(line);
    }

    public void addUsage(String usage, String description) {
        this.usage.add(new ArgvUsage(usage, description));
        if (usage.length() > helpCommandWidth) {
            helpCommandWidth = usage.length();
        }
    }

    public void addExample(String usage, String description) {
        this.examples.add(new ArgvUsage(usage, description));
        if (usage.length() > examplesCommandWidth) {
            examplesCommandWidth = usage.length();
        }
    }

    private void printSamplesSection(Collection<ArgvUsage> samples, int helpCommandWidth) {
        if (samples.isEmpty()) {
            out.println("  empty");
            return;
        }
        out.print("  [command]");
        for (int i = 0; i < helpCommandWidth+8; i++) {
            System.out.print(' ');
        }
        out.println("[description]");
        for (ArgvUsage usage : samples) {
            out.print("  $executable ");
            String command = usage.getCommand();
            out.print(command);
            for (int i = command.length(); i < helpCommandWidth+5; i++) {
                out.print(' ');
            }
            out.print(": ");
            out.println(usage.getDescription());
        }
    }

    /**
     * Create auto <code>--help</code> command
     */
    private void createHelp() {
        add("help", "show help", (keyword, parser1) -> {
            for (String headline : headlines) {
                out.println(headline);
            }
            out.println("Usage:");
            printSamplesSection(usage, helpCommandWidth);

            out.println("Examples:");
            printSamplesSection(examples, examplesCommandWidth);

            out.println("Args:");
            for (ArgvKeyword kw : keywords) {
                String name = kw.getName();
                out.print("--");
                out.print(name);
                int offset = kw.getName().length() + 2;
                for (String alias : kw.getAliases()){
                    out.print('/');
                    if (alias.length() == 1) {
                        out.print('-');
                        offset += 2;
                    } else {
                        out.print("--");
                        offset += 3;
                    }
                    out.print(alias);
                    offset += alias.length();
                }
                for (int i = offset; i < 32; i++) {
                    out.print(" ");
                }
                out.println(kw.getDescription());
            }
        }, "h");
    }

    /**
     * Add new command to the repository
     * @param name command name
     * @param description command description
     * @param consumer command callback
     * @param aliases command aliases (including shorts)
     */
    public void add(String name, String description, ArgvKeyword.KeywordConsumer consumer, String... aliases) {
        ArgvKeyword keyword = new ArgvKeyword(name, consumer, description, aliases);
        keywordsMap.put(name, keyword);
        for (String alias : aliases) {
            keywordsMap.put(alias, keyword);
        }
        keywords.add(keyword);
    }

    /**
     * @param name required value name (used in error messages)
     * @return word
     * @throws ArgvParsingException word expected but end or keyword found
     */
    public String nextWord(String name) {
        if (parser.hasNext()) {
            if (parser.peekNext().startsWith("-")) {
                throw new ArgvParsingException(name+" missing");
            }
        }

        return parser.readString(name);
    }

    public void execute() {
        if (!parser.hasNextKeyword() && parser.hasNext()) {
            parser.next();
        }
        while (parser.hasNextKeyword()) {
            String alias = parser.nextKeyword();
            ArgvKeyword keyword = keywordsMap.get(alias);
            if (keyword == null) {
                throw new ArgvParsingException("unknown keyword '"+alias+"'");
            }
            ArgvKeyword.KeywordConsumer consumer = keyword.getConsumer();
            try {
                consumer.perform(keyword, parser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (!parser.hasNextKeyword() && parser.hasNext()) {
                parser.next();
            }
        }
    }

    public boolean isNextHelp() {
        if (!parser.hasNextKeyword() && parser.hasNext() && parser.peekNext().startsWith("-")) {
            parser.next();
        }
        return parser.hasNextKeyword() && keywordsMap.get(parser.peekKeyword()) == keywordsMap.get("help");
    }

    public void printHelp() {
        ArgvKeyword keyword = keywordsMap.get("help");
        try {
            keyword.getConsumer().perform(keyword, parser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
