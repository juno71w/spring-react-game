import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

// 1. 테스트 설정: VUs를 1부터 점진적으로 늘리는 'Ramping' 구조
export const options = {
    stages: [
        { duration: '30s', target: 10 }, // 30초 동안 VU를 1에서 20까지 증가
        { duration: '1m', target: 10 },  // 1분 동안 20 VU 유지
        { duration: '20s', target: 0 },  // 마지막 20초 동안 VU를 0으로 감소 (정리)
    ],
};

const BASE_URL = 'http://3.39.50.141:8080/api/v1';
// const BASE_URL = 'http://host.docker.internal:8080/api/v2';
const testDate = Date.now();

export default function () {
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
        tags: { name: '02_게임_기록_등록' }

    };

    // 고유 사용자 이름 생성
    // const randomNumber = Math.floor(Math.random() * 1000000) + 1;
    const id = uuidv4();

    const uniqueName = `User_${testDate}_${id}`;

    // --- [STEP 1] 상위 10명 랭킹 조회 (모든 유저 수행) ---
    const top10Response = http.get(`${BASE_URL}/records`, {
        tags: { name: '01_상위_10명_랭킹_조회' }
    });

    check(top10Response, {
        '랭킹 조회 성공(200)': (r) => r.status === 200,
    });

    // --- [STEP 2] 30% 확률로 게임 실행 (기록 등록) ---
    const playChance = Math.random(); // 0.0 ~ 1.0 사이 난수

    if (playChance <= 0.3) {
        const payload = JSON.stringify({
            name: uniqueName,
            attempt1: Math.floor(Math.random() * 100),
            attempt2: Math.floor(Math.random() * 100),
            attempt3: Math.floor(Math.random() * 100),
        });

        const postResponse = http.post(`${BASE_URL}/records`, payload, params);

        check(postResponse, {
            '게임 등록 성공(201/200)': (r) => r.status === 201 || r.status === 200,
        });

        // --- [STEP 3] 게임 실행 유저 중 20% 확률로 내 기록 조회 ---
        // (전체 유저 대비 30% * 20% = 6% 확률)
        const myRankChance = Math.random();

        if (myRankChance <= 0.2) {
            const myRankResponse = http.get(
                `${BASE_URL}/records/me?name=${uniqueName}`, {
                    tags: { name: '03_내_랭킹_조회' }
                }
            );

            check(myRankResponse, {
                '내 랭킹 조회 성공(200)': (r) => r.status === 200,
            });
        }
    }

    sleep(1); // 유저 간 작업 간격 조절
}