package ru.clevertec.listener;

import liquibase.Liquibase;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.clevertec.db.DatabaseConnection;
import ru.clevertec.util.YamlReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;

@WebListener
public class DBMigration implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (YamlReader.getInitialize()) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
                updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database);
                updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "db/changelog/changelog.xml");
                updateCommand.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
