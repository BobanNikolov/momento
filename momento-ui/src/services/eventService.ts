import { eventApi, EventRequest } from '../api'

export const eventService = {
  async getEvents() {
    const res = await eventApi.list()
    return res.data
  },

  async getMyEvents() {
    const res = await eventApi.myEvents()
    return res.data
  },

  async createEvent(data: EventRequest) {
    const res = await eventApi.create(data)
    return res.data
  },

  async updateEvent(id: number, data: EventRequest) {
    const res = await eventApi.update(id, data)
    return res.data
  },

  async deleteEvent(id: number) {
    await eventApi.delete(id)
  },

  async expireEvent(id: number) {
    await eventApi.expire(id)
  },

  async getPhotographers(eventId: number) {
    const res = await eventApi.listPhotographers(eventId)
    return res.data
  },

  async assignPhotographer(eventId: number, photographerId: number) {
    await eventApi.assignPhotographer(eventId, photographerId)
    return this.getPhotographers(eventId)
  },

  async removePhotographer(eventId: number, photographerId: number) {
    await eventApi.removePhotographer(eventId, photographerId)
  },
}
