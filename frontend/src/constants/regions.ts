import { RegionCategory } from '@/types/region';

export const REGION_DATA: RegionCategory[] = [
  {
    type: 'seoul',
    label: '서울',
    regions: [
      {
        id: 'gangnam',
        name: '강남',
        keywords: ['역삼', '선릉', '삼성', '청담'],
      },
      {
        id: 'seocho',
        name: '서초',
        keywords: ['교대', '강남역', '양재', '방배'],
      },
      {
        id: 'songpa',
        name: '송파',
        keywords: ['잠실', '석촌', '문정', '가락'],
      },
      {
        id: 'gangdong',
        name: '강동',
        keywords: ['천호', '길동', '둔촌', '명일'],
      },
      { id: 'gangbuk', name: '강북', keywords: ['수유', '미아', '번동'] },
      { id: 'seongbuk', name: '성북', keywords: ['성신여대', '보문', '안암'] },
      { id: 'dobong', name: '도봉', keywords: ['쌍문', '방학', '창동'] },
      { id: 'nowon', name: '노원', keywords: ['중계', '상계', '월계'] },
      { id: 'eunpyeong', name: '은평', keywords: ['연신내', '불광', '응암'] },
      { id: 'seodaemun', name: '서대문', keywords: ['신촌', '이대', '홍제'] },
      { id: 'mapo', name: '마포', keywords: ['홍대', '합정', '상수', '망원'] },
      { id: 'yongsan', name: '용산', keywords: ['이태원', '한남', '삼각지'] },
      { id: 'junggu', name: '중구', keywords: ['명동', '을지로', '회현'] },
      { id: 'jongno', name: '종로', keywords: ['광화문', '종각', '혜화'] },
      {
        id: 'dongdaemun',
        name: '동대문',
        keywords: ['청량리', '회기', '신설동'],
      },
      { id: 'jungnang', name: '중랑', keywords: ['망우', '상봉', '면목'] },
      { id: 'gwangjin', name: '광진', keywords: ['건대', '구의', '자양'] },
      { id: 'seongdong', name: '성동', keywords: ['왕십리', '성수', '행당'] },
      {
        id: 'yeongdeungpo',
        name: '영등포',
        keywords: ['여의도', '당산', '신길'],
      },
      { id: 'guro', name: '구로', keywords: ['구로디지털', '신도림', '가산'] },
    ],
  },
  {
    type: 'gyeonggi',
    label: '경기',
    regions: [
      { id: 'suwon', name: '수원', keywords: ['인계', '영통', '장안'] },
      { id: 'seongnam', name: '성남', keywords: ['분당', '판교', '정자'] },
      { id: 'goyang', name: '고양', keywords: ['일산', '킨텍스', '화정'] },
      { id: 'yongin', name: '용인', keywords: ['수지', '기흥', '죽전'] },
      { id: 'bucheon', name: '부천', keywords: ['중동', '상동', '역곡'] },
      { id: 'ansan', name: '안산', keywords: ['중앙', '고잔', '선부'] },
      { id: 'anyang', name: '안양', keywords: ['평촌', '범계', '인덕원'] },
      { id: 'namyangju', name: '남양주', keywords: ['다산', '별내', '평내'] },
      { id: 'hanam', name: '하남', keywords: ['미사', '신장', '덕풍'] },
      { id: 'gwacheon', name: '과천', keywords: ['정부청사', '중앙', '별양'] },
    ],
  },
];
