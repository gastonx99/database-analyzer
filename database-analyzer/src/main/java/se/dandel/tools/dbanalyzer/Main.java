package se.dandel.tools.dbanalyzer;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
    public static void main(final String[] args) throws IOException {
        try {
            CommandLine cmd = parseOptions(args);
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

    private static CommandLine parseOptions(final String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(Option.builder("jdbcDriver").required().hasArg().desc("JDBC driver").build());
        options.addOption(Option.builder("jdbcUrl").required().hasArg().desc("JDBC url").build());
        options.addOption(
                Option.builder("tablenamePattern").required().hasArg().desc("Pattern for tables to analyze").build());
        options.addOption(Option.builder("output").required().hasArg().desc("Output filename").build());
        options.addOption(Option.builder("liquibaseChangelog").hasArg().desc("Liquibase changelog file path").build());
        options.addOption(Option.builder("technicalColumns").hasArg()
                .desc("Names of columns that should be treated as technical").build());
        options.addOption(Option.builder("discriminatorColumn").hasArg()
                .desc("Column name for inheritance discriminator").build());
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

}
