import axios from 'axios'
import { photoApi } from '../api'

export const photoService = {
  async getProcessingStatus(eventId: number) {
    const res = await photoApi.getProcessingStatus(eventId)
    return res.data
  },

  async getUploadUrls(eventId: number, fileNames: string[]) {
    const res = await photoApi.getUploadUrls(eventId, fileNames)
    return res.data.items
  },

  async uploadToS3(
    uploadUrl: string,
    file: File,
    onProgress: (pct: number) => void
  ) {
    await axios.put(uploadUrl, file, {
      headers: { 'Content-Type': file.type },
      onUploadProgress: (progressEvent) => {
        const pct = progressEvent.total
          ? Math.round((progressEvent.loaded * 100) / progressEvent.total)
          : 0
        onProgress(pct)
      },
    })
  },

  async confirmUpload(photoId: number) {
    await photoApi.confirmUpload(photoId)
  },
}
