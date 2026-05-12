import { ref } from 'vue'

export function usePagination(defaultPageSize = 10) {
  const currentPage = ref(1)
  const pageSize = ref(defaultPageSize)
  const total = ref(0)

  const handlePageChange = (page) => {
    currentPage.value = page
  }

  const handleSizeChange = (size) => {
    pageSize.value = size
    currentPage.value = 1
  }

  const reset = () => {
    currentPage.value = 1
    total.value = 0
  }

  return {
    currentPage,
    pageSize,
    total,
    handlePageChange,
    handleSizeChange,
    reset
  }
}
