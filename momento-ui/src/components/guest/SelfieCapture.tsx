import { Camera, X, Search } from 'lucide-react'
import { RefObject } from 'react'

interface SelfieCaptureProps {
  videoRef: RefObject<HTMLVideoElement>
  canvasRef: RefObject<HTMLCanvasElement>
  cameraReady: boolean
  previewUrl: string | null
  onCapture: () => void
  onRetake: () => void
  onSearch: () => void
}

export default function SelfieCapture({
  videoRef,
  canvasRef,
  cameraReady,
  previewUrl,
  onCapture,
  onRetake,
  onSearch,
}: SelfieCaptureProps) {
  return (
    <div className="flex flex-1 flex-col items-center">
      <h2 className="mb-4 text-lg font-semibold">Take a Selfie</h2>

      {!previewUrl ? (
        <>
          <div
            className="relative mb-4 w-full overflow-hidden rounded-xl bg-black"
            style={{ aspectRatio: '4/3' }}
          >
            <video
              ref={videoRef}
              autoPlay
              playsInline
              muted
              className="h-full w-full object-cover"
              style={{ transform: 'scaleX(-1)' }}
            />
            {!cameraReady && (
              <div className="absolute inset-0 flex items-center justify-center bg-gray-900">
                <div className="h-6 w-6 animate-spin rounded-full border-4 border-white border-t-transparent" />
              </div>
            )}
          </div>
          <canvas ref={canvasRef} className="hidden" />
          <button
            onClick={onCapture}
            disabled={!cameraReady}
            className="flex h-16 w-16 items-center justify-center rounded-full bg-indigo-600 text-white shadow-lg hover:bg-indigo-700 disabled:opacity-50"
          >
            <Camera size={24} />
          </button>
        </>
      ) : (
        <>
          <div
            className="relative mb-4 w-full overflow-hidden rounded-xl"
            style={{ aspectRatio: '4/3' }}
          >
            <img
              src={previewUrl}
              alt="Selfie"
              className="h-full w-full object-cover"
              style={{ transform: 'scaleX(-1)' }}
            />
          </div>
          <div className="flex gap-3">
            <button
              onClick={onRetake}
              className="flex items-center gap-1.5 rounded-lg border px-4 py-2 text-sm hover:bg-gray-50"
            >
              <X size={16} /> Retake
            </button>
            <button
              onClick={onSearch}
              className="flex items-center gap-1.5 rounded-lg bg-indigo-600 px-6 py-2 text-sm font-medium text-white hover:bg-indigo-700"
            >
              <Search size={16} /> Find My Photos
            </button>
          </div>
        </>
      )}
    </div>
  )
}
