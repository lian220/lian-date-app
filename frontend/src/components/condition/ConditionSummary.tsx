import { DateCondition } from '@/types/dateCondition';

interface ConditionSummaryProps {
  condition: DateCondition;
  onEdit?: (field: keyof DateCondition) => void;
}

interface SummaryItemProps {
  label: string;
  value: string | null;
  icon: string;
  onEdit?: () => void;
  isEmpty?: boolean;
}

function SummaryItem({
  label,
  value,
  icon,
  onEdit,
  isEmpty = false,
}: SummaryItemProps) {
  return (
    <div
      className={`
        flex items-center justify-between rounded-lg border p-4
        ${
          isEmpty
            ? 'border-orange-200 bg-orange-50 dark:border-orange-800 dark:bg-orange-950'
            : 'border-gray-200 bg-white dark:border-gray-700 dark:bg-gray-800'
        }
      `}
    >
      <div className="flex items-center gap-3">
        <span className="text-2xl">{icon}</span>
        <div>
          <p className="text-xs text-gray-500 dark:text-gray-400">{label}</p>
          <p
            className={`mt-0.5 font-semibold ${
              isEmpty
                ? 'text-orange-600 dark:text-orange-400'
                : 'text-gray-900 dark:text-gray-100'
            }`}
          >
            {value || '선택되지 않음'}
          </p>
        </div>
      </div>
      {onEdit && (
        <button
          onClick={onEdit}
          className="rounded-full px-3 py-1.5 text-sm font-medium text-blue-600 hover:bg-blue-50 dark:text-blue-400 dark:hover:bg-blue-950"
        >
          수정
        </button>
      )}
    </div>
  );
}

export default function ConditionSummary({
  condition,
  onEdit,
}: ConditionSummaryProps) {
  const isComplete =
    condition.region !== null &&
    condition.dateType !== null &&
    condition.budget !== null;

  return (
    <div className="w-full space-y-3">
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100">
          선택한 조건
        </h2>
        {!isComplete && (
          <span className="rounded-full bg-orange-100 px-3 py-1 text-xs font-semibold text-orange-700 dark:bg-orange-900 dark:text-orange-300">
            필수 항목 미선택
          </span>
        )}
      </div>

      <SummaryItem
        label="지역"
        value={condition.region?.name || null}
        icon="📍"
        onEdit={onEdit ? () => onEdit('region') : undefined}
        isEmpty={!condition.region}
      />

      <SummaryItem
        label="데이트 유형"
        value={
          condition.dateType
            ? `${condition.dateType.icon} ${condition.dateType.name}`
            : null
        }
        icon="💝"
        onEdit={onEdit ? () => onEdit('dateType') : undefined}
        isEmpty={!condition.dateType}
      />

      <SummaryItem
        label="예산 (1인 기준)"
        value={condition.budget?.label || null}
        icon="💰"
        onEdit={onEdit ? () => onEdit('budget') : undefined}
        isEmpty={!condition.budget}
      />

      {condition.specialRequest && (
        <div className="rounded-lg border border-blue-200 bg-blue-50 p-4 dark:border-blue-800 dark:bg-blue-950">
          <div className="flex items-start gap-3">
            <span className="text-2xl">💬</span>
            <div className="flex-1">
              <p className="text-xs text-blue-600 dark:text-blue-400">
                특별 요청사항
              </p>
              <p className="mt-1 text-sm text-gray-900 dark:text-gray-100">
                {condition.specialRequest}
              </p>
            </div>
            {onEdit && (
              <button
                onClick={() => onEdit('specialRequest')}
                className="rounded-full px-3 py-1.5 text-sm font-medium text-blue-600 hover:bg-blue-100 dark:text-blue-400 dark:hover:bg-blue-900"
              >
                수정
              </button>
            )}
          </div>
        </div>
      )}

      {!isComplete && (
        <div className="mt-4 rounded-lg bg-orange-50 p-4 dark:bg-orange-950">
          <div className="flex gap-2">
            <svg
              className="h-5 w-5 flex-shrink-0 text-orange-600 dark:text-orange-400"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              />
            </svg>
            <div className="text-sm text-orange-700 dark:text-orange-300">
              <p className="font-semibold">필수 항목을 모두 선택해주세요</p>
              <p className="mt-1 text-xs">
                지역, 데이트 유형, 예산은 필수 선택 항목입니다
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
