import { CheckCircle, AlertCircle, Clock, X } from 'lucide-react'
import { UploadFile } from '../../hooks/usePhotoUpload'

interface FileListProps {
  uploads: UploadFile[]
  onRemove: (index: number) => void
  onClearCompleted: () => void
}

export default function FileList({
  uploads,
  onRemove,
  onClearCompleted,
}: FileListProps) {
  if (uploads.length === 0) return null

  const doneCount = uploads.filter((u) => u.status === 'done').length
  const pendingCount = uploads.filter(
    (u) => u.status === 'pending' || u.status === 'error'
  ).length

  return (
    <div className="mb-4">
      <div className="mb-2 flex items-center justify-between text-sm">
        <span className="text-gray-600">
          {uploads.length} file(s) — {doneCount} uploaded, {pendingCount}{' '}
          pending
        </span>
        {doneCount > 0 && (
          <button
            onClick={onClearCompleted}
            className="text-xs text-gray-400 hover:text-gray-600"
          >
            Clear completed
          </button>
        )}
      </div>
      <div className="max-h-60 space-y-1 overflow-y-auto rounded-lg border bg-white">
        {uploads.map((u, i) => (
          <div key={i} className="flex items-center gap-2 px-3 py-2 text-sm">
            {u.status === 'done' && (
              <CheckCircle size={14} className="shrink-0 text-green-500" />
            )}
            {u.status === 'error' && (
              <AlertCircle size={14} className="shrink-0 text-red-500" />
            )}
            {u.status === 'uploading' && (
              <div className="h-3.5 w-3.5 shrink-0 animate-spin rounded-full border-2 border-indigo-600 border-t-transparent" />
            )}
            {u.status === 'pending' && (
              <Clock size={14} className="shrink-0 text-gray-300" />
            )}
            <span className="flex-1 truncate text-gray-700">{u.file.name}</span>
            {u.status === 'uploading' && (
              <span className="text-xs text-gray-400">{u.progress}%</span>
            )}
            {(u.status === 'pending' || u.status === 'error') && (
              <button
                onClick={() => onRemove(i)}
                className="text-gray-300 hover:text-gray-500"
              >
                <X size={14} />
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
