package inc.opsnow.xwing.admin.common.repository;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RepositoryBase implements Repository {

    // Setter 메서드 캐시
    private static final ConcurrentHashMap<Class<?>, Map<String, Method>> SETTER_CACHE = new ConcurrentHashMap<>();
    // 배치 크기 조절 용도
    private static final int BATCH_SIZE = 1000;
    @Inject
    SqlLogger sqlLogger;
    @ConfigProperty(name = "database.bill.name")
    String databaseBillName;
    @ConfigProperty(name = "database.cmp_admin.name")
    String databaseCmpAdminName;

    private static Map<String, Method> cacheSetters(Class<?> clazz) {
        Map<String, Method> setters = new HashMap<>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                String fieldName = method.getName().substring(3);
                fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                setters.put(fieldName, method);
            }
        }
        return setters;
    }

    private String changeSql(String sql) {
        sql = sql.replace(" bill.", " " + databaseBillName + ".");
        sql = sql.replace(" cmp_admin.", " " + databaseCmpAdminName + ".");

        return sql;
    }

    @Override
    public Uni<Row> findById(MySQLPool pool, String sql) {
        return findById(pool, sql, Tuple.tuple());
    }

    @Override
    public Uni<Tuple> findByIdTuple(MySQLPool pool, String sql) {
        return findByIdTuple(pool, sql, Tuple.tuple());
    }

    @Override
    public <T> Uni<T> findByIdClass(MySQLPool pool, String sql, Class<T> clazz) {
        return findByIdClass(pool, sql, Tuple.tuple(), clazz);
    }

    @Override
    public Uni<Row> findById(MySQLPool pool, String sql, Tuple parameters) {
        sql = changeSql(sql);

        String finalSql = sql;
        return executeQuery(pool, sql, parameters)
                .onItem().transform(rowSet -> rowSet.iterator().hasNext() ? rowSet.iterator().next() : null)
                .onItem().invoke(row -> {
                    if (row == null) {
                        Log.info("No data found for query: " + finalSql);
                    }
                });
    }

    @Override
    public Uni<Tuple> findByIdTuple(MySQLPool pool, String sql, Tuple parameters) {
        sql = changeSql(sql);
        String finalSql = sql;
        return executeQuery(pool, sql, parameters)
                .onItem().transform(rowSet -> rowSet.iterator().hasNext() ? mapToTuple(rowSet.iterator().next()) : null)
                .onItem().invoke(tuple -> {
                    if (tuple == null) {
                        Log.info("No data found for query: " + finalSql);
                    }
                });
    }

    @Override
    public <T> Uni<T> findByIdClass(MySQLPool pool, String sql, Tuple parameters, Class<T> clazz) {
        sql = changeSql(sql);
        String finalSql = sql;
        return executeQuery(pool, sql, parameters)
                .onItem().transform(rowSet -> rowSet.iterator().hasNext() ? mapToObj(rowSet.iterator().next(), clazz) : null)
                .onItem().invoke(obj -> {
                    if (obj == null) {
                        Log.info("No data found for query: " + finalSql);
                    }
                });
    }

    @Override
    public Uni<Row> findById(SqlConnection connection, String sql) {
        return findById(connection, sql, Tuple.tuple());
    }

    @Override
    public Uni<Tuple> findByIdTuple(SqlConnection connection, String sql) {
        return findByIdTuple(connection, sql, Tuple.tuple());
    }

    @Override
    public <T> Uni<T> findByIdClass(SqlConnection connection, String sql, Class<T> clazz) {
        return findByIdClass(connection, sql, Tuple.tuple(), clazz);
    }

    @Override
    public Uni<Row> findById(SqlConnection connection, String sql, Tuple parameters) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return connection.preparedQuery(sql)
                .execute(parameters)
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(row -> row)
                .collect().first()
                .onFailure().invoke(e -> Log.error("Error while finding data", e));
    }

    @Override
    public Uni<Tuple> findByIdTuple(SqlConnection connection, String sql, Tuple parameters) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return connection.preparedQuery(sql)
                .execute(parameters)
                .onItem().transform(rowSet -> {
                    if (rowSet.iterator().hasNext()) {
                        Row row = rowSet.iterator().next();
                        return mapToTuple(row);
                    }
                    return null;
                })
                .onFailure().invoke(e -> Log.error("Error while finding data", e));
    }

    @Override
    public <T> Uni<T> findByIdClass(SqlConnection connection, String sql, Tuple parameters, Class<T> clazz) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return connection.preparedQuery(sql)
                .execute(parameters)
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(row -> mapToObj(row, clazz))
                .collect().first()
                .onFailure().invoke(e -> Log.error("Error while finding data", e));
    }

    @Override
    public Uni<List<Row>> findAll(MySQLPool pool, String sql) {
        return findAll(pool, sql, Tuple.tuple());
    }

    @Override
    public Uni<List<Tuple>> findAllTuple(MySQLPool pool, String sql) {
        return findAllTuple(pool, sql, Tuple.tuple());
    }

    @Override
    public <T> Uni<List<T>> findAll(MySQLPool pool, String sql, Class<T> clazz) {
        return findAll(pool, sql, Tuple.tuple(), clazz);
    }

    @Override
    public Uni<List<Row>> findAll(MySQLPool pool, String sql, Tuple parameters) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return pool.preparedQuery(sql)
                .execute(parameters)
                .onItem().transformToMulti(Multi.createFrom()::iterable)
                .collect().asList()
                .onFailure().invoke(e -> Log.error("Error while finding data", e));
    }

    @Override
    public Uni<List<Tuple>> findAllTuple(MySQLPool pool, String sql, Tuple parameters) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return pool.preparedQuery(sql)
                .execute(parameters)
                .onItem().transform(rowSet -> {
                    List<Tuple> tuples = new ArrayList<>();
                    for (Row row : rowSet) {
                        tuples.add(mapToTuple(row));
                    }
                    return tuples;
                })
                .onFailure().invoke(e -> Log.error("Error while finding data", e));
    }

    @Override
    public <T> Uni<List<T>> findAll(MySQLPool pool, String sql, Tuple parameters, Class<T> clazz) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return pool.preparedQuery(sql)
                .execute(parameters)
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(row -> mapToObj(row, clazz))
                .collect().asList()
                .onFailure().invoke(e -> Log.error("Error while finding data", e));
    }

    @Override
    public Uni<List<Row>> findAll(SqlConnection connection, String sql) {
        return findAll(connection, sql, Tuple.tuple());
    }

    @Override
    public Uni<List<Tuple>> findAllTuple(SqlConnection connection, String sql) {
        return findAllTuple(connection, sql, Tuple.tuple());
    }

    @Override
    public <T> Uni<List<T>> findAll(SqlConnection connection, String sql, Class<T> clazz) {
        return findAll(connection, sql, Tuple.tuple(), clazz);
    }

    @Override
    public Uni<List<Row>> findAll(SqlConnection connection, String sql, Tuple parameters) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return connection.preparedQuery(sql)
                .execute(parameters)
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(row -> row)
                .collect().asList()
                .onFailure().invoke(e -> Log.error("Error while finding data", e));
    }

    @Override
    public Uni<List<Tuple>> findAllTuple(SqlConnection connection, String sql, Tuple parameters) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return connection.preparedQuery(sql)
                .execute(parameters)
                .onItem().transform(rowSet -> {
                    List<Tuple> tuples = new ArrayList<>();
                    for (Row row : rowSet) {
                        tuples.add(mapToTuple(row));
                    }
                    return tuples;
                })
                .onFailure().invoke(e -> Log.error("Error while finding data", e));
    }

    @Override
    public <T> Uni<List<T>> findAll(SqlConnection connection, String sql, Tuple parameters, Class<T> clazz) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return connection.preparedQuery(sql)
                .execute(parameters)
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .map(row -> mapToObj(row, clazz))
                .collect().asList()
                .onFailure().invoke(e -> Log.error("Error while finding data", e));
    }

    @Override
    public Uni<Integer> save(MySQLPool pool, String sql) {
        return save(pool, sql, Tuple.tuple());
    }

    @Override
    public Uni<Integer> save(MySQLPool pool, String sql, Tuple parameters) {
        sql = changeSql(sql);
        return executeQuery(pool, sql, parameters)
                .onItem().transform(RowSet::rowCount)
                .onItem().invoke(count -> Log.debug("Affected rows: " + count));
    }

    @Override
    public Uni<Integer> save(SqlConnection connection, String sql) {
        return save(connection, sql, Tuple.tuple());
    }

    @Override
    public Uni<Integer> save(SqlConnection connection, String sql, Tuple parameters) {
        sql = changeSql(sql);
        return executeQuery(connection, sql, parameters)
                .onItem().transform(RowSet::rowCount)
                .onItem().invoke(count -> Log.info("Affected rows: " + count));
    }

    @Override
    public Uni<Integer> saveAll(SqlConnection connection, String sql, List<Tuple> parametersList) {
        sql = changeSql(sql);
        if (parametersList.isEmpty()) {
            return Uni.createFrom().item(0);
        }

        List<List<Tuple>> batches = new ArrayList<>();
        for (int i = 0; i < parametersList.size(); i += BATCH_SIZE) {
            batches.add(parametersList.subList(i, Math.min(i + BATCH_SIZE, parametersList.size())));
        }

        String finalSql = sql;
        return Uni.join().all(batches.stream()
                        .map(batch -> saveBatch(connection, finalSql, batch))
                        .toList())
                .andCollectFailures()
                .onItem().transform(results -> results.stream().mapToInt(Integer::intValue).sum());

    }

    @Override
    public Uni<Integer> saveAll(MySQLPool pool, String sql, List<Tuple> parametersList) {
        sql = changeSql(sql);
        if (parametersList.isEmpty()) {
            return Uni.createFrom().item(0);
        }

        List<List<Tuple>> batches = new ArrayList<>();
        for (int i = 0; i < parametersList.size(); i += BATCH_SIZE) {
            batches.add(parametersList.subList(i, Math.min(i + BATCH_SIZE, parametersList.size())));
        }

        String finalSql = sql;
        return Uni.join().all(batches.stream()
                        .map(batch -> saveBatch(pool, finalSql, batch))
                        .toList())
                .andCollectFailures()
                .onItem().transform(results -> results.stream().mapToInt(Integer::intValue).sum());
    }

    public Uni<Integer> saveBatch(MySQLPool pool, String sql, List<Tuple> parametersList) {
        if (parametersList.isEmpty()) {
            return Uni.createFrom().item(0);
        }
        try {
            String upperSql = sql.toUpperCase();
            boolean isInsertSelect = upperSql.contains("SELECT") && !upperSql.contains("VALUES");

            if (isInsertSelect) {
                // INSERT ... SELECT 구문 처리
                return executeInsertSelectBatch(pool, sql, parametersList);
            } else {
                // INSERT ... VALUES 구문 처리
                return executeInsertValues(pool, sql, parametersList);
            }
        } catch (Exception e) {
            Log.errorf("Error processing SQL: %s. \nSQL: %s", e.getMessage(), sql);
            return Uni.createFrom().failure(e);
        }
    }

    private Uni<Integer> executeInsertSelectBatch(MySQLPool pool, String sql, List<Tuple> parametersList) {
        sql = changeSql(sql);

        // SQL 문을 분석하여 INSERT와 SELECT 부분을 추출
        int selectIndex = sql.toUpperCase().indexOf("SELECT");
        String insertPart = sql.substring(0, selectIndex).trim();
        String selectPart = sql.substring(selectIndex).trim();

        // SELECT 문에서 파라미터 위치 파악
        List<Integer> paramPositions = new ArrayList<>();
        for (int i = 0; i < selectPart.length(); i++) {
            if (selectPart.charAt(i) == '?') {
                paramPositions.add(i);
            }
        }

        // 배치 처리를 위한 Uni 리스트 생성
        List<Uni<Integer>> batchUnis = new ArrayList<>();

        for (int i = 0; i < parametersList.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, parametersList.size());
            List<Tuple> batch = parametersList.subList(i, endIndex);

            StringBuilder batchSql = new StringBuilder(insertPart);
            batchSql.append(" ");
            for (int j = 0; j < batch.size(); j++) {
                if (j > 0) {
                    batchSql.append(" UNION ALL ");
                }
                batchSql.append(selectPart);
            }

            // 모든 파라미터를 하나의 Tuple로 합칩니다.
            Tuple batchParams = Tuple.tuple();
            for (Tuple params : batch) {
                for (int pos = 0; pos < paramPositions.size(); pos++) {
                    Object value = params.getValue(pos);
                    batchParams = batchParams.addValue(value != null ? value : Tuple.JSON_NULL);
                }
            }

            String fullSql = batchSql.toString();
            sqlLogger.logQuery(fullSql, batchParams);
            Log.info("Executing batch SQL: " + fullSql);
            Log.info("Batch parameters: " + batchParams.deepToString());

            Uni<Integer> batchUni = pool.preparedQuery(fullSql)
                    .execute(batchParams)
                    .onItem().transform(RowSet::rowCount)
                    .onItem().invoke(count -> Log.info("Affected rows in batch: " + count))
                    .onFailure().invoke(e -> Log.error("Error while saving batch data", e));

            batchUnis.add(batchUni);
        }

        // 모든 배치 실행 결과를 합산
        return Uni.combine().all().unis(batchUnis)
                .with(results -> results.stream().mapToInt(i -> (Integer) i).sum())
                .onItem().invoke(totalCount -> Log.info("Total affected rows: " + totalCount));
    }

    private Uni<Integer> executeInsertValues(MySQLPool pool, String sql, List<Tuple> parametersList) {
        // VALUES 절만 분리합니다. ON DUPLICATE KEY UPDATE 절이 없을 수 있으므로 조건부로 처리합니다.
        String mainSql = sql.substring(0, sql.toUpperCase().indexOf("VALUES")).trim();
        String valuesClause = sql.substring(sql.toUpperCase().indexOf("VALUES"));
        String onDuplicateKeyUpdate = "";

        int onDuplicateKeyUpdateIndex = sql.toUpperCase().indexOf("ON DUPLICATE KEY UPDATE");
        if (onDuplicateKeyUpdateIndex != -1) {
            valuesClause = sql.substring(sql.toUpperCase().indexOf("VALUES"), onDuplicateKeyUpdateIndex).trim();
            onDuplicateKeyUpdate = sql.substring(onDuplicateKeyUpdateIndex);
        }

        // VALUES 절에서 하드코딩된 값의 위치를 찾습니다.
        List<Integer> hardcodedPositions = new ArrayList<>();
        List<String> hardcodedValues = new ArrayList<>();
        String[] valueParts = valuesClause.substring(valuesClause.indexOf("(") + 1, valuesClause.lastIndexOf(")")).split(",");
        for (int i = 0; i < valueParts.length; i++) {
            String part = valueParts[i].trim();
            if (!part.equals("?")) {
                hardcodedPositions.add(i);
                hardcodedValues.add(part);
            }
        }

        // 새로운 VALUES 절을 생성합니다.
        StringBuilder newValuesClause = new StringBuilder("VALUES ");
        for (int i = 0; i < parametersList.size(); i++) {
            newValuesClause.append("(");
            for (int j = 0; j < valueParts.length; j++) {
                if (hardcodedPositions.contains(j)) {
                    newValuesClause.append(hardcodedValues.get(hardcodedPositions.indexOf(j)));
                } else {
                    newValuesClause.append("?");
                }
                if (j < valueParts.length - 1) {
                    newValuesClause.append(", ");
                }
            }
            newValuesClause.append(")");
            if (i < parametersList.size() - 1) {
                newValuesClause.append(", ");
            }
        }

        // 완성된 SQL을 조립합니다.
        String fullSql = mainSql + " " + newValuesClause + " " + onDuplicateKeyUpdate;

        // 모든 파라미터를 하나의 Tuple로 합칩니다.
        Tuple allParams = Tuple.tuple();
        for (Tuple params : parametersList) {
            int paramIndex = 0;
            for (int i = 0; i < valueParts.length; i++) {
                if (!hardcodedPositions.contains(i)) {
                    Object value = params.getValue(paramIndex++);
                    allParams = allParams.addValue(value != null ? value : Tuple.JSON_NULL);
                }
            }
        }

        sqlLogger.logQuery(fullSql, allParams);
        Log.info("Executing SQL: " + fullSql);
        Log.info("Parameters: " + allParams.deepToString());

        return pool.preparedQuery(fullSql)
                .execute(allParams)
                .onItem().transform(RowSet::rowCount)
                .onItem().invoke(count -> Log.info("Affected rows: " + count))
                .onFailure().invoke(e -> Log.error("Error while saving data", e));
    }

    public Uni<Integer> saveBatch(SqlConnection connection, String sql, List<Tuple> parametersList) {
        sql = changeSql(sql);
        if (parametersList.isEmpty()) {
            return Uni.createFrom().item(0);
        }
        try {
            String upperSql = sql.toUpperCase();
            boolean isInsertSelect = upperSql.contains("SELECT") && !upperSql.contains("VALUES");

            if (isInsertSelect) {
                // INSERT ... SELECT 구문 처리
                return executeInsertSelectBatch(connection, sql, parametersList);
            } else {
                // INSERT ... VALUES 구문 처리
                return executeInsertValues(connection, sql, parametersList);
            }
        } catch (Exception e) {
            Log.errorf("Error processing SQL: %s. \nSQL: %s", e.getMessage(), sql);
            return Uni.createFrom().failure(e);
        }
    }

    private Uni<Integer> executeInsertSelectBatch(SqlConnection connection, String sql, List<Tuple> parametersList) {

        // SQL 문을 분석하여 INSERT와 SELECT 부분을 추출
        int selectIndex = sql.toUpperCase().indexOf("SELECT");
        String insertPart = sql.substring(0, selectIndex).trim();
        String selectPart = sql.substring(selectIndex).trim();

        // SELECT 문에서 파라미터 위치 파악
        List<Integer> paramPositions = new ArrayList<>();
        for (int i = 0; i < selectPart.length(); i++) {
            if (selectPart.charAt(i) == '?') {
                paramPositions.add(i);
            }
        }

        // 배치 처리를 위한 Uni 리스트 생성
        List<Uni<Integer>> batchUnis = new ArrayList<>();

        for (int i = 0; i < parametersList.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, parametersList.size());
            List<Tuple> batch = parametersList.subList(i, endIndex);

            StringBuilder batchSql = new StringBuilder(insertPart);
            batchSql.append(" ");
            for (int j = 0; j < batch.size(); j++) {
                if (j > 0) {
                    batchSql.append(" UNION ALL ");
                }
                batchSql.append(selectPart);
            }

            // 모든 파라미터를 하나의 Tuple로 합칩니다.
            Tuple batchParams = Tuple.tuple();
            for (Tuple params : batch) {
                for (int pos = 0; pos < paramPositions.size(); pos++) {
                    Object value = params.getValue(pos);
                    batchParams = batchParams.addValue(value != null ? value : Tuple.JSON_NULL);
                }
            }

            String fullSql = batchSql.toString();
            sqlLogger.logQuery(fullSql, batchParams);
            Log.debug("Executing batch SQL: " + fullSql);
            Log.debug("Batch parameters: " + batchParams.deepToString());

            Uni<Integer> batchUni = connection.preparedQuery(fullSql)
                    .execute(batchParams)
                    .onItem().transform(RowSet::rowCount)
                    .onItem().invoke(count -> Log.info("Affected rows in batch: " + count))
                    .onFailure().invoke(e -> Log.error("Error while saving batch data", e));

            batchUnis.add(batchUni);
        }

        // 모든 배치 실행 결과를 합산
        return Uni.combine().all().unis(batchUnis)
                .with(results -> results.stream().mapToInt(i -> (Integer) i).sum())
                .onItem().invoke(totalCount -> Log.info("Total affected rows: " + totalCount));
    }

    private Uni<Integer> executeInsertValues(SqlConnection connection, String sql, List<Tuple> parametersList) {
        // VALUES 절만 분리합니다. ON DUPLICATE KEY UPDATE 절이 없을 수 있으므로 조건부로 처리합니다.
        String mainSql = sql.substring(0, sql.toUpperCase().indexOf("VALUES")).trim();
        String valuesClause = sql.substring(sql.toUpperCase().indexOf("VALUES"));
        String onDuplicateKeyUpdate = "";

        int onDuplicateKeyUpdateIndex = sql.toUpperCase().indexOf("ON DUPLICATE KEY UPDATE");
        if (onDuplicateKeyUpdateIndex != -1) {
            valuesClause = sql.substring(sql.toUpperCase().indexOf("VALUES"), onDuplicateKeyUpdateIndex).trim();
            onDuplicateKeyUpdate = sql.substring(onDuplicateKeyUpdateIndex);
        }

        // VALUES 절에서 하드코딩된 값의 위치를 찾습니다.
        List<Integer> hardcodedPositions = new ArrayList<>();
        List<String> hardcodedValues = new ArrayList<>();
        String[] valueParts = valuesClause.substring(valuesClause.indexOf("(") + 1, valuesClause.lastIndexOf(")")).split(",");
        for (int i = 0; i < valueParts.length; i++) {
            String part = valueParts[i].trim();
            if (!part.equals("?")) {
                hardcodedPositions.add(i);
                hardcodedValues.add(part);
            }
        }

        // 새로운 VALUES 절을 생성합니다.
        StringBuilder newValuesClause = new StringBuilder("VALUES ");
        for (int i = 0; i < parametersList.size(); i++) {
            newValuesClause.append("(");
            for (int j = 0; j < valueParts.length; j++) {
                if (hardcodedPositions.contains(j)) {
                    newValuesClause.append(hardcodedValues.get(hardcodedPositions.indexOf(j)));
                } else {
                    newValuesClause.append("?");
                }
                if (j < valueParts.length - 1) {
                    newValuesClause.append(", ");
                }
            }
            newValuesClause.append(")");
            if (i < parametersList.size() - 1) {
                newValuesClause.append(", ");
            }
        }

        // 완성된 SQL을 조립합니다.
        String fullSql = mainSql + " " + newValuesClause + " " + onDuplicateKeyUpdate;

        // 모든 파라미터를 하나의 Tuple로 합칩니다.
        Tuple allParams = Tuple.tuple();
        for (Tuple params : parametersList) {
            int paramIndex = 0;
            for (int i = 0; i < valueParts.length; i++) {
                if (!hardcodedPositions.contains(i)) {
                    Object value = params.getValue(paramIndex++);
                    allParams = allParams.addValue(value != null ? value : Tuple.JSON_NULL);
                }
            }
        }

        sqlLogger.logQuery(fullSql, allParams);
        Log.debug("Executing SQL: " + fullSql);
        Log.debug("Parameters: " + allParams.deepToString());

        return connection.preparedQuery(fullSql)
                .execute(allParams)
                .onItem().transform(RowSet::rowCount)
                .onItem().invoke(count -> Log.info("Affected rows: " + count))
                .onFailure().invoke(e -> Log.error("Error while saving data", e));
    }

    @Override
    public Uni<Void> delete(MySQLPool pool, String sql) {
        return delete(pool, sql, Tuple.tuple());
    }

    @Override
    public Uni<Void> delete(MySQLPool pool, String sql, Tuple parameters) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return pool.preparedQuery(sql)
                .execute(parameters)
                .onItem().transform(rowSet -> null)
                .onFailure().invoke(e -> Log.error("Error while deleting data", e)).replaceWithVoid();
    }

    @Override
    public Uni<Void> delete(SqlConnection connection, String sql) {
        return delete(connection, sql, Tuple.tuple());
    }

    @Override
    public Uni<Void> delete(SqlConnection connection, String sql, Tuple parameters) {
        sql = changeSql(sql);
        sqlLogger.logQuery(sql, parameters);
        return connection.preparedQuery(sql)
                .execute(parameters)
                .onItem().transform(rowSet -> null)
                .onFailure().invoke(e -> Log.error("Error while deleting data", e)).replaceWithVoid();
    }

    public <T> Tuple convertToTuple(T object, Class<T> clazz) {
        Tuple tuple = Tuple.tuple();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(object);
                if (value == null) {
                    // null 값인 경우 처리 로직 추가
//                    if (field.getType().isPrimitive()) {
//                        // 기본 타입인 경우 기본값 할당
//                        value = getDefaultValue(field.getType());
//                    } else {
                    // 참조 타입인 경우 null 값 그대로 사용
                    value = null;
//                    }
                } else {
                    tuple.addValue(value);
                }
            } catch (IllegalAccessException e) {
                // 예외 처리
                e.printStackTrace();
            }
        }

        return tuple;
    }

    public <T> List<Tuple> convertToTupleList(List<T> objectList, Class<T> clazz) {

        if (objectList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Tuple> tupleList = new ArrayList<>();

        for (T object : objectList) {
            tupleList.add(convertToTuple(object, clazz));
        }

        return tupleList;
    }

    private Tuple mapToTuple(Row row) {
        int columnCount = row.size();
        List<Object> values = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            values.add(row.getValue(i));
        }
        return Tuple.wrap(values);
    }

    private Uni<RowSet<Row>> executeQuery(MySQLPool pool, String sql, Tuple parameters) {
        sqlLogger.logQuery(sql, parameters);
        return pool.preparedQuery(sql)
                .execute(parameters)
                .onFailure().invoke(e -> Log.error("Error executing query: " + sql, e));
    }

    private Uni<RowSet<Row>> executeQuery(SqlConnection connection, String sql, Tuple parameters) {
        sqlLogger.logQuery(sql, parameters);
        return connection.preparedQuery(sql)
                .execute(parameters)
                .onFailure().invoke(e -> Log.error("Error executing query: " + sql, e));
    }

    private <T> T mapToObj(Row row, Class<T> clazz) {
        Log.debugf("Starting mapToObj for class: %s", clazz.getName());
        try {
            // String 클래스에 대한 특별 처리
            if (clazz.equals(String.class)) {
                String columnName = row.getColumnName(0); // 첫 번째 컬럼 이름 가져오기
                String value = row.getString(columnName);
                Log.debugf("Mapped String value: %s from column: %s", value, columnName);
                return (T) value;
            }

            T obj = clazz.getDeclaredConstructor().newInstance();
            Map<String, Method> setters = SETTER_CACHE.computeIfAbsent(clazz, RepositoryBase::cacheSetters);
            Log.debugf("Setters cache size: %d", setters.size());

            for (Field field : clazz.getDeclaredFields()) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                String columnName = (columnAnnotation != null) ? columnAnnotation.value() : field.getName().toUpperCase();
                Method setter = setters.get(field.getName());

                if (setter != null) {
                    Log.debugf("Processing field: %s, column: %s", field.getName(), columnName);
                    try {
                        if (getColumnIndex(row, columnName) >= 0) {
                            Class<?> paramType = setter.getParameterTypes()[0];
                            Object value = getValueFromRow(row, columnName, paramType);

                            Log.debugf("Retrieved value: %s for column: %s", value, columnName);

                            if (value != null) {
                                setter.invoke(obj, value);
                                Log.debugf("Set %s to %s for column %s", setter.getName(), value, columnName);
                            } else {
                                Log.infof("Null value for column %s of type %s", columnName, paramType.getName());
                            }
                        } else {
                            Log.warnf("Column not found: %s", columnName);
                        }
                    } catch (Exception e) {
                        Log.errorf(e, "Error processing column: %s", columnName);
                    }
                }
            }

            Log.debugf("Finished mapToObj for class: %s", clazz.getName());
            return obj;
        } catch (Exception e) {
            Log.errorf(e, "Critical error in mapToObj for class: %s", clazz.getName());
            throw new RuntimeException("Error mapping row to object", e);
        }
    }

    private int getColumnIndex(Row row, String columnName) {
        for (int i = 0; i < row.size(); i++) {
            if (row.getColumnName(i).equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private Object getValueFromRow(Row row, String columnName, Class<?> paramType) {
        Log.debugf("Getting value for column: %s, type: %s", columnName, paramType.getName());
        try {
            if (String.class.equals(paramType)) {
                return row.getString(columnName);
            } else if (Integer.class.equals(paramType) || int.class.equals(paramType)) {
                return row.getInteger(columnName);
            } else if (Long.class.equals(paramType) || long.class.equals(paramType)) {
                return row.getLong(columnName);
            } else if (Double.class.equals(paramType) || double.class.equals(paramType)) {
                return row.getDouble(columnName);
            } else if (Boolean.class.equals(paramType) || boolean.class.equals(paramType)) {
                return row.getBoolean(columnName);
            } else if (Date.class.equals(paramType)) {
                return row.getLocalDateTime(columnName);
            } else if (Character.class.equals(paramType) || char.class.equals(paramType)) {
                String value = row.getString(columnName);
                return (value != null && !value.isEmpty()) ? value.charAt(0) : null;
            }
            Log.warnf("Unsupported type: %s for column: %s", paramType.getName(), columnName);
            return null;
        } catch (Exception e) {
            Log.errorf(e, "Error getting value for column: %s, type: %s", columnName, paramType.getName());
            return null;
        }
    }
}