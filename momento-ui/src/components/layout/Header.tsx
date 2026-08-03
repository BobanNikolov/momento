import { LogOut } from 'lucide-react'
import { UserResponse } from '../../api'

interface HeaderProps {
  user?: UserResponse | null
  onLogout?: () => void
  maxWidth?: string
  centerLogo?: boolean
}

export default function Header({
  user,
  onLogout,
  maxWidth = 'max-w-5xl',
  centerLogo = false,
}: HeaderProps) {
  return (
    <header className="border-b bg-white">
      <div
        className={`mx-auto flex ${maxWidth} items-center ${
          centerLogo ? 'justify-center' : 'justify-between'
        } px-4 py-3 sm:px-6`}
      >
        <h1 className="text-lg font-bold text-indigo-600">Momento</h1>
        {!centerLogo && (
          <div className="flex items-center gap-3">
            {user && (
              <span className="hidden text-sm text-gray-500 sm:inline">
                {user.email}
              </span>
            )}
            {onLogout && (
              <button
                onClick={onLogout}
                className="rounded-lg p-2 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
              >
                <LogOut size={18} />
              </button>
            )}
          </div>
        )}
      </div>
    </header>
  )
}
