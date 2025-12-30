# JsonNode로 받은 기상 예보 자료 구조 가이드
### 1. JsonNode items의 구조
#### 현재 WeatherController에서 받은 item변수는 다음과 같은 구조로 되어 있음
> [   
{   
"baseDate": "20240702",   
"baseTime": "1700",   
"category": "TMP",   
"fcstDate": "20240703",   
"fcstTime": "0900",   
"fcstValue": "28",   
"nx": 64,   
"ny": 123   
},   
{   
"baseDate": "20240702",   
"baseTime": "1700",   
"category": "SKY",   
"fcstDate": "20240703",   
"fcstTime": "0900",   
"fcstValue": "1",   
"nx": 64,   
"ny": 123   
}   
...   
]   
#### 사용시 items(JSON)에서 필요한 정보를 꺼내오면 됨.

<br>

### 2. 기상청 예보 자료구조
#### 1) 단기 예보 데이터 종류
* POP    강수확률
* PTY    강수형태
* PCP    1시간 강수량
* REH    습도
* SNO    1시간 신적설
* SKY    하늘상태
* TMP    1시간 기온
* TMN    일 최저기온
* TMX    일 최고기온
* UUU    풍속(동서성분)
* VVV    풍속(남북성분)
* WAV    파고
* VEC    풍향
* WSD    풍속
#### 2) 각 데이터가 json 구조로 되어 있음.
> 예시 : POP 데이터의 구조   
{   
baseDate : 20240625    
baseTime : 1400    
category : POP    
fcstDate : 20240625    
fcstTime : 1500    
fcstValue : 20    
nx : 64    
ny : 123    
}    

#### 3) 각 데이터 별 json 구조 및 단위(cm, % 등)는 아래 블로그에서 찾아보길 바람!
> https://codepracticeroom.tistory.com/207