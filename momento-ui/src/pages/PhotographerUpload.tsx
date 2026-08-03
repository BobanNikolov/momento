import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'
import { useEvents } from '../hooks/useEvents'
import { usePhotoUpload } from '../hooks/usePhotoUpload'
import Header from '../components/layout/Header'
import EventSelector from '../components/photographer/EventSelector'
import DropZone from '../components/photographer/DropZone'
import FileList from '../components/photographer/FileList'
import UploadStatus from '../components/photographer/UploadStatus'

export default function PhotographerUpload() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const { events, loading: loadingEvents } = useEvents(user?.role === 'PHOTOGRAPHER')

  // Filter only ACTIVE or DRAFT events for photographers
  const activeEvents = events.filter(
    (e) => e.status === 'ACTIVE' || e.status === 'DRAFT'
  )

  const {
    selectedEventId,
    setSelectedEventId,
    uploads,
    uploading,
    processingStatus,
    addFiles,
    removeFile,
    clearCompleted,
    uploadAll,
    refreshStatus,
  } = usePhotoUpload(activeEvents.length > 0 ? activeEvents[0].id : null)

  // Sync selected event if none is selected and events load
  useEffect(() => {
    if (!selectedEventId && activeEvents.length > 0) {
      setSelectedEventId(activeEvents[0].id)
    }
  }, [activeEvents, selectedEventId, setSelectedEventId])

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const pendingCount = uploads.filter(
    (u) => u.status === 'pending' || u.status === 'error'
  ).length

  return (
    <div className="min-h-screen bg-gray-50">
      <Header user={user} onLogout={handleLogout} maxWidth="max-w-3xl" />

      <main className="mx-auto max-w-3xl px-4 py-6 sm:px-6">
        <h2 className="mb-4 text-xl font-semibold">Upload Photos</h2>

        <EventSelector
          events={activeEvents}
          selectedId={selectedEventId}
          onChange={setSelectedEventId}
          loading={loadingEvents}
        />

        {selectedEventId && (
          <>
            <UploadStatus status={processingStatus} onRefresh={refreshStatus} />

            <DropZone onFilesSelected={addFiles} />

            <FileList
              uploads={uploads}
              onRemove={removeFile}
              onClearCompleted={clearCompleted}
            />

            {pendingCount > 0 && (
              <button
                onClick={uploadAll}
                disabled={uploading}
                className="w-full rounded-lg bg-indigo-600 py-2.5 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50 sm:w-auto sm:px-6"
              >
                {uploading ? 'Uploading...' : `Upload ${pendingCount} photo(s)`}
              </button>
            )}
          </>
        )}
      </main>
    </div>
  )
}
