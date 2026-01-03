export function getWeatherMessage(type?: string) {
    switch (type) { case 'sunny':
        return '햇볕이 쨍쨍! 오늘은 기분도 맑게 ☀️';
        case 'cloud':
            return '구름이 살짝 덮였어요, 예쁜 구름을 찾아봐요 ☁️';
        case 'rain':
            return '비가 촉촉히 내려요, 우산 챙기는 거 잊지 마요 ☔️';
        case 'snow':
            return '하얀 눈이 내려요, 발걸음은 조심조심 ❄️';
        case 'thunder':
            return '번개가 번쩍! 실내에서 안전하게 쉬어요 ⚡️';
        default: return '오늘도 행복한 하루 보내세요!';
    }
}
