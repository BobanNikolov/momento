import { useState, useEffect } from 'react'
import { QRCodeSVG } from 'qrcode.react'
import { Copy, Edit2, Check, X } from 'lucide-react'
import {
  EventResponse,
  EventRequest,
  ProcessingStatusResponse,
  PhotographerResponse,
} from '../../api'
import Modal from '../common/Modal'
import StatusBadge from '../common/StatusBadge'
import ProcessingStatusBar from './ProcessingStatusBar'
import PhotographerList from './PhotographerList'
import FormInput from '../common/FormInput'

interface EventDetailModalProps {
  event: EventResponse | null
  processingStatus: ProcessingStatusResponse | null
  photographers: PhotographerResponse[]
  onClose: () => void
  onExpire: () => Promise<void>
  onDelete: () => Promise<void>
  onUpdate: (data: EventRequest) => Promise<void>
  onAssignPhotographer: (id: number) => Promise<void>
  onRemovePhotographer: (id: number) => Promise<void>
}

export default function EventDetailModal({
  event,
  processingStatus,
  photographers,
  onClose,
  onExpire,
  onDelete,
  onUpdate,
  onAssignPhotographer,
  onRemovePhotographer,
}: EventDetailModalProps) {
  const [isEditing, setIsEditing] = useState(false)
  const [formData, setFormData] = useState<EventRequest>({
    name: '',
    slug: '',
    location: '',
    eventDate: '',
    retentionDays: 30,
  })
  const [copied, setCopied] = useState(false)

  useEffect(() => {
    if (event) {
      setFormData({
        name: event.name,
        slug: event.slug,
        location: event.location || '',
        eventDate: event.eventDate || '',
        retentionDays: event.retentionDays || 30,
      })
    }
  }, [event])

  if (!event) return null

  const guestUrl = `${window.location.origin}/e/${event.slug}`

  const handleCopyLink = () => {
    navigator.clipboard.writeText(guestUrl)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const handleSave = async () => {
    await onUpdate(formData)
    setIsEditing(false)
  }

  return (
    <Modal
      title={isEditing ? 'Edit Event' : event.name}
      onClose={onClose}
      maxWidth="max-w-lg"
      containerClassName="max-h-[85vh] overflow-y-auto"
    >
      <div className="space-y-6">
        {isEditing ? (
          <div className="space-y-4">
            <FormInput
              label="Event Name"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />
            <div className="grid grid-cols-2 gap-3">
              <FormInput
                label="Date"
                type="date"
                value={formData.eventDate || ''}
                onChange={(e) =>
                  setFormData({ ...formData, eventDate: e.target.value })
                }
              />
              <FormInput
                label="Retention Days"
                type="number"
                value={formData.retentionDays || ''}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    retentionDays: parseInt(e.target.value) || 0,
                  })
                }
              />
            </div>
            <FormInput
              label="Location"
              value={formData.location || ''}
              onChange={(e) => setFormData({ ...formData, location: e.target.value })}
            />
            <div className="flex justify-end gap-2 pt-2">
              <button
                onClick={() => setIsEditing(false)}
                className="rounded-lg border px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={handleSave}
                className="flex items-center gap-1.5 rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
              >
                <Check size={16} />
                Save Changes
              </button>
            </div>
          </div>
        ) : (
          <>
            {/* QR Code Section */}
            <div className="flex flex-col items-center rounded-xl bg-gray-50 p-6">
              <div className="rounded-lg bg-white p-3 shadow-sm">
                <QRCodeSVG value={guestUrl} size={160} />
              </div>
              <p className="mt-4 text-center text-sm font-medium text-gray-700">
                Guest Access QR Code
              </p>
              <div className="mt-3 flex w-full max-w-xs items-center gap-2 rounded-lg border bg-white p-2">
                <input
                  type="text"
                  readOnly
                  value={guestUrl}
                  className="w-full bg-transparent px-2 text-xs text-gray-500 focus:outline-none"
                />
                <button
                  onClick={handleCopyLink}
                  className="flex shrink-0 items-center justify-center rounded-md p-1.5 text-gray-400 hover:bg-gray-100 hover:text-indigo-600"
                  title="Copy Link"
                >
                  {copied ? (
                    <Check size={16} className="text-green-500" />
                  ) : (
                    <Copy size={16} />
                  )}
                </button>
              </div>
            </div>

            {/* Info */}
            <div className="relative">
              <button
                onClick={() => setIsEditing(true)}
                className="absolute right-0 top-0 text-gray-400 hover:text-indigo-600"
                title="Edit Details"
              >
                <Edit2 size={18} />
              </button>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                    Status
                  </span>
                  <div className="mt-1">
                    <StatusBadge status={event.status} />
                  </div>
                </div>
                <div>
                  <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                    Slug
                  </span>
                  <p className="mt-1 font-mono text-xs text-gray-600">
                    /e/{event.slug}
                  </p>
                </div>
                <div>
                  <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                    Date
                  </span>
                  <p className="mt-1 text-gray-700">{event.eventDate || 'Not set'}</p>
                </div>
                <div>
                  <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                    Location
                  </span>
                  <p className="mt-1 text-gray-700">{event.location || 'Not set'}</p>
                </div>
                <div>
                  <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">
                    Retention
                  </span>
                  <p className="mt-1 text-gray-700">{event.retentionDays} days</p>
                </div>
              </div>
            </div>

            {/* Processing Status */}
            <div className="border-t pt-4">
              <ProcessingStatusBar status={processingStatus} />
            </div>

            {/* Photographers */}
            <div className="border-t pt-4">
              <PhotographerList
                photographers={photographers}
                onAssign={onAssignPhotographer}
                onRemove={onRemovePhotographer}
              />
            </div>

            {/* Actions */}
            <div className="flex flex-wrap gap-2 border-t pt-4">
              {event.status !== 'EXPIRED' && (
                <button
                  onClick={() => onExpire()}
                  className="rounded-lg border border-yellow-300 px-3 py-1.5 text-sm text-yellow-700 hover:bg-yellow-50"
                >
                  Expire Event
                </button>
              )}
              <button
                onClick={() => onDelete()}
                className="rounded-lg border border-red-300 px-3 py-1.5 text-sm text-red-600 hover:bg-red-50"
              >
                Delete Event
              </button>
            </div>
          </>
        )}
      </div>
    </Modal>
  )
}
