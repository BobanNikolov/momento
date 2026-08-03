import { getStatusColor } from '../../utils/statusColors'

interface StatusBadgeProps {
  status: string
  className?: string
  children?: React.ReactNode
}

export default function StatusBadge({
  status,
  className = '',
  children,
}: StatusBadgeProps) {
  return (
    <span
      className={`shrink-0 rounded-full px-2 py-0.5 text-xs font-medium ${getStatusColor(
        status
      )} ${className}`}
    >
      {children || status}
    </span>
  )
}
