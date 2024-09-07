
drop table if exists x_account_info;

create table x_account_info
(
    ID                  bigint auto_increment   primary key,
    PAYER_NAME          varchar(100)                       not null comment 'Payer account alias/name',
    SITE_ID             varchar(36)                        not null comment '사이트 아이디',
    PAYER_ID            varchar(50)                        null comment '페이어 아이디',
    TARGET_COV          decimal(2, 1)                      null comment '타겟 커버리지',
    STATUS              varchar(15)                        not null comment '수정 스테이터스',
    LAST_COLLECTION_DAY varchar(12)                        null comment '최종 수집 일자',
    P1_UTIL_PERCENT     decimal(18, 10)                    null comment 'P1 utilization %',
    P1_COV_PERCENT      decimal(18, 10)                    null comment 'P1 coverage %',
    P2_UTIL_PERCENT     decimal(18, 10)                    null comment 'P2 utilization %',
    P2_COV_PERCENT      decimal(18, 10)                    null comment 'P2 coverage %',
    CREATED_DATE        datetime default CURRENT_TIMESTAMP not null,
    LAST_MODIFIED_DATE  datetime default CURRENT_TIMESTAMP not null
);


drop table if exists x_setting_target;

create table x_setting_target
(
    ID                 bigint auto_increment    primary key,
    SITE_ID            varchar(36)                              not null comment '사이트 아이디',
    PAYER_ID           varchar(50)                              null comment '페이어 아이디',
    TARGET_COV         decimal(18, 1) default 100.0             null comment '타겟 커버리지',
    CREATED_BY         varchar(50)                         not null,
    CREATED_DATE       timestamp default CURRENT_TIMESTAMP not null,
    LAST_MODIFIED_BY   varchar(50)                         not null,
    LAST_MODIFIED_DATE timestamp default CURRENT_TIMESTAMP not null
);
