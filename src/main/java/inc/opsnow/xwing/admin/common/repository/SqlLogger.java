package inc.opsnow.xwing.admin.common.repository;

import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;

@ApplicationScoped
public class SqlLogger {

    private static final Logger logger = LoggerFactory.getLogger(SqlLogger.class);

    @ConfigProperty(name = "sql.query.log.sql.view", defaultValue = "false")
    String sqlQueryLog;
    @ConfigProperty(name = "sql.query.log.sql.full", defaultValue = "false")
    String sqlQueryFullLog;

    public void logQuery(String sql) {
        logger.info("SQL: {}", sql);
    }

    public void logQuery(String sql, Tuple parameters) {
        logger.info("SQL: {}", log(sql, parameters));
    }

    public void logQuery(String sql, List<Tuple> parametersList) {
        logger.info("SQL: {}", log(sql, parametersList));
    }

    private String log(String sql, Tuple parameters) {

        if (sqlQueryLog.equals("false")) {
            return "";
        }
        if (sqlQueryFullLog.equals("false")) {
            return sql;
        }

        if (parameters == null) {
            return sql;
        }

        String completedSql = sql;
        for (int i = 0; i < parameters.size(); i++) {
            Object value = parameters.getValue(i);
            String stringValue = convertToString(value);
            stringValue = Matcher.quoteReplacement(stringValue);
            completedSql = completedSql.replaceFirst("\\?", stringValue);
        }
        return completedSql;
    }


    private String log(String sql, List<Tuple> parametersList) {

        StringBuilder completedSql = null;
        try {
            if (sqlQueryLog.equals("false")) {
                return "";
            }
            if (sqlQueryFullLog.equals("false")) {
                return sql;
            }

            if (parametersList == null || parametersList.isEmpty()) {
                return sql;
            }

            completedSql = new StringBuilder(sql);
            for (Tuple parameters : parametersList) {
                for (int i = 0; i < parameters.size(); i++) {
                    Object value = parameters.getValue(i);
                    String stringValue = convertToString(value);
                    stringValue = Matcher.quoteReplacement(stringValue);
                    completedSql = new StringBuilder(completedSql.toString().replaceFirst("\\?", stringValue));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return completedSql.toString();
    }

    private String convertToString(Object value) {

        if (value == null) {
            return "null";
        }
        String result = "";

        if (value instanceof LocalDateTime) {
            result = "'" + value + "'";
        } else if (value instanceof String) {
            result = "'" + value + "'";
        } else if (value instanceof Double) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##########");
            result = decimalFormat.format(value);
        } else {
            result = value.toString();
        }

        return result;
    }

}