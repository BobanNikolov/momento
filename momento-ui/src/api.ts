import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (!window.location.pathname.startsWith('/login') && !window.location.pathname.startsWith('/e/')) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  role: 'ADMIN' | 'PHOTOGRAPHER'
}

export interface UserResponse {
  id: number
  email: string
  role: 'ADMIN' | 'PHOTOGRAPHER'
}

export interface LoginResponse {
  token: string
  user: UserResponse
}

export interface EventRequest {
  name: string
  slug: string
  eventDate?: string
  location?: string
  retentionDays?: number
}

export interface EventResponse {
  id: number
  name: string
  slug: string
  eventDate: string | null
  location: string | null
  status: 'DRAFT' | 'ACTIVE' | 'COMPLETED' | 'EXPIRED'
  retentionDays: number | null
  createdAt: string
  expiresAt: string | null
  qrCodeUrl: string | null
}

export interface PhotoResponse {
  id: number
  fileName: string
  thumbnailS3Key: string | null
  downloadUrl: string | null
}

export interface UploadUrlItem {
  photoId: number
  fileName: string
  uploadUrl: string
}

export interface UploadUrlResponse {
  items: UploadUrlItem[]
}

export interface ProcessingStatusResponse {
  counts: Record<string, number>
}

export interface GuestSearchResponse {
  matchedPhotos: PhotoResponse[]
}

export interface PhotographerResponse {
  id: number
  email: string
}

// Auth
export const authApi = {
  login: (data: LoginRequest) => api.post<LoginResponse>('/auth/login', data),
  register: (data: RegisterRequest) => api.post<UserResponse>('/auth/register', data),
  me: () => api.get<UserResponse>('/auth/me'),
}

// Events
export const eventApi = {
  list: () => api.get<EventResponse[]>('/event'),
  myEvents: () => api.get<EventResponse[]>('/event/my-events'),
  getBySlug: (slug: string) => api.get<EventResponse>(`/event/${slug}`),
  getById: (id: number) => api.get<EventResponse>(`/event/${id}`),
  create: (data: EventRequest) => api.post<EventResponse>('/event', data),
  update: (id: number, data: EventRequest) => api.put<EventResponse>(`/event/${id}`, data),
  delete: (id: number) => api.delete(`/event/${id}`),
  expire: (id: number) => api.post(`/event/${id}/expire`),
  listPhotographers: (eventId: number) => api.get<PhotographerResponse[]>(`/event/${eventId}/photographers`),
  assignPhotographer: (eventId: number, photographerId: number) =>
    api.post(`/event/${eventId}/photographers`, { photographerId }),
  removePhotographer: (eventId: number, photographerId: number) =>
    api.delete(`/event/${eventId}/photographers/${photographerId}`),
}

// Photos
export const photoApi = {
  getUploadUrls: (eventId: number, fileNames: string[]) =>
    api.post<UploadUrlResponse>(`/event/${eventId}/photos/upload-urls`, { fileNames }),
  confirmUpload: (photoId: number) => api.post('/photos/confirm-upload', { photoId }),
  listPhotos: (eventId: number) => api.get<PhotoResponse[]>(`/event/${eventId}/photos`),
  getProcessingStatus: (eventId: number) => api.get<ProcessingStatusResponse>(`/event/${eventId}/processing-status`),
  reprocessAll: (eventId: number) => api.post(`/event/${eventId}/process-all`),
}

// Guest (public)
export const guestApi = {
  getEvent: (slug: string) => api.get<EventResponse>(`/public/event/${slug}`),
  search: (slug: string, selfie: string, consentAccepted: boolean) =>
    api.post<GuestSearchResponse>(`/public/event/${slug}/search`, {
      selfie,
      consentAccepted,
      consentPolicyVersion: '1.0',
    }),
  getDownloadUrl: (photoId: number) => api.get<string>(`/public/photos/${photoId}/download-url`),
}

export default api
