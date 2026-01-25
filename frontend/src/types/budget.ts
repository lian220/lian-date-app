export type BudgetRangeId = '0-30000' | '30000-50000' | '50000-100000' | '100000-';

export interface BudgetRange {
  id: BudgetRangeId;
  label: string;
  range: string;
  hint: string;
}

export interface BudgetRangesResponse {
  budgetRanges: BudgetRange[];
}
