package inc.opsnow.xwing.admin.transfer.repository.query;

public class DashboardQuery {

    // x-wing만 대상으로 한정함
    public final static String GET_PAYER = """
        select s.SITE_ID,
               s.PAYR_ACC_ID,
               l.ALIAS,
               COALESCE(t.TARGET_COV, 100) as TARGET_COV, 
               COALESCE(t.FIX_YN, 'N') as FIX_YN 
        from bill.tbil_cmpn_sbsc_svc_acc_l s
                 join bill.tbil_aws_ak_l l on s.PAYR_ACC_ID = l.PAYR_ACC_ID and s.SITE_ID = l.SITE_ID and l.PAYR_YN = 'Y'
                 left join cmp_admin.x_setting_target t on s.SITE_ID = t.SITE_ID and s.PAYR_ACC_ID = t.PAYER_ID
        where s.ACC_TYPE = 'PAYER'
          and s.SVC_CD = 'AUTO-SP'
          and s.STAT = 'SUBSCRIBED'
          and s.SITE_ID =  ?
        """;

    // 먼저 테이블 삭제
    public final static String DELETE_ACCOUNT_INFO_RESULT = """
            delete from cmp_admin.x_account_info_result
            where ACCOUNT_ID IN (select ID from cmp_admin.x_account_info where SITE_ID = ?)
            """;
    public final static String DELETE_ACCOUNT_INFO = """
            delete from cmp_admin.x_account_info
            where SITE_ID = ?
            """;

    // 테이블 등록
    public final static String INSERT_ACCOUNT_INFO = """
            insert into cmp_admin.x_account_info (PAYER_NAME, SITE_ID, PAYER_ID, TARGET_COV, FIX_YN,
                            LAST_COLLECTION_DAY, LATEST_UTIL_PERCENT, LATEST_COV_PERCENT, AVG_UTIL_PERCENT, AVG_COV_PERCENT,
                            P1_UTIL_PERCENT, P1_COV_PERCENT, P2_UTIL_PERCENT, P2_COV_PERCENT,
                            CREATED_DATE, LAST_MODIFIED_DATE)
            values (?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?,
                    NOW(), NOW())
            """;

    // 커밋 상태인 데이터만 조회하여 대상과 갯수 비교 -> 일치하면 Engine ECS 종료
    public final static String GET_ACCOUNT_INFO_COMMIT_COUNT = """
            select count(0) cnt from cmp_admin.x_account_info where SITE_ID=? and STATUS='COMMIT'
            """;

    // select PFX from bill_new.tbil_pfx_h;
    public final static String SELECT_PFX = """
            select PFX from bill.tbil_pfx_h
            """;
// TODO
//    후처리 : 어카운트가 이동 후 정상적으로 수집을 시작하였는지 확인
//    1) x_transfer 테이블에서 이동한 어카운트에 대해 수집된 데이터가 있는지 확인
//    2) 수집되지 않으면 x_account_info의 status = 'LOCK' 으로 업데이트
//       수집되었으면 유지, 변경 없음 (status = 'COMMIT')
//    3) status = 'LOCK' 이 아니면 x_transfer 테이블의 CURRENT_STATE를 Completed 로 업데이트 한다.


    //    1) x_transfer 테이블에서 이동한 어카운트에 대해 수집된 데이터가 있는지 확인 (SEND_PAYER_ID, RECV_PAYER_ID, LNKD_ACC_ID, RESULT)
    public final static String GET_TRANSFER_ACCOUNT_STATUS = """
        SELECT t.SEND_PAYER_ID,
               t.RECV_PAYER_ID,
               t.LNKD_ACC_ID,
               CASE
                   WHEN COUNT(DISTINCT CASE
                                           WHEN t.CURRENT_STATE = 'Transferred'
                                               AND t.LAST_MODIFIED_DATE < STR_TO_DATE(u.USE_DT, '%Y%m%d')
                                               THEN t.LNKD_ACC_ID
                       END) = 0 THEN 'LOCK'
                   ELSE NULL
                   END AS RESULT
        FROM cmp_admin_dev.x_transfer t
                 LEFT JOIN
             bill.%PFX%_tbil_sp_utl_l u ON (t.SEND_PAYER_ID = u.PAYR_ACC_ID OR t.RECV_PAYER_ID = u.PAYR_ACC_ID)
                 AND t.LNKD_ACC_ID = u.LNKD_ACC_ID
        WHERE t.SITE_ID = ?
        GROUP BY t.SEND_PAYER_ID, t.RECV_PAYER_ID, t.LNKD_ACC_ID
        """;

    //    2) 수집되지 않으면 x_account_info의 status = 'LOCK' 으로 업데이트
    // %LOCK_PAYER_IDS% : 1)에서 result가 'LOCK'인 PAYR_ACC_ID -> 동일 페이어에 대해 LOCK 이 하나라도 있으면 LOCK 처리
    public final static String UPDATE_ACCOUNT_INFO_STATUS_LOCK = """
        UPDATE cmp_admin.x_account_info
        SET STATUS = 'LOCK'
        WHERE SITE_ID = ?
        AND PAYER_ID IN (%LOCK_PAYER_IDS%)
        AND STATUS = 'COMMIT'
        """;

    //    3) status = 'LOCK' 이 아니면 x_transfer 테이블의 CURRENT_STATE를 Completed 로 업데이트 한다.
    // %PAYER_IDS% : 1)에서 result가 'LOCK'이 아닌 PAYR_ACC_ID 중 SEND_PAYER_ID, RECV_PAYER_ID 를 모두 만족하면... (AND 조건) CURRENT_STATE를 Completed 로 업데이트 한다.

    public final static String UPDATE_TRANSFER_STATUS_COMPLETED = """
        UPDATE cmp_admin.x_transfer
        SET CURRENT_STATE = 'Completed'
        WHERE SITE_ID = ?
        AND LNKD_ACC_ID in (%COMPLETED_LNKD_ACC_IDS%) 
        """;

}
