package inc.opsnow.xwing.admin.common.repository;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.List;

public interface Repository {

    // MySQLPool
    Uni<Row> findById(MySQLPool pool, String sql);

    Uni<Tuple> findByIdTuple(MySQLPool pool, String sql);

    <T> Uni<T> findByIdClass(MySQLPool pool, String sql, Class<T> clazz);

    Uni<Row> findById(MySQLPool pool, String sql, Tuple parameters);

    Uni<Tuple> findByIdTuple(MySQLPool pool, String sql, Tuple parameters);

    <T> Uni<T> findByIdClass(MySQLPool pool, String sql, Tuple parameters, Class<T> clazz);

    Uni<Row> findById(SqlConnection connection, String sql);

    Uni<Tuple> findByIdTuple(SqlConnection connection, String sql);

    <T> Uni<T> findByIdClass(SqlConnection connection, String sql, Class<T> clazz);

    Uni<Row> findById(SqlConnection connection, String sql, Tuple parameters);

    Uni<Tuple> findByIdTuple(SqlConnection connection, String sql, Tuple parameters);

    <T> Uni<T> findByIdClass(SqlConnection connection, String sql, Tuple parameters, Class<T> clazz);


    Uni<List<Row>> findAll(MySQLPool pool, String sql);

    Uni<List<Tuple>> findAllTuple(MySQLPool pool, String sql);

    <T> Uni<List<T>> findAll(MySQLPool pool, String sql, Class<T> clazz);

    Uni<List<Row>> findAll(MySQLPool pool, String sql, Tuple parameters);

    Uni<List<Tuple>> findAllTuple(MySQLPool pool, String sql, Tuple parameters);

    <T> Uni<List<T>> findAll(MySQLPool pool, String sql, Tuple parameters, Class<T> clazz);

    // SqlConnection -> for Transaction
    Uni<List<Row>> findAll(SqlConnection connection, String sql);

    Uni<List<Tuple>> findAllTuple(SqlConnection connection, String sql);

    <T> Uni<List<T>> findAll(SqlConnection connection, String sql, Class<T> clazz);

    Uni<List<Row>> findAll(SqlConnection connection, String sql, Tuple parameters);

    Uni<List<Tuple>> findAllTuple(SqlConnection connection, String sql, Tuple parameters);

    <T> Uni<List<T>> findAll(SqlConnection connection, String sql, Tuple parameters, Class<T> clazz);

    Uni<Integer> save(MySQLPool pool, String sql);

    Uni<Integer> save(MySQLPool pool, String sql, Tuple parameters);

    Uni<Integer> save(SqlConnection connection, String sql);

    Uni<Integer> save(SqlConnection connection, String sql, Tuple parameters);

    Uni<Integer> saveAll(MySQLPool pool, String sql, List<Tuple> parametersList);

    Uni<Integer> saveAll(SqlConnection connection, String sql, List<Tuple> parametersList);

    Uni<Void> delete(MySQLPool pool, String sql);

    Uni<Void> delete(MySQLPool pool, String sql, Tuple parameters);

    Uni<Void> delete(SqlConnection connection, String sql);

    Uni<Void> delete(SqlConnection connection, String sql, Tuple parameters);
}
