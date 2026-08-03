import { Download, Image } from 'lucide-react'
import { PhotoResponse } from '../../api'

interface PhotoResultsProps {
  photos: PhotoResponse[]
  onDownload: (photo: PhotoResponse) => void
  onReset: () => void
}

export default function PhotoResults({
  photos,
  onDownload,
  onReset,
}: PhotoResultsProps) {
  return (
    <div className="flex flex-1 flex-col">
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-lg font-semibold">
          {photos.length > 0
            ? `Found ${photos.length} photo(s)`
            : 'No photos found'}
        </h2>
        <button
          onClick={onReset}
          className="text-sm text-indigo-600 hover:text-indigo-700"
        >
          Search Again
        </button>
      </div>

      {photos.length === 0 ? (
        <div className="flex flex-1 flex-col items-center justify-center text-center">
          <Image size={48} className="mb-3 text-gray-300" />
          <p className="text-sm text-gray-500">
            We couldn't find any photos matching your face. Try again with
            better lighting or a different angle.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-2 sm:grid-cols-3">
          {photos.map((photo) => (
            <div
              key={photo.id}
              className="group relative overflow-hidden rounded-lg bg-gray-200"
              style={{ aspectRatio: '1' }}
            >
              {photo.downloadUrl ? (
                <img
                  src={photo.downloadUrl}
                  alt={photo.fileName}
                  className="h-full w-full object-cover"
                  loading="lazy"
                />
              ) : (
                <div className="flex h-full w-full items-center justify-center">
                  <Image size={24} className="text-gray-400" />
                </div>
              )}
              <button
                onClick={() => onDownload(photo)}
                className="absolute inset-0 flex items-center justify-center bg-black/0 transition group-hover:bg-black/40"
              >
                <Download
                  size={24}
                  className="text-white opacity-0 transition group-hover:opacity-100"
                />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
