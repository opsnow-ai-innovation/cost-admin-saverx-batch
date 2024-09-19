package inc.opsnow.xwing.admin.transfer.repository;

import inc.opsnow.xwing.admin.common.repository.RepositoryBase;
import inc.opsnow.xwing.admin.transfer.model.AccountInfo;
import inc.opsnow.xwing.admin.transfer.model.AwsSitePayer;
import inc.opsnow.xwing.admin.transfer.repository.query.DashboardQuery;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Tuple;
import io.vertx.sqlclient.TransactionPropagation;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class DashboardRepository extends RepositoryBase {

    public Uni<List<AwsSitePayer>> getPayer(MySQLPool pool, String siteId) {   //GET_PAYER
        return findAll(pool, DashboardQuery.GET_PAYER, Tuple.of(siteId), AwsSitePayer.class);
    }

    public Uni<Integer> saveAccountInfo(MySQLPool pool, String siteId, List<AccountInfo> accountInfos) {   //SAVE_PAYER

        List<Tuple> tuples = convertToTupleList(accountInfos, AccountInfo.class);

        return pool.withTransaction(TransactionPropagation.CONTEXT, connection -> {
            return delete(connection, DashboardQuery.DELETE_ACCOUNT_INFO_RESULT, Tuple.of(siteId))
                    .chain(v -> delete(connection, DashboardQuery.DELETE_ACCOUNT_INFO, Tuple.of(siteId)))
                    .chain(v -> saveAll(connection, DashboardQuery.INSERT_ACCOUNT_INFO, tuples));
        });
    }

}
