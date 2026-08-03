import { useState, useCallback } from 'react'
import {
  EventResponse,
  EventRequest,
  ProcessingStatusResponse,
  PhotographerResponse,
  photoApi,
} from '../api'
import { eventService } from '../services/eventService'

export function useEventDetail() {
  const [selectedEvent, setSelectedEvent] = useState<EventResponse | null>(null)
  const [processingStatus, setProcessingStatus] =
    useState<ProcessingStatusResponse | null>(null)
  const [photographers, setPhotographers] = useState<PhotographerResponse[]>([])
  const [loading, setLoading] = useState(false)

  const loadDetail = useCallback(async (event: EventResponse) => {
    setSelectedEvent(event)
    setLoading(true)
    try {
      const [statusRes, photoRes] = await Promise.all([
        photoApi.getProcessingStatus(event.id),
        eventService.getPhotographers(event.id),
      ])
      setProcessingStatus(statusRes.data)
      setPhotographers(photoRes)
    } catch {
      // ignore
    } finally {
      setLoading(false)
    }
  }, [])

  const deleteEvent = async () => {
    if (!selectedEvent) return
    await eventService.deleteEvent(selectedEvent.id)
    setSelectedEvent(null)
  }

  const expireEvent = async () => {
    if (!selectedEvent) return
    await eventService.expireEvent(selectedEvent.id)
  }

  const updateEvent = async (data: EventRequest) => {
    if (!selectedEvent) return
    const updated = await eventService.updateEvent(selectedEvent.id, data)
    setSelectedEvent(updated)
    return updated
  }

  const assignPhotographer = async (photographerId: number) => {
    if (!selectedEvent) return
    const data = await eventService.assignPhotographer(
      selectedEvent.id,
      photographerId
    )
    setPhotographers(data)
  }

  const removePhotographer = async (photographerId: number) => {
    if (!selectedEvent) return
    await eventService.removePhotographer(selectedEvent.id, photographerId)
    setPhotographers((prev) => prev.filter((p) => p.id !== photographerId))
  }

  const clearDetail = () => {
    setSelectedEvent(null)
    setProcessingStatus(null)
    setPhotographers([])
  }

  return {
    selectedEvent,
    processingStatus,
    photographers,
    loading,
    loadDetail,
    deleteEvent,
    expireEvent,
    updateEvent,
    assignPhotographer,
    removePhotographer,
    clearDetail,
  }
}
