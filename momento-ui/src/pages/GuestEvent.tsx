import { useParams } from 'react-router-dom'
import { AlertCircle } from 'lucide-react'
import { useGuestSearch } from '../hooks/useGuestSearch'
import { useCamera } from '../hooks/useCamera'
import Header from '../components/layout/Header'
import Spinner from '../components/common/Spinner'
import ConsentScreen from '../components/guest/ConsentScreen'
import SelfieCapture from '../components/guest/SelfieCapture'
import PhotoResults from '../components/guest/PhotoResults'
import { PhotoResponse } from '../api'
import { guestService } from '../services/guestService'

export default function GuestEvent() {
  const { slug } = useParams<{ slug: string }>()
  const {
    event,
    step,
    errorMsg,
    results,
    search,
    reset,
    setStep,
    setError,
  } = useGuestSearch(slug)

  const {
    videoRef,
    canvasRef,
    cameraReady,
    selfiePreview,
    selfieBase64,
    error: cameraError,
    startCamera,
    capture,
    reset: resetCamera,
  } = useCamera()

  const handleStartCamera = () => {
    setStep('selfie')
    startCamera()
  }

  // Handle camera error
  if (cameraError && step === 'selfie') {
    setError(cameraError)
  }

  const handleSearch = () => {
    if (selfieBase64) {
      search(selfieBase64)
    }
  }

  const handleDownload = async (photo: PhotoResponse) => {
    try {
      const url = await guestService.getDownloadUrl(photo.id)
      window.open(url, '_blank')
    } catch {
      // ignore
    }
  }

  const handleReset = () => {
    resetCamera()
    reset()
  }

  return (
    <div className="flex min-h-screen flex-col bg-gray-50">
      <Header maxWidth="max-w-lg" centerLogo />

      <main className="mx-auto flex w-full max-w-lg flex-1 flex-col px-4 py-6">
        {step === 'loading' && <Spinner className="flex-1" />}

        {step === 'error' && (
          <div className="flex flex-1 flex-col items-center justify-center text-center">
            <AlertCircle size={40} className="mb-3 text-red-400" />
            <p className="mb-4 text-sm text-gray-600">{errorMsg}</p>
            <button
              onClick={handleReset}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-700"
            >
              Try Again
            </button>
          </div>
        )}

        {step === 'consent' && event && (
          <ConsentScreen event={event} onAgree={handleStartCamera} />
        )}

        {step === 'selfie' && (
          <SelfieCapture
            videoRef={videoRef}
            canvasRef={canvasRef}
            cameraReady={cameraReady}
            previewUrl={selfiePreview}
            onCapture={capture}
            onRetake={startCamera}
            onSearch={handleSearch}
          />
        )}

        {step === 'searching' && (
          <div className="flex flex-1 flex-col items-center justify-center text-center">
            <Spinner size="md" className="mb-4" />
            <p className="text-sm text-gray-600">Searching for your photos...</p>
          </div>
        )}

        {step === 'results' && (
          <PhotoResults
            photos={results}
            onDownload={handleDownload}
            onReset={handleReset}
          />
        )}
      </main>
    </div>
  )
}
