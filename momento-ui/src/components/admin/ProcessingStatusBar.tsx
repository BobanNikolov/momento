import { ProcessingStatusResponse } from '../../api'
import StatusBadge from '../common/StatusBadge'

interface ProcessingStatusBarProps {
  status: ProcessingStatusResponse | null
}

export default function ProcessingStatusBar({
  status,
}: ProcessingStatusBarProps) {
  if (!status) return null

  const totalPhotos = Object.values(status.counts).reduce((a, b) => a + b, 0)

  return (
    <div>
      <h4 className="mb-2 text-sm font-medium text-gray-700">
        Photos ({totalPhotos})
      </h4>
      <div className="flex flex-wrap gap-2 text-xs">
        {Object.entries(status.counts).map(([s, count]) => (
          <StatusBadge key={s} status={s} className="px-2 py-1">
            {s}: {count}
          </StatusBadge>
        ))}
      </div>
    </div>
  )
}
