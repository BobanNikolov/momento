import { ReactNode } from 'react'
import { X } from 'lucide-react'

interface ModalProps {
  title: string
  children: ReactNode
  onClose: () => void
  maxWidth?: string
  containerClassName?: string
}

export default function Modal({
  title,
  children,
  onClose,
  maxWidth = 'max-w-md',
  containerClassName = '',
}: ModalProps) {
  return (
    <div
      className="fixed inset-0 z-50 flex items-end justify-center bg-black/40 sm:items-center"
      onClick={onClose}
    >
      <div
        className={`w-full ${maxWidth} rounded-t-2xl bg-white p-6 sm:rounded-2xl ${containerClassName}`}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-semibold">{title}</h3>
          <button onClick={onClose} className="rounded-lg p-1 hover:bg-gray-100">
            <X size={20} />
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}
