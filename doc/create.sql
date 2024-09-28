
drop table if exists x_account_info;

create table x_account_info
(
    ID                  bigint auto_increment
        primary key,
    PAYER_NAME          varchar(100)                       not null comment 'Payer account alias/name',
    SITE_ID             varchar(36)                        not null comment '사이트 아이디',
    PAYER_ID            varchar(50)                        not null comment '페이어 아이디',
    TARGET_COV          decimal(18, 1)                     null comment '타겟 커버리지',
    FIX_YN              varchar(1)                         null comment 'Target coverage 고정 여부',
    STATUS              varchar(15)                        null comment '수정 스테이터스',
    LAST_COLLECTION_DAY varchar(16)                        null comment '최종 수집 일자',
    LATEST_UTIL_PERCENT decimal(18, 10)                    null comment '최근 유틸',
    LATEST_COV_PERCENT  decimal(18, 10)                    null comment '최근 커버리지',
    AVG_UTIL_PERCENT    decimal(18, 10)                    null comment '토탈 평균 유틸',
    AVG_COV_PERCENT     decimal(18, 10)                    null comment '토탈 평균 커버리지',
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
    ID                 bigint auto_increment
        primary key,
    SITE_ID            varchar(36)                              not null comment '사이트 아이디',
    PAYER_ID           varchar(50)                              null comment '페이어 아이디',
    TARGET_COV         decimal(18, 1) default 100.0             null comment '타겟 커버리지',
    FIX_YN             varchar(1)     default 'N'               null,
    CREATED_BY         varchar(50)                              not null,
    CREATED_DATE       datetime       default CURRENT_TIMESTAMP not null,
    LAST_MODIFIED_BY   varchar(50)                              not null,
    LAST_MODIFIED_DATE datetime       default CURRENT_TIMESTAMP not null
);

drop table if exists x_account_info_result;

create table x_account_info_result
(
    ID                 bigint auto_increment
        primary key,
    ACCOUNT_ID         bigint                                   not null,
    Transfer_AMOUNT    decimal(18, 2) default 0.00              null comment 'Transfer amount',
    Expected_UTIL      decimal(18, 10)                          null comment 'Expected utilization %',
    Expected_COV       decimal(18, 10)                          null comment 'Expected coverage %',
    Transfer_SCORE     varchar(20)                              null comment 'Transfer score',
    CREATED_DATE       datetime       default CURRENT_TIMESTAMP not null,
    LAST_MODIFIED_DATE datetime       default CURRENT_TIMESTAMP not null,
    constraint fk_account_info
        foreign key (ACCOUNT_ID) references x_account_info (ID)
            on update cascade on delete cascade
);

drop table if exists x_transfer;


create table x_transfer
(
    TRANSFER_ID          bigint auto_increment
        primary key,
    SITE_ID              varchar(36)                              not null comment '사이트 아이디',
    SEND_PAYER_ID        varchar(50)                              not null comment 'Send Payer Account ID',
    RECV_PAYER_ID        varchar(50)                              not null comment 'Recieve Payer Account ID',
    CURRENT_STATE        varchar(50)                              not null comment '현재 상태',
    TARGET_COV           decimal(18, 1)                           null comment '타겟 커버리지',
    SEND_TRANSFER_AMOUNT decimal(18, 2) default 0.00              null comment 'Transfer amount of Sender',
    RECV_TRANSFER_AMOUNT decimal(18, 2) default 0.00              null comment 'Transfer amount of Reciever',
    LNKD_ACC_ID          varchar(50)                              not null comment '계정 아이디',
    CMMT                 decimal(18, 2) default 0.00              null comment 'Commitment Amount',
    CUR_UTIL_PERCENT     decimal(18, 10)                          null comment 'Current Utilizaion %',
    CUR_COV_PERCENT      decimal(18, 10)                          null comment 'Current Coverage %',
    EST_UTIL_PERCENT     decimal(18, 10)                          null comment 'Estimated Utilizaion %',
    EST_COV_PERCENT      decimal(18, 10)                          null comment 'Estimated Coverage %',
    CREATED_DATE         datetime       default CURRENT_TIMESTAMP not null,
    CREATED_BY           varchar(50)                              not null comment '생성자',
    LAST_MODIFIED_DATE   datetime       default CURRENT_TIMESTAMP not null,
    LAST_MODIFIED_BY     varchar(50)                              not null comment '수정자'
)