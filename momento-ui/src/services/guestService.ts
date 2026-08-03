import { guestApi } from '../api'

export const guestService = {
  async getEvent(slug: string) {
    const res = await guestApi.getEvent(slug)
    return res.data
  },

  async searchPhotos(slug: string, selfieBase64: string) {
    const res = await guestApi.search(slug, selfieBase64, true)
    return res.data.matchedPhotos
  },

  async getDownloadUrl(photoId: number) {
    const res = await guestApi.getDownloadUrl(photoId)
    return typeof res.data === 'string' ? res.data : (res.data as any)
  },
}
