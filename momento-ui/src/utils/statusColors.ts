export const getStatusColor = (status: string) => {
  switch (status.toUpperCase()) {
    case 'ACTIVE':
    case 'PROCESSED':
      return 'bg-green-100 text-green-700'
    case 'DRAFT':
    case 'PENDING':
    case 'UPLOADING':
    case 'QUEUED':
      return 'bg-yellow-100 text-yellow-700'
    case 'COMPLETED':
      return 'bg-blue-100 text-blue-700'
    case 'EXPIRED':
      return 'bg-gray-100 text-gray-500'
    case 'FAILED':
    case 'ERROR':
      return 'bg-red-100 text-red-600'
    default:
      return 'bg-gray-100 text-gray-600'
  }
}
