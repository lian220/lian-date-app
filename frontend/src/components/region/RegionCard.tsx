import { Region } from '@/types/region';

interface RegionCardProps {
  region: Region;
  isSelected: boolean;
  onSelect: (region: Region) => void;
}

export default function RegionCard({
  region,
  isSelected,
  onSelect,
}: RegionCardProps) {
  return (
    <button
      onClick={() => onSelect(region)}
      className={`
        flex flex-col gap-2 rounded-lg border p-4 transition-all duration-200
        ${
          isSelected
            ? 'border-blue-500 bg-blue-50 dark:border-blue-400 dark:bg-blue-950'
            : 'border-gray-200 bg-white hover:border-gray-300 dark:border-gray-700 dark:bg-gray-800 dark:hover:border-gray-600'
        }
      `}
    >
      <h3
        className={`text-lg font-semibold ${
          isSelected
            ? 'text-blue-700 dark:text-blue-300'
            : 'text-gray-900 dark:text-gray-100'
        }`}
      >
        {region.name}
      </h3>
      <div className="flex flex-wrap gap-1.5">
        {region.keywords.map(keyword => (
          <span
            key={keyword}
            className={`
              rounded-full px-2.5 py-1 text-xs font-medium
              ${
                isSelected
                  ? 'bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-200'
                  : 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300'
              }
            `}
          >
            {keyword}
          </span>
        ))}
      </div>
    </button>
  );
}
