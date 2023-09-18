-- 테스트 계정
-- TODO: 테스트용이지만 비밀번호가 노출된 데이터 세팅. 개선하는 것이 좋을 지 고민해 보자.
insert into user_account (user_id, user_password, role_types, nickname, email, memo, created_at, created_by, modified_at, modified_by) values
    ('uno', '{noop}asdf1234', 'ADMIN', 'Uno', 'uno@mail.com', 'I am Uno.', now(), 'uno', now(), 'uno'),
    ('uno2', '{noop}asdf1234', 'DEVELOPER','Uno2', 'uno2@mail.com', 'I am Uno2.', now(), 'uno2', now(), 'uno2'),
    ('uno3', '{noop}asdf1234', 'MANAGER,DEVELOPER', 'Uno3', 'uno3@mail.com', 'I am Uno3.', now(), 'uno3', now(), 'uno3'),
    ('uno4', '{noop}asdf1234', 'USER', 'Uno4', 'uno4@mail.com', 'I am Uno4.', now(), 'uno4', now(), 'uno4')
;
