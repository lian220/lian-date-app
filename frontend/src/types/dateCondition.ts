import { Region } from './region';
import { DateType } from './dateType';
import { BudgetRange } from './budget';

export interface DateCondition {
  region: Region | null;
  dateType: DateType | null;
  budget: BudgetRange | null;
  specialRequest: string;
}

export interface DateConditionSummary {
  isComplete: boolean;
  missingFields: string[];
}
