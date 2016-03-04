package se.dandel.tools.dbanalyzer;

import java.util.Arrays;
import java.util.Collection;

public class Settings {

    private String outputFilename;
    private String jdbcUrl;
    private String jdbcDriver;
    private String liquibaseChangelog;
    private String tablenamePattern;
    private Collection<String> technicalColumns;
    private String discriminatorColumn;

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public void setLiquibaseChangelog(String liquibaseChangelog) {
        this.liquibaseChangelog = liquibaseChangelog;
    }

    public String getLiquibaseChangelog() {
        return liquibaseChangelog;
    }

    public void setTablenamePattern(String tablenamePattern) {
        this.tablenamePattern = tablenamePattern;
    }

    public String getTablenamePattern() {
        return tablenamePattern;
    }

    public Collection<String> getTechnicalColumns() {
        return technicalColumns;
    }

    public void setTechnicalColumns(String techColumnsStr) {
        this.technicalColumns = Arrays.asList(techColumnsStr.split(","));
    }

    public String getDiscriminatorColumn() {
        return discriminatorColumn;
    }

    public void setDiscriminatorColumn(String discriminatorColumn) {
        this.discriminatorColumn = discriminatorColumn;
    }

}