import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';



// 1. 테스트 설정 (원하는 부하 정도에 따라 조절하세요)
export const options = {
    vus: 10,          // 가상 사용자 수
    duration: '30s',  // 테스트 지속 시간
};

// const BASE_URL = 'http://host.docker.internal:8080/api/v2';
const BASE_URL = 'http://3.39.50.141:8080/api/v2';

export default function () {
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // --- 1. 내 기록 등록하기 (POST) ---
    const uniqueName = `User_${uuidv4()}`;
    const payload = JSON.stringify({
        name: uniqueName,
        attempt1: Math.floor(Math.random() * 100),
        attempt2: Math.floor(Math.random() * 100),
        attempt3: Math.floor(Math.random() * 100),
    });

    const postResponse = http.post(
        `${BASE_URL}/records`, payload, params
    );

    check(postResponse, {
        'POST - 기록 등록 성공 (201 or 200)': (r) => r.status === 201 || r.status === 200,
    });


    sleep(1);
}