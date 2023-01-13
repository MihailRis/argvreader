package mihailris.argv;

import java.util.ArrayDeque;
import java.util.Queue;

public class ArgvParser {
    private final String[] args;
    private int pointer;
    private final Queue<String> keywords;

    ArgvParser(String[] args) {
        this.args = args;
        this.keywords = new ArrayDeque<>();
    }

    public boolean hasNext() {
        return pointer < args.length;
    }

    public void next() {
        if (!hasNext())
            throw new IllegalStateException("no more args");
        String arg = args[pointer++];
        if (arg.startsWith("--")) {
            keywords.add(arg.substring(2));
        } else if (arg.startsWith("-")) {
            for (int i = 1; i < arg.length(); i++) {
                keywords.add(String.valueOf(arg.charAt(i)));
            }
        } else {
            pointer--;
        }
    }

    public int readInt(String name) {
        if (!hasNext()) {
            throw new ArgvParsingException(name+" missing");
        }
        try {
            return Integer.parseInt(args[pointer++]);
        } catch (NumberFormatException e) {
            throw new ArgvParsingException(name+" must be integer");
        }
    }

    public float readFloat(String name) {
        if (!hasNext()) {
            throw new ArgvParsingException(name+" missing");
        }
        try {
            return Float.parseFloat(args[pointer++]);
        } catch (NumberFormatException e) {
            throw new ArgvParsingException(name+" must be a number");
        }
    }

    public String readString(String name) {
        if (!hasNext()) {
            throw new ArgvParsingException(name+" missing");
        }
        String arg = args[pointer++];
        if (hasNext())
            next();
        return arg;
    }

    public String nextKeyword() {
        return keywords.remove();
    }

    public boolean hasNextKeyword() {
        return !keywords.isEmpty();
    }

    public String peekNext() {
        return args[pointer];
    }

    public String peekKeyword() {
        return keywords.peek();
    }
}
