import { BudgetRange } from '@/types/budget';

export const MOCK_BUDGET_RANGES: BudgetRange[] = [
  {
    id: '0-30000',
    label: '~3만원',
    range: '3만원 이하',
    hint: '카페, 가벼운 브런치, 공원 산책',
  },
  {
    id: '30000-50000',
    label: '3~5만원',
    range: '3만원 ~ 5만원',
    hint: '맛집 식사, 영화, 베이커리 카페',
  },
  {
    id: '50000-100000',
    label: '5~10만원',
    range: '5만원 ~ 10만원',
    hint: '코스 요리, 특별한 체험, 공연 관람',
  },
  {
    id: '100000-',
    label: '10만원~',
    range: '10만원 이상',
    hint: '파인 다이닝, 프리미엄 체험, 특급 호텔',
  },
];
