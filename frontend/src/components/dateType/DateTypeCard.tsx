import { DateType } from '@/types/dateType';

interface DateTypeCardProps {
  dateType: DateType;
  isSelected: boolean;
  onSelect: (dateType: DateType) => void;
}

export default function DateTypeCard({
  dateType,
  isSelected,
  onSelect,
}: DateTypeCardProps) {
  return (
    <button
      type="button"
      onClick={() => onSelect(dateType)}
      className={`
        flex flex-col gap-3 rounded-lg border p-5 transition-all duration-200
        ${
          isSelected
            ? 'border-blue-500 bg-blue-50 dark:border-blue-400 dark:bg-blue-950'
            : 'border-gray-200 bg-white hover:border-gray-300 dark:border-gray-700 dark:bg-gray-800 dark:hover:border-gray-600'
        }
      `}
    >
      <div className="flex items-center gap-3">
        <span className="text-3xl">{dateType.icon}</span>
        <h3
          className={`text-lg font-semibold ${
            isSelected
              ? 'text-blue-700 dark:text-blue-300'
              : 'text-gray-900 dark:text-gray-100'
          }`}
        >
          {dateType.name}
        </h3>
      </div>
      <p
        className={`text-left text-sm ${
          isSelected
            ? 'text-blue-600 dark:text-blue-400'
            : 'text-gray-600 dark:text-gray-400'
        }`}
      >
        {dateType.description}
      </p>
    </button>
  );
}
