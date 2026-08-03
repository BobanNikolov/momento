import { useState, useEffect, useCallback } from 'react'
import { ProcessingStatusResponse } from '../api'
import { photoService } from '../services/photoService'

export interface UploadFile {
  file: File
  status: 'pending' | 'uploading' | 'done' | 'error'
  progress: number
}

export function usePhotoUpload(initialEventId: number | null) {
  const [selectedEventId, setSelectedEventId] = useState<number | null>(
    initialEventId
  )
  const [uploads, setUploads] = useState<UploadFile[]>([])
  const [uploading, setUploading] = useState(false)
  const [processingStatus, setProcessingStatus] =
    useState<ProcessingStatusResponse | null>(null)

  const loadStatus = useCallback(async () => {
    if (!selectedEventId) return
    try {
      const data = await photoService.getProcessingStatus(selectedEventId)
      setProcessingStatus(data)
    } catch {
      // ignore
    }
  }, [selectedEventId])

  useEffect(() => {
    loadStatus()
    const interval = setInterval(loadStatus, 10000)
    return () => clearInterval(interval)
  }, [loadStatus])

  const addFiles = (files: File[]) => {
    const newFiles: UploadFile[] = files.map((file) => ({
      file,
      status: 'pending',
      progress: 0,
    }))
    setUploads((prev) => [...prev, ...newFiles])
  }

  const removeFile = (index: number) => {
    setUploads((prev) => prev.filter((_, i) => i !== index))
  }

  const clearCompleted = () => {
    setUploads((prev) => prev.filter((u) => u.status !== 'done'))
  }

  const uploadAll = async () => {
    if (!selectedEventId || uploads.length === 0) return
    setUploading(true)

    const pendingFiles = uploads.filter(
      (u) => u.status === 'pending' || u.status === 'error'
    )
    if (pendingFiles.length === 0) {
      setUploading(false)
      return
    }

    try {
      const fileNames = pendingFiles.map((u) => u.file.name)
      const items = await photoService.getUploadUrls(selectedEventId, fileNames)

      for (let i = 0; i < items.length; i++) {
        const item = items[i]
        const uploadFile = pendingFiles[i]
        const idx = uploads.indexOf(uploadFile)

        setUploads((prev) =>
          prev.map((u, j) => (j === idx ? { ...u, status: 'uploading' } : u))
        )

        try {
          await photoService.uploadToS3(
            item.uploadUrl,
            uploadFile.file,
            (pct) => {
              setUploads((prev) =>
                prev.map((u, j) => (j === idx ? { ...u, progress: pct } : u))
              )
            }
          )
          await photoService.confirmUpload(item.photoId)
          setUploads((prev) =>
            prev.map((u, j) =>
              j === idx ? { ...u, status: 'done', progress: 100 } : u
            )
          )
        } catch {
          setUploads((prev) =>
            prev.map((u, j) => (j === idx ? { ...u, status: 'error' } : u))
          )
        }
      }
    } catch {
      setUploads((prev) =>
        prev.map((u) =>
          u.status === 'uploading' || pendingFiles.includes(u)
            ? { ...u, status: 'error' }
            : u
        )
      )
    } finally {
      setUploading(false)
      loadStatus()
    }
  }

  return {
    selectedEventId,
    setSelectedEventId,
    uploads,
    uploading,
    processingStatus,
    addFiles,
    removeFile,
    clearCompleted,
    uploadAll,
    refreshStatus: loadStatus,
  }
}
