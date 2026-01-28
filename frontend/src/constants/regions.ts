import { Region } from '@/types/region';

/**
 * Mock 데이터: 백엔드 API (/v1/regions) 응답 형식과 일치
 * 실제 API 연동 시 이 데이터는 API 호출로 대체됨
 */
export const MOCK_REGIONS: Region[] = [
  // 서울 권역 (20개)
  {
    id: 'gangnam',
    name: '강남',
    city: 'seoul',
    description: '서울의 대표 명소 강남',
    centerLat: 37.4979,
    centerLng: 127.0276,
    keywords: ['역삼', '선릉', '삼성', '청담'],
  },
  {
    id: 'seocho',
    name: '서초',
    city: 'seoul',
    description: '서초 지역 명소',
    centerLat: 37.4862,
    centerLng: 127.0075,
    keywords: ['교대', '강남역', '양재', '방배'],
  },
  {
    id: 'songpa',
    name: '송파',
    city: 'seoul',
    description: '송파 지역 명소',
    centerLat: 37.5112,
    centerLng: 127.1001,
    keywords: ['잠실', '석촌', '문정', '가락'],
  },
  {
    id: 'gangdong',
    name: '강동',
    city: 'seoul',
    description: '강동 지역 명소',
    centerLat: 37.5381,
    centerLng: 127.1236,
    keywords: ['천호', '길동', '둔촌', '명일'],
  },
  {
    id: 'gangbuk',
    name: '강북',
    areaType: 'SEOUL',
    keywords: ['수유', '미아', '번동'],
  },
  {
    id: 'seongbuk',
    name: '성북',
    areaType: 'SEOUL',
    keywords: ['성신여대', '보문', '안암'],
  },
  {
    id: 'dobong',
    name: '도봉',
    areaType: 'SEOUL',
    keywords: ['쌍문', '방학', '창동'],
  },
  {
    id: 'nowon',
    name: '노원',
    areaType: 'SEOUL',
    keywords: ['중계', '상계', '월계'],
  },
  {
    id: 'eunpyeong',
    name: '은평',
    areaType: 'SEOUL',
    keywords: ['연신내', '불광', '응암'],
  },
  {
    id: 'seodaemun',
    name: '서대문',
    areaType: 'SEOUL',
    keywords: ['신촌', '이대', '홍제'],
  },
  {
    id: 'mapo',
    name: '마포',
    areaType: 'SEOUL',
    keywords: ['홍대', '합정', '상수', '망원'],
  },
  {
    id: 'yongsan',
    name: '용산',
    areaType: 'SEOUL',
    keywords: ['이태원', '한남', '삼각지'],
  },
  {
    id: 'junggu',
    name: '중구',
    areaType: 'SEOUL',
    keywords: ['명동', '을지로', '회현'],
  },
  {
    id: 'jongno',
    name: '종로',
    areaType: 'SEOUL',
    keywords: ['광화문', '종각', '혜화'],
  },
  {
    id: 'dongdaemun',
    name: '동대문',
    areaType: 'SEOUL',
    keywords: ['청량리', '회기', '신설동'],
  },
  {
    id: 'jungnang',
    name: '중랑',
    areaType: 'SEOUL',
    keywords: ['망우', '상봉', '면목'],
  },
  {
    id: 'gwangjin',
    name: '광진',
    areaType: 'SEOUL',
    keywords: ['건대', '구의', '자양'],
  },
  {
    id: 'seongdong',
    name: '성동',
    areaType: 'SEOUL',
    keywords: ['왕십리', '성수', '행당'],
  },
  {
    id: 'yeongdeungpo',
    name: '영등포',
    areaType: 'SEOUL',
    keywords: ['여의도', '당산', '신길'],
  },
  {
    id: 'guro',
    name: '구로',
    areaType: 'SEOUL',
    keywords: ['구로디지털', '신도림', '가산'],
  },
  // 경기 권역 (10개)
  {
    id: 'suwon',
    name: '수원',
    areaType: 'GYEONGGI',
    keywords: ['인계', '영통', '장안'],
  },
  {
    id: 'seongnam',
    name: '성남',
    areaType: 'GYEONGGI',
    keywords: ['분당', '판교', '정자'],
  },
  {
    id: 'goyang',
    name: '고양',
    areaType: 'GYEONGGI',
    keywords: ['일산', '킨텍스', '화정'],
  },
  {
    id: 'yongin',
    name: '용인',
    areaType: 'GYEONGGI',
    keywords: ['수지', '기흥', '죽전'],
  },
  {
    id: 'bucheon',
    name: '부천',
    areaType: 'GYEONGGI',
    keywords: ['중동', '상동', '역곡'],
  },
  {
    id: 'ansan',
    name: '안산',
    areaType: 'GYEONGGI',
    keywords: ['중앙', '고잔', '선부'],
  },
  {
    id: 'anyang',
    name: '안양',
    areaType: 'GYEONGGI',
    keywords: ['평촌', '범계', '인덕원'],
  },
  {
    id: 'namyangju',
    name: '남양주',
    areaType: 'GYEONGGI',
    keywords: ['다산', '별내', '평내'],
  },
  {
    id: 'hanam',
    name: '하남',
    areaType: 'GYEONGGI',
    keywords: ['미사', '신장', '덕풍'],
  },
  {
    id: 'gwacheon',
    name: '과천',
    areaType: 'GYEONGGI',
    keywords: ['정부청사', '중앙', '별양'],
  },
];
