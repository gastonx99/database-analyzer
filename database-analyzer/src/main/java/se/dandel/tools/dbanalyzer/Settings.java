package se.dandel.tools.dbanalyzer;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Settings {

    private String outputFilename;

    private String url;

    private String driver;

    private String user;

    private String password;

    private String liquibaseChangelog;

    private String tablenamePattern;

    private Collection<String> technicalColumns = new ArrayList<>();

    private String discriminatorColumn;

    private String catalogueName;

    private PrintWriter printWriter;

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public void setCatalogueName(String catalogueName) {
        this.catalogueName = catalogueName;
    }

    public String getCatalogueName() {
        return catalogueName;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }
}
