package se.dandel.tools.dbanalyzer;

import com.google.inject.Guice;
import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(final String[] args) {
        List<String> arglist = args == null ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(args));
        try {
            arglistAddIfNotExists(arglist, "output", "target/output.plantuml");
            addBetmeSettings(arglist);
            Options options = createOptions();
            CommandLine cmd = parseOptions(options, arglist);
            Settings settings = parseSettings(cmd);

            PrintWriter printWriter = new PrintWriter(settings.getOutputFilename());
            settings.setPrintWriter(printWriter);
            DatabaseAnalyzer analyzer = Guice.createInjector().getInstance(DatabaseAnalyzer.class);
            analyzer.analyze(settings);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Settings parseSettings(CommandLine cmd) {
        Settings settings = new Settings();
        settings.setDriver(cmd.getOptionValue("driver"));
        settings.setUrl(cmd.getOptionValue("url"));
        settings.setUser(cmd.getOptionValue("user"));
        settings.setPassword(cmd.getOptionValue("password"));
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
        arglistAddIfNotExists(arglist, "driver", "com.mysql.cj.jdbc.Driver");
        arglistAddIfNotExists(arglist, "url", "jdbc:mysql://localhost:3306/betme");
        arglistAddIfNotExists(arglist, "user", "betme");
        arglistAddIfNotExists(arglist, "password", "Milano93");
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
        options.addOption(Option.builder("driver").required().hasArg().desc("Database driver").build());
        options.addOption(Option.builder("url").required().hasArg().desc("Database url").build());
        options.addOption(Option.builder("user").required().hasArg().desc("Database user").build());
        options.addOption(Option.builder("password").required().hasArg().desc("Database password").build());
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
