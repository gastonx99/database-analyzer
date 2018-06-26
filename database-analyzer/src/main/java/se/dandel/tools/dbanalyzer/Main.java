package se.dandel.tools.dbanalyzer;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(final String[] args) throws IOException {
        List<String> arglist = args == null ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(args));
        try {
            arglistAddIfNotExists(arglist, "output", "target/output.plantuml");
            addBetmeSettings(arglist);
            Options options = createOptions();
            CommandLine cmd = parseOptions(options, arglist);
            Settings settings = parseSettings(cmd);


            DatabaseAnalyzer analyzer = DatabaseAnalyzer.newInjectedInstance(settings);
            analyzer.analyze();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Settings parseSettings(CommandLine cmd) {
        Settings settings = new Settings();
        settings.setJdbcDriver(cmd.getOptionValue("jdbcDriver"));
        settings.setJdbcUrl(cmd.getOptionValue("jdbcUrl"));
        settings.setTablenamePattern(cmd.getOptionValue("tablenamePattern"));
        settings.setCatalogueName(cmd.getOptionValue("catalogueNamePattern"));
        settings.setOutputFilename(cmd.getOptionValue("output"));
        settings.setLiquibaseChangelog(cmd.getOptionValue("liquibaseChangelog"));
        if (cmd.hasOption("discriminatorColumn")) {
            settings.setDiscriminatorColumn(cmd.getOptionValue("discriminatorColumn"));
        }
        if (cmd.hasOption("technicalColumns")) {
            settings.setTechnicalColumns(cmd.getOptionValue("technicalColumns"));
        }
        return settings;
    }

    private static CommandLine parseOptions(Options options, final List<String> args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args.toArray(new String[]{}));
    }

    private static void addBetmeSettings(List<String> arglist) {
        arglistAddIfNotExists(arglist, "jdbcDriver", "com.mysql.cj.jdbc.Driver");
        arglistAddIfNotExists(arglist, "jdbcUrl", "jdbc:mysql://localhost:3306/betme?user=betme&password=Milano93");
        arglistAddIfNotExists(arglist, "catalogueNamePattern", "betme");
//        arglistAddIfNotExists(arglist, "tablenamePattern", "NULL");
    }

    private static void arglistAddIfNotExists(List<String> arglist, String key, String value) {
        String modifiedKey = "-" + key;
        if (!arglist.contains(modifiedKey)) {
            arglist.add(modifiedKey);
            arglist.add(value);
        }
    }


    private static Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("jdbcDriver").required().hasArg().desc("JDBC driver").build());
        options.addOption(Option.builder("jdbcUrl").required().hasArg().desc("JDBC url").build());
        options.addOption(Option.builder("output").required().hasArg().desc("Output filename").build());

        options.addOption(
                Option.builder("catalogueNamePattern").hasArg().desc("Pattern for catalogues to analyze").build());
        options.addOption(
                Option.builder("tablenamePattern").hasArg().desc("Pattern for tables to analyze").build());
        options.addOption(Option.builder("liquibaseChangelog").hasArg().desc("Liquibase changelog file path").build());
        options.addOption(Option.builder("technicalColumns").hasArg()
                .desc("Names of columns that should be treated as technical").build());
        options.addOption(Option.builder("discriminatorColumn").hasArg()
                .desc("Column name for inheritance discriminator").build());
        return options;
    }

}
