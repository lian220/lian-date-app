import { DateTypeId } from '@/types/dateType';
import { BudgetRangeId } from '@/types/budget';
import { ApiDateType, ApiBudget } from '@/types/course';

/**
 * 프론트엔드 DateTypeId를 백엔드 ApiDateType으로 매핑
 */
export function mapDateTypeToApi(dateTypeId: DateTypeId): ApiDateType {
  const mapping: Record<DateTypeId, ApiDateType> = {
    romantic: 'CAFE_TOUR',
    activity: 'ACTIVITY',
    food: 'FOOD_TOUR',
    culture: 'CULTURE',
    healing: 'HEALING',
  };

  return mapping[dateTypeId];
}

/**
 * 프론트엔드 BudgetRangeId를 백엔드 ApiBudget으로 매핑
 */
export function mapBudgetToApi(budgetId: BudgetRangeId): ApiBudget {
  const mapping: Record<BudgetRangeId, ApiBudget> = {
    low: 'UNDER_30K',
    medium: '30K_TO_50K',
    high: '50K_TO_100K',
    premium: 'OVER_100K',
  };

  return mapping[budgetId];
}
