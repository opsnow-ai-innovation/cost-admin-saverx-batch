package inc.opsnow.xwing.admin.transfer.service;

import inc.opsnow.xwing.admin.transfer.external.AwsPayerAccountSummaryService;
import inc.opsnow.xwing.admin.transfer.external.dto.GetAwsPayerAccountSummaryResponse;
import inc.opsnow.xwing.admin.transfer.model.AccountInfo;
import inc.opsnow.xwing.admin.transfer.model.AwsSitePayer;
import inc.opsnow.xwing.admin.transfer.repository.DashboardRepository;
import io.quarkus.logging.Log;
import io.quarkus.reactive.datasource.ReactiveDataSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.sqlclient.TransactionPropagation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Duration;
import java.util.List;


@ApplicationScoped
public class DashboardService {

    private static final int BATCH_SIZE = 10; // 배치 크기를 줄임
    private static final int MAX_RETRIES = 5; // 재시도 횟수 증가
    private static final Duration RETRY_DELAY = Duration.ofSeconds(30); // 재시도 간격 증가
    private static final Duration TIMEOUT = Duration.ofMinutes(2); // 타임아웃 시간 증가
    @Inject
    @ReactiveDataSource("billnew")
    MySQLPool billPool;
    @ConfigProperty(name = "analyzeTerm", defaultValue = "7")
    Integer analyzeTerm;
    @Inject
    DashboardRepository dashboardRepository;
    @RestClient
    AwsPayerAccountSummaryService summaryService;

    public Uni<Integer> getPayerAndTestTimeout(String siteId) {
        return getPayer(siteId)
                .onItem().transformToMulti(payers -> Multi.createFrom().iterable(payers))
                .group().intoLists().of(BATCH_SIZE)
                .onItem().transformToUniAndMerge(this::processBatch)
                .collect().asList()
                .onItem().transform(list -> list.stream()
                        .flatMap(List::stream)
                        .toList())
                .invoke(list -> Log.infof("Finished processing %d payers, endTime: %s", list.size(), System.currentTimeMillis()))
                .chain(list -> dashboardRepository.saveAccountInfo(billPool, siteId, list))
                .invoke(() -> Log.info("Finished saving account info"))
                ;
    }

    public Uni<Integer> getAccountInfoNormalCommitCount(String siteId) {
        return dashboardRepository.getAccountInfoNormalCommitCount(billPool, siteId);
    }
    public Uni<Integer> getAccountInfoOptimCommitCount(String siteId) {
        return dashboardRepository.getAccountInfoOptimCommitCount(billPool, siteId);
    }


    public Uni<Void> updateStatus(String siteId) {
        return billPool.withTransaction(TransactionPropagation.CONTEXT, connection -> {
            return dashboardRepository.selectPfx(connection)
                    .onItem().transformToUni(pfx -> dashboardRepository.getTransferAccountStatus(connection, pfx, siteId))
                    .chain(txAccountStatusList -> dashboardRepository.updateAccountInfo(connection, siteId, txAccountStatusList)
                            .chain(v -> dashboardRepository.updateTransferAccount(connection, siteId, txAccountStatusList)))
                    ;
        }).replaceWithVoid();
    }

    private Uni<List<AwsSitePayer>> getPayer(String siteId) {
        return dashboardRepository.getPayer(billPool, siteId);
    }

    private Uni<List<AccountInfo>> processBatch(List<AwsSitePayer> batch) {
        return Multi.createFrom().iterable(batch)
                .onItem().transformToUniAndMerge(payer -> getAwsPayerAccountSummary(payer)
                        .chain(this::updateAwsPayerAccountSummary))
                .onFailure().retry().withBackOff(RETRY_DELAY).atMost(MAX_RETRIES)
                .collect().asList();
    }

    private Uni<AccountInfo> getAwsPayerAccountSummary(AwsSitePayer payer) {
        Log.infof("Starting get AwsPayerAccountSummary for payer: %s, start: %s", payer, System.currentTimeMillis());

        return summaryService.getByIdAsync(payer.getSiteId(), payer.getPayerAccId(), analyzeTerm)
                .ifNoItem().after(TIMEOUT).fail()
                .onFailure().invoke(e -> Log.error("Request failed for payer " + payer + ": " + e.getMessage()))
                .onItem().invoke(item -> Log.info("Received response for payer " + payer + ": " + item.getStatus()))
                .map(item -> {
                    try {
                        return mapFromSummary(payer, item);
                    } catch (Exception e) {
                        Log.error("Failed to map response to AccountInfo", e);
                        throw new RuntimeException(e);
                    }
                })
                ;
    }

