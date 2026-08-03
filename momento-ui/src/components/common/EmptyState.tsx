import { LucideIcon } from 'lucide-react'

interface EmptyStateProps {
  icon: LucideIcon
  message: string
  className?: string
}

export default function EmptyState({ icon: Icon, message, className = '' }: EmptyStateProps) {
  return (
    <div className={`rounded-xl border-2 border-dashed border-gray-200 py-12 text-center ${className}`}>
      <Icon size={40} className="mx-auto mb-3 text-gray-300" />
      <p className="text-sm text-gray-500">{message}</p>
    </div>
  )
}
