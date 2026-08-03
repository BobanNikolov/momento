import { Trash2, Users } from 'lucide-react'
import { useState } from 'react'
import { PhotographerResponse } from '../../api'

interface PhotographerListProps {
  photographers: PhotographerResponse[]
  onAssign: (id: number) => Promise<void>
  onRemove: (id: number) => Promise<void>
}

export default function PhotographerList({
  photographers,
  onAssign,
  onRemove,
}: PhotographerListProps) {
  const [photographerId, setPhotographerId] = useState('')

  const handleAssign = async () => {
    if (!photographerId) return
    await onAssign(parseInt(photographerId))
    setPhotographerId('')
  }

  return (
    <div>
      <h4 className="mb-2 flex items-center gap-1 text-sm font-medium text-gray-700">
        <Users size={14} /> Photographers
      </h4>
      {photographers.length > 0 ? (
        <ul className="mb-2 space-y-1">
          {photographers.map((p) => (
            <li
              key={p.id}
              className="flex items-center justify-between rounded-lg bg-gray-50 px-3 py-2 text-sm"
            >
              <span>{p.email}</span>
              <button
                onClick={() => onRemove(p.id)}
                className="text-red-400 hover:text-red-600"
              >
                <Trash2 size={14} />
              </button>
            </li>
          ))}
        </ul>
      ) : (
        <p className="mb-2 text-xs text-gray-400">No photographers assigned</p>
      )}
      <div className="flex gap-2">
        <input
          type="number"
          placeholder="Photographer ID"
          value={photographerId}
          onChange={(e) => setPhotographerId(e.target.value)}
          className="flex-1 rounded-lg border px-3 py-1.5 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
        />
        <button
          onClick={handleAssign}
          disabled={!photographerId}
          className="rounded-lg bg-indigo-600 px-3 py-1.5 text-sm text-white hover:bg-indigo-700 disabled:opacity-50"
        >
          Assign
        </button>
      </div>
    </div>
  )
}