    private AccountInfo mapFromSummary(AwsSitePayer payer, GetAwsPayerAccountSummaryResponse accountSummary) throws Exception {

        Log.info(accountSummary.toString());

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setPayerName(payer.getAlias());
        accountInfo.setCmpnId(payer.getCmpnId());
        accountInfo.setCmpnNm(payer.getCmpnNm());
        accountInfo.setSiteId(accountSummary.getSiteCode());
        accountInfo.setPayerId(accountSummary.getPayerAccountId());
        accountInfo.setTargetCov(payer.getTargetCov());
        accountInfo.setFixYn(payer.getFixYn());
        accountInfo.setLastCollectionDay(accountSummary.getLastUseDate());

        if (accountSummary.getLatest() != null && accountSummary.getLatest().getUtilization() != null) {
            accountInfo.setLatestUtilPercent(accountSummary.getLatest().getUtilization().getTotalUtilization());
        } else {
            accountInfo.setLatestUtilPercent(0.0);
        }
        if (accountSummary.getLatest() != null && accountSummary.getLatest().getCoverage() != null) {
            accountInfo.setLatestCovPercent(accountSummary.getLatest().getCoverage().getTotalCoverage());
        } else {
            accountInfo.setLatestCovPercent(0.0);
        }

        if (accountSummary.getData().get("p1") != null && accountSummary.getData().get("p1").getUtilization() != null) {
            accountInfo.setP1UtilPercent(accountSummary.getData().get("p1").getUtilization().getTotalUtilization());
        } else {
            accountInfo.setP1UtilPercent(0.0);
        }
        if (accountSummary.getData().get("p1") != null && accountSummary.getData().get("p1").getCoverage() != null) {
            accountInfo.setP1CovPercent(accountSummary.getData().get("p1").getCoverage().getTotalCoverage());
        } else {
            accountInfo.setP1CovPercent(0.0);
        }
        if (accountSummary.getData().get("p2") != null && accountSummary.getData().get("p2").getUtilization() != null) {
            accountInfo.setP2UtilPercent(accountSummary.getData().get("p2").getUtilization().getTotalUtilization());
        } else {
            accountInfo.setP2UtilPercent(0.0);
        }
        if (accountSummary.getData().get("p2") != null && accountSummary.getData().get("p2").getCoverage() != null) {
            accountInfo.setP2CovPercent(accountSummary.getData().get("p2").getCoverage().getTotalCoverage());
        } else {
            accountInfo.setP2CovPercent(0.0);
        }

        return accountInfo;
    }


    private Uni<AccountInfo> updateAwsPayerAccountSummary(AccountInfo accountInfo) {
        return summaryService.getByIdAsync(accountInfo.getSiteId(), accountInfo.getPayerId(), analyzeTerm * 2, 1)
                .ifNoItem().after(TIMEOUT).fail()
                .onFailure().invoke(e -> Log.error("Request failed for payer " + accountInfo.getPayerId() + ": " + e.getMessage()))
                .onItem().invoke(item -> Log.info("Received response for payer " + accountInfo.getPayerId() + ": " + item.getStatus()))
                .map(item -> {
                    try {

                        Log.infof("Received response for payer %s: %s", accountInfo.getPayerId(), item.toString());
                        if (item.getData().get("p1").getUtilization() == null || item.getData().get("p1").getCoverage() == null) {
                            Log.errorf("Missing data for payer %s: %s", accountInfo.getPayerId(), item.toString());
                            return accountInfo;
                        }

                        accountInfo.setAvgUtilPercent(item.getData().get("p1").getUtilization().getTotalUtilization());
                        accountInfo.setAvgCovPercent(item.getData().get("p1").getCoverage().getTotalCoverage());

                        return accountInfo;
                    } catch (Exception e) {
                        Log.error("Failed to map response to AccountInfo", e);
                        throw new RuntimeException(e);
                    }
                });
    }
}