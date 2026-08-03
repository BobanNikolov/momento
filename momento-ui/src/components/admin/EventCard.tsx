import { Calendar, MapPin, ChevronRight } from 'lucide-react'
import { EventResponse } from '../../api'
import StatusBadge from '../common/StatusBadge'

interface EventCardProps {
  event: EventResponse
  onClick: (event: EventResponse) => void
}

export default function EventCard({ event, onClick }: EventCardProps) {
  return (
    <button
      onClick={() => onClick(event)}
      className="flex flex-col rounded-xl border bg-white p-4 text-left transition hover:shadow-md"
    >
      <div className="mb-2 flex items-start justify-between">
        <h3 className="font-semibold leading-tight">{event.name}</h3>
        <StatusBadge status={event.status} className="ml-2" />
      </div>
      <div className="mt-auto space-y-1 text-xs text-gray-500">
        {event.eventDate && (
          <div className="flex items-center gap-1">
            <Calendar size={12} />
            {event.eventDate}
          </div>
        )}
        {event.location && (
          <div className="flex items-center gap-1">
            <MapPin size={12} />
            {event.location}
          </div>
        )}
      </div>
      <div className="mt-3 flex items-center justify-between text-xs text-gray-400">
        <span>/e/{event.slug}</span>
        <ChevronRight size={14} />
      </div>
    </button>
  )
}
