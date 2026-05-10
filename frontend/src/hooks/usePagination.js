import { ref } from 'vue'

export function usePagination(fetchFn) {
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)
  const loading = ref(false)

  const loadData = async () => {
    loading.value = true
    try {
      const result = await fetchFn({ page: currentPage.value, size: pageSize.value })
      total.value = result.total || 0
      return result
    } finally {
      loading.value = false
    }
  }

  const handlePageChange = (page) => {
    currentPage.value = page
    return loadData()
  }

  const handleSizeChange = (size) => {
    pageSize.value = size
    currentPage.value = 1
    return loadData()
  }

  return {
    currentPage,
    pageSize,
    total,
    loading,
    loadData,
    handlePageChange,
    handleSizeChange
  }
}
