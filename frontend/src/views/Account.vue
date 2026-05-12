<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1 class="page-title">会计科目管理</h1>
        <p class="page-desc">管理企业会计科目体系，支持多级科目设置</p>
      </div>
      <div class="header-actions">
        <el-button class="btn-secondary" @click="handleRefresh" :loading="refreshing">
          <el-icon v-if="!refreshing"><Refresh /></el-icon>
          {{ refreshing ? '刷新中...' : '刷新' }}
        </el-button>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增科目
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon :size="24"><Folder /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ allAccounts.length }}</span>
          <span class="stat-label">科目总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon :size="24"><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ allAccounts.filter(a => a.isEnabled).length }}</span>
          <span class="stat-label">已启用</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon :size="24"><FolderOpened /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ allAccounts.filter(a => a.level === 1).length }}</span>
          <span class="stat-label">一级科目</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple">
          <el-icon :size="24"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ allAccounts.filter(a => a.level === 2).length }}</span>
          <span class="stat-label">二级科目</span>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-card">
      <div class="card-header">
        <div class="card-title">
          <el-icon><List /></el-icon>
          <span>科目列表</span>
        </div>
        <div class="card-tools">
          <el-input
            v-model="searchText"
            placeholder="搜索科目编码或名称"
            prefix-icon="Search"
            clearable
            style="width: 240px"
          />
        </div>
      </div>
      <div class="card-body">
        <el-table 
          :data="filteredData" 
          row-key="code" 
          :tree-props="{ children: 'children' }"
          default-expand-all
          :header-cell-style="{ background: 'transparent', color: 'var(--text-secondary)', fontWeight: 600 }"
          v-loading="tableLoading"
          element-loading-text="加载中..."
        >
          <el-table-column prop="code" label="科目编码" width="160">
            <template #default="{ row }">
              <span class="code-cell">{{ row.code }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="科目名称" min-width="200">
            <template #default="{ row }">
              <div class="name-cell">
                <span class="account-name">{{ row.name }}</span>
                <span v-if="row.parentCode" class="parent-tag">子科目</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="direction" label="余额方向" width="120" align="center">
            <template #default="{ row }">
              <span :class="['direction-badge', row.direction.toLowerCase()]">
                {{ row.direction === 'DEBIT' ? '借方' : '贷方' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="level" label="科目级次" width="100" align="center">
            <template #default="{ row }">
              <span class="level-badge">{{ row.level }}级</span>
            </template>
          </el-table-column>
          <el-table-column prop="isEnabled" label="状态" width="100" align="center">
            <template #default="{ row }">
              <span :class="['status-badge', row.isEnabled ? 'active' : 'inactive']">
                <span class="status-dot"></span>
                {{ row.isEnabled ? '启用' : '停用' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip content="编辑" placement="top">
                  <el-button link @click="handleEdit(row)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button link type="danger" @click="handleDelete(row)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChangeWithLoad"
            @current-change="handlePageChangeWithLoad"
          />
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="form.id ? '编辑科目' : '新增科目'" 
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="科目编码" prop="code">
          <el-input v-model="form.code" :disabled="!!form.id" placeholder="请输入科目编码" />
        </el-form-item>
        <el-form-item label="科目名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入科目名称" />
        </el-form-item>
        <el-form-item label="上级科目" prop="parentCode">
          <el-select v-model="form.parentCode" placeholder="请选择上级科目（可选）" clearable filterable style="width: 100%">
            <el-option 
              v-for="a in allAccounts.filter(x => x.level === 1)" 
              :key="a.code" 
              :label="`${a.code} - ${a.name}`" 
              :value="a.code" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="余额方向" prop="direction">
          <el-radio-group v-model="form.direction" class="direction-radio">
            <el-radio value="DEBIT">
              <span class="radio-label debit">借方</span>
            </el-radio>
            <el-radio value="CREDIT">
              <span class="radio-label credit">贷方</span>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch 
            v-model="form.isEnabled" 
            active-text="启用" 
            inactive-text="停用"
            inline-prompt
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving">
            {{ saving ? '保存中...' : '确认保存' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { accountApi } from '../api'
import { usePagination } from '../composables/usePagination'
import { useLoading } from '../composables/useLoading'
import { getDirectionText } from '../utils/format'

const accounts = ref([])
const allAccounts = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const searchText = ref('')
const form = ref({ code: '', name: '', parentCode: '', direction: 'DEBIT', isEnabled: true })

const { currentPage, pageSize, total, handlePageChange, handleSizeChange } = usePagination(10)
const { loading: tableLoading, withLoading: withTableLoading } = useLoading()
const { loading: saving, withLoading: withSaving } = useLoading()
const { loading: refreshing, withLoading: withRefreshing } = useLoading()

const rules = {
  code: [{ required: true, message: '请输入科目编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入科目名称', trigger: 'blur' }],
  direction: [{ required: true, message: '请选择余额方向', trigger: 'change' }]
}

const tableData = computed(() => {
  const map = new Map()
  accounts.value.forEach(a => map.set(a.code, { ...a, children: [] }))
  const tree = []
  map.forEach(item => {
    if (item.parentCode && map.has(item.parentCode)) {
      map.get(item.parentCode).children.push(item)
    } else {
      tree.push(item)
    }
  })
  return tree
})

const filteredData = computed(() => {
  if (!searchText.value) return tableData.value
  const keyword = searchText.value.toLowerCase()
  const filterTree = (items) => {
    return items.filter(item => {
      const match = item.code.toLowerCase().includes(keyword) || item.name.toLowerCase().includes(keyword)
      if (item.children?.length) {
        item.children = filterTree(item.children)
        return match || item.children.length > 0
      }
      return match
    })
  }
  return filterTree(JSON.parse(JSON.stringify(tableData.value)))
})

const loadData = async () => {
  await withTableLoading(async () => {
    const allRes = await accountApi.list()
    allAccounts.value = allRes.data || []
    
    const pageRes = await accountApi.page(currentPage.value, pageSize.value)
    accounts.value = pageRes.data?.list || []
    total.value = pageRes.data?.total || 0
  })
}

const handlePageChangeWithLoad = (page) => {
  handlePageChange(page)
  loadData()
}

const handleSizeChangeWithLoad = (size) => {
  handleSizeChange(size)
  loadData()
}

const handleRefresh = async () => {
  await withRefreshing(async () => {
    await loadData()
    ElMessage.success('刷新成功')
  })
}

const handleAdd = () => {
  form.value = { code: '', name: '', parentCode: '', direction: 'DEBIT', isEnabled: true }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  await withSaving(async () => {
    form.value.level = form.value.parentCode ? 2 : 1
    await accountApi.save(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  })
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除科目「${row.name}」吗？`, '删除确认', { 
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消'
  })
  await accountApi.delete(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-container {
  animation: pageEnter 0.5s ease-out;
}

@keyframes pageEnter {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.page-desc {
  font-size: 14px;
  color: #6b7280;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.btn-secondary {
  background: #f5f7fa !important;
  border: 1px solid #e5e7eb !important;
  color: #4b5563 !important;
}

.btn-secondary:hover {
  background: #e5e7eb !important;
  color: #1f2937 !important;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: var(--radius-lg);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.blue { background: linear-gradient(135deg, #3b82f6, #2563eb); }
.stat-icon.green { background: linear-gradient(135deg, #10b981, #059669); }
.stat-icon.orange { background: linear-gradient(135deg, #f59e0b, #d97706); }
.stat-icon.purple { background: linear-gradient(135deg, #8b5cf6, #7c3aed); }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
}

/* 数据卡片 */
.data-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
  background: #ffffff;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.card-body {
  padding: 0;
}

/* 表格样式 */
:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: transparent;
  --el-table-row-hover-bg-color: rgba(37, 99, 235, 0.05);
  --el-table-text-color: #4b5563;
  --el-table-header-text-color: #4b5563;
  --el-table-border-color: #e5e7eb;
}

:deep(.el-table th.el-table__cell) {
  border-bottom: 1px solid #e5e7eb !important;
  padding: 14px 0;
}

:deep(.el-table td.el-table__cell) {
  border-bottom: 1px solid #e5e7eb !important;
  padding: 16px 0;
}

.code-cell {
  font-family: 'SF Mono', Monaco, monospace;
  font-size: 13px;
  color: var(--primary-light);
  background: rgba(37, 99, 235, 0.1);
  padding: 4px 10px;
  border-radius: var(--radius-sm);
}

.name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.account-name {
  color: #1f2937;
  font-weight: 500;
}

.parent-tag {
  font-size: 11px;
  color: #6b7280;
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 10px;
}

.direction-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.direction-badge.debit {
  background: rgba(59, 130, 246, 0.15);
  color: #60a5fa;
}

.direction-badge.credit {
  background: rgba(16, 185, 129, 0.15);
  color: #34d399;
}

.level-badge {
  color: #6b7280;
  font-size: 13px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background: rgba(16, 185, 129, 0.15);
  color: #34d399;
}

.status-badge.inactive {
  background: rgba(100, 116, 139, 0.15);
  color: #94a3b8;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.action-buttons .el-button {
  padding: 8px;
  border-radius: var(--radius-sm);
}

.action-buttons .el-button:hover {
  background: rgba(255, 255, 255, 0.05);
}

/* 分页样式 */
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid #e5e7eb;
}

/* 弹窗样式 */
.dialog-form {
  padding: 8px 0;
}

.direction-radio {
  display: flex;
  gap: 24px;
}

.radio-label {
  font-weight: 500;
}

.radio-label.debit { color: #60a5fa; }
.radio-label.credit { color: #34d399; }

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
