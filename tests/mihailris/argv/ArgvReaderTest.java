package mihailris.argv;

import org.junit.Test;

public class ArgvReaderTest {
    @Test
    public void test() {
        String[] args = "test.oi -r out.mp4 --fps 60 --speed 1.2".split("\\s");
        try {
            ArgvReader reader = new ArgvReader(args);
            reader.addUsage("source-file", "preview film");
            reader.addUsage("source-file --render output-file", "render film to file");
            reader.addUsage("source-file --render output-file --speed 1.5", "render film to file with speed x1.5");
            reader.addExample("test.oi --render test.mp4", "render film test.oi to test.mp4");
            reader.add("render", "render film to video", (keyword, parser) ->
                    System.out.println("Render to "+parser.readString("output-file")), "r");
            reader.add("framerate", "set animation framerate", (keyword, parser) ->
                    System.out.println("Framerate set to "+parser.readInt("framerate")), "fps", "f");
            reader.add("speed", "set time multiplier", (keyword, parser) ->
                    System.out.println("Speed set to "+parser.readFloat("speed")), "s");

            if (reader.isNextHelp()) {
                reader.printHelp();
                return;
            }
            String film = reader.nextWord("film");
            System.out.println("film: " + film);
            reader.execute();
        } catch (ArgvParsingException e) {
            System.err.println("arguments parsing error: "+e.getLocalizedMessage());
        }
    }
}