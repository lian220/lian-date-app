import { DateTypeId } from '@/types/dateType';
import { BudgetRangeId } from '@/types/budget';

/**
 * 프론트엔드 DateTypeId를 백엔드가 기대하는 형식으로 그대로 반환
 * (DateTypeId는 이미 백엔드 형식과 동일: "romantic", "activity" 등)
 */
export function mapDateTypeToApi(dateTypeId: DateTypeId): string {
  return dateTypeId;
}

/**
 * 프론트엔드 BudgetRangeId를 백엔드가 기대하는 형식으로 그대로 반환
 * (BudgetRangeId는 이미 백엔드 형식과 동일: "0-30000", "30000-50000" 등)
 */
export function mapBudgetToApi(budgetId: BudgetRangeId): string {
  return budgetId;
}
