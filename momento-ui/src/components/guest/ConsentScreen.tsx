import { Camera } from 'lucide-react'
import { EventResponse } from '../../api'

interface ConsentScreenProps {
  event: EventResponse
  onAgree: () => void
}

export default function ConsentScreen({ event, onAgree }: ConsentScreenProps) {
  return (
    <div className="flex flex-1 flex-col items-center justify-center text-center">
      <div className="mb-6">
        <h2 className="text-xl font-bold">{event.name}</h2>
        {event.eventDate && (
          <p className="mt-1 text-sm text-gray-500">{event.eventDate}</p>
        )}
        {event.location && (
          <p className="text-sm text-gray-500">{event.location}</p>
        )}
      </div>

      <div className="mb-6 w-full rounded-xl bg-white p-4 text-left text-sm shadow-sm border">
        <h3 className="mb-2 font-semibold">Find Your Photos</h3>
        <p className="text-gray-600">
          Take a selfie and we'll use facial recognition to find photos of you
          from this event. Your selfie will only be used for this search and
          will be automatically deleted.
        </p>
      </div>

      <button
        onClick={onAgree}
        className="flex w-full items-center justify-center gap-2 rounded-lg bg-indigo-600 py-3 text-sm font-medium text-white hover:bg-indigo-700 sm:w-auto sm:px-8"
      >
        <Camera size={18} />
        I Agree — Take My Selfie
      </button>
    </div>
  )
}
