package inc.opsnow.xwing.admin.transfer.repository.query;

public class DashboardQuery {

    // x-wing만 대상으로 한정함
    public final static String GET_PAYER = """
            select s.SITE_ID,
                   s.PAYR_ACC_ID,
                   l.ALIAS,
                   t.TARGET_COV
            from bill_new.tbil_cmpn_sbsc_svc_acc_l s
                     join bill_new.tbil_aws_ak_l l on s.PAYR_ACC_ID = l.PAYR_ACC_ID and s.SITE_ID = l.SITE_ID and l.PAYR_YN = 'Y'
                     join cmp_admin_dev.x_setting_target t on s.SITE_ID = t.SITE_ID and s.PAYR_ACC_ID = t.PAYER_ID
            where s.ACC_TYPE = 'PAYER'
              and s.SVC_CD = 'AUTO-SP'
              and s.STAT = 'SUBSCRIBED'
              and s.SITE_ID = ?
            """;

    // 먼저 테이블 삭제
    public final static String DELETE_ACCOUNT_INFO = """
            delete from cmp_admin.x_account_info
            where SITE_ID = ?
            """;

    // 테이블 등록
    public final static String INSERT_ACCOUNT_INFO = """
            insert into cmp_admin.x_account_info (PAYER_NAME, SITE_ID, PAYER_ID, TARGET_COV,
                            LAST_COLLECTION_DAY, P1_UTIL_PERCENT, P1_COV_PERCENT, P2_UTIL_PERCENT, P2_COV_PERCENT,
                            CREATED_DATE, LAST_MODIFIED_DATE)
            values (?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    NOW(), NOW())
            """;
}
