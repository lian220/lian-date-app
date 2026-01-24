export type BudgetRangeId = 'low' | 'medium' | 'high' | 'premium';

export interface BudgetRange {
  id: BudgetRangeId;
  label: string;
  range: string;
  hint: string;
}

export interface BudgetRangesResponse {
  budgetRanges: BudgetRange[];
}
