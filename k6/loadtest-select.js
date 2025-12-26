import http from 'k6/http';
import { check, sleep } from 'k6';

// 1. 테스트 설정 (원하는 부하 정도에 따라 조절하세요)
export const options = {
    vus: 100,          // 가상 사용자 수
    duration: '30s',  // 테스트 지속 시간
};

// const BASE_URL = 'http://host.docker.internal:8080/api/v2';
const BASE_URL = 'http://3.39.50.141:8080/api/v1';

export default function () {
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const randomNumber = Math.floor(Math.random() * 1000000) + 1;
    const uniqueName = `User_${randomNumber}`;

    // --- 2. 상위 10명 랭킹 조회 (GET) ---
    const top10Response = http.get(
        `${BASE_URL}/records`, {
            tags: { name: '상위 10명 랭킹 조회'}
        }
    );

    check(top10Response, {
        'GET - 상위 랭킹 조회 성공 (200)': (r) => r.status === 200,
    });

    // --- 3. 내 랭킹 및 주변 10명 조회 (GET) ---
    const myRankResponse = http.get(
        `${BASE_URL}/records/me?name=${uniqueName}`, {
            tags: { name: '내 랭킹 및 주변 10명 조회'}
        }
    );

    check(myRankResponse, {
        'GET - 내 랭킹 조회 성공 (200)': (r) => r.status === 200,
    });

    sleep(1);
}