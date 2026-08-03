import { useState } from 'react'
import { EventRequest } from '../../api'
import FormInput from '../common/FormInput'

interface CreateEventFormProps {
  onSubmit: (
    data: EventRequest
  ) => Promise<{ success: boolean; error?: string }>
  submitting: boolean
}

export default function CreateEventForm({
  onSubmit,
  submitting,
}: CreateEventFormProps) {
  const [name, setName] = useState('')
  const [slug, setSlug] = useState('')
  const [eventDate, setEventDate] = useState('')
  const [location, setLocation] = useState('')
  const [retentionDays, setRetentionDays] = useState('30')
  const [error, setError] = useState('')

  const handleNameChange = (val: string) => {
    setName(val)
    if (!slug) {
      setSlug(
        val
          .toLowerCase()
          .replace(/[^a-z0-9]+/g, '-')
          .replace(/(^-|-$)/g, '')
      )
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    const result = await onSubmit({
      name,
      slug,
      eventDate: eventDate || undefined,
      location: location || undefined,
      retentionDays: retentionDays ? parseInt(retentionDays) : undefined,
    })
    if (!result.success) {
      setError(result.error || 'Failed to create event')
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-3">
      {error && (
        <div className="rounded-lg bg-red-50 p-2 text-sm text-red-600">
          {error}
        </div>
      )}
      <FormInput
        label="Name"
        required
        value={name}
        onChange={(e) => handleNameChange(e.target.value)}
        placeholder="Wedding of John & Jane"
      />
      <FormInput
        label="Slug"
        required
        value={slug}
        onChange={(e) => setSlug(e.target.value)}
        placeholder="john-jane-wedding"
      />
      <div className="grid grid-cols-2 gap-3">
        <FormInput
          label="Date"
          type="date"
          value={eventDate}
          onChange={(e) => setEventDate(e.target.value)}
        />
        <FormInput
          label="Retention (days)"
          type="number"
          value={retentionDays}
          onChange={(e) => setRetentionDays(e.target.value)}
        />
      </div>
      <FormInput
        label="Location"
        value={location}
        onChange={(e) => setLocation(e.target.value)}
        placeholder="Grand Ballroom, NYC"
      />
      <button
        type="submit"
        disabled={submitting}
        className="w-full rounded-lg bg-indigo-600 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
      >
        {submitting ? 'Creating...' : 'Create Event'}
      </button>
    </form>
  )
}
