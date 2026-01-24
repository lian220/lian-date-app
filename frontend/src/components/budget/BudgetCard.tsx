import { BudgetRange } from '@/types/budget';

interface BudgetCardProps {
  budget: BudgetRange;
  isSelected: boolean;
  onSelect: (budget: BudgetRange) => void;
}

export default function BudgetCard({
  budget,
  isSelected,
  onSelect,
}: BudgetCardProps) {
  return (
    <button
      type="button"
      onClick={() => onSelect(budget)}
      className={`
        flex flex-col gap-2 rounded-lg border p-4 transition-all duration-200
        ${
          isSelected
            ? 'border-blue-500 bg-blue-50 dark:border-blue-400 dark:bg-blue-950'
            : 'border-gray-200 bg-white hover:border-gray-300 dark:border-gray-700 dark:bg-gray-800 dark:hover:border-gray-600'
        }
      `}
    >
      <div className="flex items-baseline justify-between">
        <span
          className={`text-xl font-bold ${
            isSelected
              ? 'text-blue-700 dark:text-blue-300'
              : 'text-gray-900 dark:text-gray-100'
          }`}
        >
          {budget.label}
        </span>
        <span
          className={`text-xs ${
            isSelected
              ? 'text-blue-600 dark:text-blue-400'
              : 'text-gray-500 dark:text-gray-400'
          }`}
        >
          1인 기준
        </span>
      </div>
      <p
        className={`text-left text-sm ${
          isSelected
            ? 'text-blue-600 dark:text-blue-400'
            : 'text-gray-600 dark:text-gray-400'
        }`}
      >
        {budget.hint}
      </p>
    </button>
  );
}
