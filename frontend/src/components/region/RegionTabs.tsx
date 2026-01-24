import { RegionType } from '@/types/region';

interface RegionTabsProps {
  activeTab: RegionType;
  onTabChange: (tab: RegionType) => void;
}

export default function RegionTabs({
  activeTab,
  onTabChange,
}: RegionTabsProps) {
  const tabs: { type: RegionType; label: string }[] = [
    { type: 'seoul', label: '서울' },
    { type: 'gyeonggi', label: '경기' },
  ];

  return (
    <div className="flex gap-2 border-b border-gray-200 dark:border-gray-700">
      {tabs.map(tab => (
        <button
          key={tab.type}
          onClick={() => onTabChange(tab.type)}
          className={`
            relative px-6 py-3 text-sm font-semibold transition-colors
            ${
              activeTab === tab.type
                ? 'text-blue-600 dark:text-blue-400'
                : 'text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
            }
          `}
        >
          {tab.label}
          {activeTab === tab.type && (
            <span className="absolute bottom-0 left-0 h-0.5 w-full bg-blue-600 dark:bg-blue-400" />
          )}
        </button>
      ))}
    </div>
  );
}
