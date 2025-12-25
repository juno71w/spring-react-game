SET SESSION cte_max_recursion_depth = 3000000;
INSERT INTO `records` (created_at, updated_at, attempt1, attempt2, attempt3, average_time, name)
WITH RECURSIVE t AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM t WHERE n < 3000000
)
SELECT
    NOW(6),
    NOW(6),
    ROUND(RAND() * 100, 2), -- 0~100 사이의 랜덤값
    ROUND(RAND() * 100, 2),
    ROUND(RAND() * 100, 2),
    0, -- 우선 0으로 입력 후 아래에서 업데이트하거나 직접 계산 가능
    CONCAT('User_', n)     -- 중복 방지를 위한 이름 생성
FROM t;

-- 만약 average_time을 세 시도의 평균으로 맞추고 싶다면?
UPDATE records SET average_time = (attempt1 + attempt2 + attempt3) / 3
WHERE average_time = 0;