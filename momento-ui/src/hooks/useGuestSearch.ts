import { useState, useEffect, useCallback } from 'react'
import { EventResponse, PhotoResponse } from '../api'
import { guestService } from '../services/guestService'

export type Step =
  | 'loading'
  | 'consent'
  | 'selfie'
  | 'searching'
  | 'results'
  | 'error'

export function useGuestSearch(slug: string | undefined) {
  const [event, setEvent] = useState<EventResponse | null>(null)
  const [step, setStep] = useState<Step>('loading')
  const [errorMsg, setErrorMsg] = useState('')
  const [results, setResults] = useState<PhotoResponse[]>([])

  const loadEvent = useCallback(async () => {
    if (!slug) return
    setStep('loading')
    try {
      const data = await guestService.getEvent(slug)
      setEvent(data)
      if (data.status !== 'ACTIVE' && data.status !== 'COMPLETED') {
        setStep('error')
        setErrorMsg('This event is not currently available.')
      } else {
        setStep('consent')
      }
    } catch {
      setStep('error')
      setErrorMsg('Event not found.')
    }
  }, [slug])

  useEffect(() => {
    loadEvent()
  }, [loadEvent])

  const search = async (selfieBase64: string) => {
    if (!slug) return
    setStep('searching')
    try {
      const matchedPhotos = await guestService.searchPhotos(slug, selfieBase64)
      setResults(matchedPhotos)
      setStep('results')
    } catch {
      setStep('error')
      setErrorMsg('Something went wrong while searching. Please try again.')
    }
  }

  const reset = () => {
    setResults([])
    setErrorMsg('')
    if (event) {
      setStep('consent')
    } else {
      loadEvent()
    }
  }

  const setStepManually = (newStep: Step) => setStep(newStep)

  return {
    event,
    step,
    errorMsg,
    results,
    search,
    reset,
    setStep: setStepManually,
    setError: (msg: string) => {
      setStep('error')
      setErrorMsg(msg)
    },
  }
}
