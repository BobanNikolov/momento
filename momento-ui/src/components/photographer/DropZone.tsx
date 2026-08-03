import { Upload } from 'lucide-react'
import { useRef } from 'react'

interface DropZoneProps {
  onFilesSelected: (files: File[]) => void
}

export default function DropZone({ onFilesSelected }: DropZoneProps) {
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleFilesSelected = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files) return
    onFilesSelected(Array.from(e.target.files))
    e.target.value = ''
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    const files = Array.from(e.dataTransfer.files).filter((f) =>
      f.type.startsWith('image/')
    )
    onFilesSelected(files)
  }

  return (
    <div
      onDragOver={(e) => e.preventDefault()}
      onDrop={handleDrop}
      onClick={() => fileInputRef.current?.click()}
      className="mb-4 cursor-pointer rounded-xl border-2 border-dashed border-gray-300 p-8 text-center transition hover:border-indigo-400 hover:bg-indigo-50/30"
    >
      <Upload size={32} className="mx-auto mb-2 text-gray-400" />
      <p className="text-sm text-gray-600">
        <span className="font-medium text-indigo-600">Click to browse</span> or
        drag & drop photos
      </p>
      <p className="mt-1 text-xs text-gray-400">JPG, PNG, HEIC supported</p>
      <input
        ref={fileInputRef}
        type="file"
        multiple
        accept="image/*"
        onChange={handleFilesSelected}
        className="hidden"
      />
    </div>
  )
}
