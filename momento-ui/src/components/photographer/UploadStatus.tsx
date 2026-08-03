import { CheckCircle, AlertCircle, Clock, RefreshCw } from 'lucide-react'
import { ProcessingStatusResponse } from '../../api'

interface UploadStatusProps {
  status: ProcessingStatusResponse | null
  onRefresh: () => void
}

export default function UploadStatus({ status, onRefresh }: UploadStatusProps) {
  if (!status) return null

  const totalPhotos = Object.values(status.counts).reduce((a, b) => a + b, 0)
  if (totalPhotos === 0) return null

  return (
    <div className="mb-4 flex flex-wrap items-center gap-2 rounded-lg bg-white p-3 text-xs border">
      <span className="font-medium text-gray-700">Processing:</span>
      {Object.entries(status.counts).map(([s, count]) => {
        const Icon =
          s === 'PROCESSED' ? CheckCircle : s === 'FAILED' ? AlertCircle : Clock
        const iconColor =
          s === 'PROCESSED'
            ? 'text-green-500'
            : s === 'FAILED'
            ? 'text-red-500'
            : 'text-yellow-500'

        return (
          <span
            key={s}
            className="flex items-center gap-1 rounded-full bg-gray-50 px-2 py-0.5"
          >
            <Icon size={12} className={iconColor} /> {s}: {count}
          </span>
        )
      })}
      <button
        onClick={onRefresh}
        className="ml-auto text-gray-400 hover:text-gray-600"
      >
        <RefreshCw size={14} />
      </button>
    </div>
  )
}
