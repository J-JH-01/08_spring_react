SELECT * FROM "MEMBER";

-- 관리자 샘플 회원 데이터 삽입

INSERT INTO "MEMBER"
VALUES(SEQ_MEMBER_NO.NEXTVAL, 
       'admin01@kh.or.kr',
       '$2a$10$mQuTt31FyF3uXL2qAkF21eZsPnoQP6zeo9pKCevmsWtGJEOsKtFhu',
--       pass01!임
       '김관리',
       '01098765432',
       NULL,
       NULL,
       DEFAULT,
       DEFAULT,
       2
);

COMMIT;

SELECT MEMBER_NO, MEMBER_EMAIL, MEMBER_NICKNAME, MEMBER_PW, AUTHORITY
FROM "MEMBER"
WHERE MEMBER_EMAIL = 'admin01@kh.or.kr'
AND MEMBER_DEL_FL ='N'
AND AUTHORITY = 2;