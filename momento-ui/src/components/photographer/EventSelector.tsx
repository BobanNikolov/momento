import { EventResponse } from '../../api'
import Spinner from '../common/Spinner'

interface EventSelectorProps {
  events: EventResponse[]
  selectedId: number | null
  onChange: (id: number) => void
  loading?: boolean
}

export default function EventSelector({
  events,
  selectedId,
  onChange,
  loading = false,
}: EventSelectorProps) {
  if (loading) {
    return <Spinner size="sm" className="py-8" />
  }

  if (events.length === 0) {
    return <p className="text-sm text-gray-500">No active events available.</p>
  }

  return (
    <div className="mb-4">
      <label className="mb-1 block text-sm font-medium text-gray-700">
        Event
      </label>
      <select
        value={selectedId ?? ''}
        onChange={(e) => onChange(Number(e.target.value))}
        className="w-full rounded-lg border px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
      >
        {events.map((ev) => (
          <option key={ev.id} value={ev.id}>
            {ev.name} ({ev.slug})
          </option>
        ))}
      </select>
    </div>
  )
}
