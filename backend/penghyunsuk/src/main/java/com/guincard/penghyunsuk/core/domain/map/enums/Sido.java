package com.guincard.penghyunsuk.core.domain.map.enums;

public enum Sido {

    seoul("서울", 37.5666103, 126.9783882),
    busan("부산", 35.1798160, 129.0750223),
    daegu("대구", 35.8713900, 128.6017630),
    incheon("인천", 37.4559418, 126.7051505),
    gwangju("광주", 35.1600320, 126.8513380),
    daejeon("대전", 36.3504396, 127.3849508),
    ulsan("울산", 35.5394773, 129.3112994),
    sejong("세종", 36.4803512, 127.2894325),
    gyeonggi("경기", 37.4363177, 127.5508020),
    gangwon("강원", 37.8603672, 128.3115261),
    chungbuk("충북", 36.7853718, 127.6551404),
    chungnam("충남", 36.6173379, 126.8453965),
    jeonbuk("전북", 35.6910153, 127.2368291),
    jeonnam("전남", 34.9007274, 126.9571667),
    gyeongbuk("경북", 36.6308397, 128.9625780),
    gyeongnam("경남", 35.4414209, 128.2417453),
    jeju("제주", 33.4273366, 126.5758344);

    private final String displayname;
    private final double lat;
    private final double lon;

    Sido(String displayname, double lat, double lon) {
        this.displayname = displayname;
        this.lat = lat;
        this.lon = lon;
    }

    public String getdisplayname() { return displayname; }
    public double getlat() { return lat; }
    public double getlon() { return lon; }

    public static Sido fromdisplayname(String name) {
        for (Sido v : values()) {
            if (v.displayname.equals(name)) return v;
        }
//        throw new illegalargumentexception("no such Sido: " + name);
        return null;
    }

}
