package inc.opsnow.xwing.admin.transfer.repository;

import inc.opsnow.xwing.admin.common.repository.RepositoryBase;
import inc.opsnow.xwing.admin.transfer.model.AccountInfo;
import inc.opsnow.xwing.admin.transfer.model.AwsSitePayer;
import inc.opsnow.xwing.admin.transfer.model.TransferStatus;
import inc.opsnow.xwing.admin.transfer.repository.query.DashboardQuery;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import io.vertx.sqlclient.TransactionPropagation;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;

@ApplicationScoped
public class DashboardRepository extends RepositoryBase {


    @ConfigProperty(name = "default.target.coverage", defaultValue = "90")
    String defaultTargetCoverage;


    public Uni<List<AwsSitePayer>> getPayer(MySQLPool pool, String siteId) {   //GET_PAYER
        return findAll(pool, DashboardQuery.GET_PAYER.replaceAll("%defaultTargetCoverage%",defaultTargetCoverage), Tuple.of(siteId), AwsSitePayer.class);
    }

    public Uni<Integer> saveAccountInfo(MySQLPool pool, String siteId, List<AccountInfo> accountInfos) {   //SAVE_PAYER

        List<Tuple> tuples = convertToTupleList(accountInfos, AccountInfo.class);

        return pool.withTransaction(TransactionPropagation.CONTEXT, connection -> {
            return delete(connection, DashboardQuery.DELETE_ACCOUNT_INFO_RESULT, Tuple.of(siteId))
                    .chain(v -> delete(connection, DashboardQuery.DELETE_ACCOUNT_INFO, Tuple.of(siteId)))
                    .chain(v -> saveAll(connection, DashboardQuery.INSERT_ACCOUNT_INFO, tuples));
        });
    }

    public Uni<Integer> getAccountInfoCommitCount(MySQLPool pool, String siteId) {   //GET_ACCOUNT_INFO_COMMIT_COUNT
        return findById(pool, DashboardQuery.GET_ACCOUNT_INFO_COMMIT_COUNT, Tuple.of(siteId))
                .onItem().transform(row -> row.getInteger("cnt"));
    }

    // <-- transaction
    public Uni<String> selectPfx(SqlConnection connection) {   //SELECT_PFX
        return findByIdClass(connection, DashboardQuery.SELECT_PFX, Tuple.tuple(), String.class);
    }

    //GET_TRANSFER_ACCOUNT_STATUS
    public Uni<List<TransferStatus>> getTransferAccountStatus(SqlConnection connection, String pfx, String siteId) {   //GET_TRANSFER_ACCOUNT_STATUS
        return findAll(connection, DashboardQuery.GET_TRANSFER_ACCOUNT_STATUS.replaceAll("%PFX%", pfx) , Tuple.of(siteId), TransferStatus.class);
    }

    // UPDATE_X_ACCOUNT_INFO
    public Uni<Integer> updateAccountInfo(SqlConnection connection, String siteId, List<TransferStatus> txAccountStatusList) {   //UPDATE_X_ACCOUNT_INFO
        String lockedPayerIds = getLockedPayerIds(txAccountStatusList);
        if(lockedPayerIds.isEmpty()) {
            return Uni.createFrom().nullItem();
        }
        return save(connection, DashboardQuery.UPDATE_ACCOUNT_INFO_STATUS_LOCK.replaceAll("%LOCK_PAYER_IDS%",lockedPayerIds), Tuple.of(siteId));
    }

    // UPDATE_X_TRANSFER_ACCOUNT
    public Uni<Integer> updateTransferAccount(SqlConnection connection, String siteId, List<TransferStatus> txAccountStatusList) {   //UPDATE_X_TRANSFER
        String linkedIds = getUnLockedLinkedIds(txAccountStatusList);
        if(linkedIds.isEmpty()) {
            return Uni.createFrom().nullItem();
        }
        return save(connection, DashboardQuery.UPDATE_TRANSFER_STATUS_COMPLETED.replaceAll("%COMPLETED_LNKD_ACC_IDS%",linkedIds), Tuple.of(siteId));
    }
    // --> transaction

    // RESULT='LOCK'인 경우의 PAYER_ID를 콤마로 구분하여 반환
    private String getLockedPayerIds(List<TransferStatus> statusList) {
        Set<String> lockedPayerIds = new HashSet<>();

        for (TransferStatus status : statusList) {
            if ("LOCK".equals(status.getResult())) {
                lockedPayerIds.add("'"+status.getSendPayerId()+"'");
                lockedPayerIds.add("'"+status.getRecvPayerId()+"'");
            }
        }
        return String.join(",", lockedPayerIds);
    }

    // %COMPLETED_LNKD_ACC_IDS%
    private String getUnLockedLinkedIds(List<TransferStatus> statusList) {
        Set<String> LinkedIds = new HashSet<>();

        for (TransferStatus status : statusList) {
            if (!"LOCK".equals(status.getResult())) {
                LinkedIds.add("'"+status.getLnkdAccId()+"'");
            }
        }
        return String.join(",", LinkedIds);
    }

}
