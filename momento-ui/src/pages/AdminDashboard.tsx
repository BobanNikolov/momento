import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Plus, Image } from 'lucide-react'
import { useAuth } from '../AuthContext'
import { useEvents } from '../hooks/useEvents'
import { useEventDetail } from '../hooks/useEventDetail'
import Header from '../components/layout/Header'
import Spinner from '../components/common/Spinner'
import Modal from '../components/common/Modal'
import EmptyState from '../components/common/EmptyState'
import EventCard from '../components/admin/EventCard'
import CreateEventForm from '../components/admin/CreateEventForm'
import EventDetailModal from '../components/admin/EventDetailModal'
import { EventResponse } from '../api'

type ModalView = 'none' | 'create' | 'detail'

export default function AdminDashboard() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [modal, setModal] = useState<ModalView>('none')

  const { events, loading, submitting, refresh, createEvent } = useEvents()
  const {
    selectedEvent,
    processingStatus,
    photographers,
    loadDetail,
    deleteEvent,
    expireEvent,
    updateEvent,
    assignPhotographer,
    removePhotographer,
    clearDetail,
  } = useEventDetail()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const handleOpenDetail = (event: EventResponse) => {
    loadDetail(event)
    setModal('detail')
  }

  const handleCreateSubmit = async (data: any) => {
    const res = await createEvent(data)
    if (res.success) {
      setModal('none')
    }
    return res
  }

  const handleDelete = async () => {
    if (!confirm('Delete this event? This cannot be undone.')) return
    await deleteEvent()
    setModal('none')
    refresh()
  }

  const handleExpire = async () => {
    if (!confirm('Expire this event? Photos will be scheduled for cleanup.')) return
    await expireEvent()
    setModal('none')
    refresh()
  }

  const handleUpdate = async (data: any) => {
    await updateEvent(data)
    refresh()
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header user={user} onLogout={handleLogout} />

      <main className="mx-auto max-w-5xl px-4 py-6 sm:px-6">
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-xl font-semibold">Events</h2>
          <button
            onClick={() => setModal('create')}
            className="flex items-center gap-1.5 rounded-lg bg-indigo-600 px-3 py-2 text-sm font-medium text-white hover:bg-indigo-700"
          >
            <Plus size={16} />
            <span className="hidden sm:inline">New Event</span>
          </button>
        </div>

        {loading ? (
          <Spinner className="py-12" />
        ) : events.length === 0 ? (
          <EmptyState
            icon={Image}
            message="No events yet. Create your first event to get started."
          />
        ) : (
          <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
            {events.map((event) => (
              <EventCard
                key={event.id}
                event={event}
                onClick={handleOpenDetail}
              />
            ))}
          </div>
        )}
      </main>

      {modal === 'create' && (
        <Modal title="Create Event" onClose={() => setModal('none')}>
          <CreateEventForm
            onSubmit={handleCreateSubmit}
            submitting={submitting}
          />
        </Modal>
      )}

      {modal === 'detail' && (
        <EventDetailModal
          event={selectedEvent}
          processingStatus={processingStatus}
          photographers={photographers}
          onClose={() => {
            setModal('none')
            clearDetail()
          }}
          onExpire={handleExpire}
          onDelete={handleDelete}
          onUpdate={handleUpdate}
          onAssignPhotographer={assignPhotographer}
          onRemovePhotographer={removePhotographer}
        />
      )}
    </div>
  )
}
