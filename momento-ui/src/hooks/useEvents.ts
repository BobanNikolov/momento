import { useState, useEffect, useCallback } from 'react'
import { EventResponse, EventRequest } from '../api'
import { eventService } from '../services/eventService'

export function useEvents(onlyMyEvents: boolean = false) {
  const [events, setEvents] = useState<EventResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const loadEvents = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = onlyMyEvents
        ? await eventService.getMyEvents()
        : await eventService.getEvents()
      setEvents(data)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load events')
    } finally {
      setLoading(false)
    }
  }, [onlyMyEvents])

  useEffect(() => {
    loadEvents()
  }, [loadEvents])

  const createEvent = async (data: EventRequest) => {
    setSubmitting(true)
    try {
      await eventService.createEvent(data)
      await loadEvents()
      return { success: true }
    } catch (err: any) {
      return {
        success: false,
        error: err.response?.data?.message || 'Failed to create event',
      }
    } finally {
      setSubmitting(false)
    }
  }

  return {
    events,
    loading,
    submitting,
    error,
    refresh: loadEvents,
    createEvent,
  }
}
