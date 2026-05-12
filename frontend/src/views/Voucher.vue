<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1 class="page-title">凭证管理</h1>
        <p class="page-desc">录入、审核、查询会计凭证</p>
      </div>
      <div class="header-actions">
        <el-button class="btn-secondary" @click="handleRefresh" :loading="refreshing">
          <el-icon v-if="!refreshing"><Refresh /></el-icon>
          {{ refreshing ? '刷新中...' : '刷新' }}
        </el-button>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增凭证
        </el-button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label>会计期间</label>
          <el-date-picker 
            v-model="filters.period" 
            type="month" 
            value-format="YYYY-MM" 
            placeholder="选择期间"
            style="width: 160px"
          />
        </div>
        <div class="filter-item">
          <label>凭证状态</label>
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已过账" value="POSTED" />
            <el-option label="已作废" value="VOID" />
          </el-select>
        </div>
        <div class="filter-item">
          <label>凭证号</label>
          <el-input v-model="filters.voucherNo" placeholder="输入凭证号" clearable style="width: 160px" />
        </div>
        <el-button type="primary" class="filter-btn" @click="loadData" :loading="tableLoading">
          查询
        </el-button>
        <el-button class="filter-btn" @click="resetFilters">重置</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-card">
      <div class="card-header">
        <div class="card-title">
          <el-icon><Document /></el-icon>
          <span>凭证列表</span>
          <span class="count-badge">{{ vouchers.length }}</span>
        </div>
      </div>
      <div class="card-body">
        <el-table 
          :data="vouchers" 
          :header-cell-style="{ background: 'transparent', color: '#4b5563', fontWeight: 600 }"
          v-loading="tableLoading"
          element-loading-text="加载中..."
        >
          <el-table-column prop="voucherNo" label="凭证号" width="160">
            <template #default="{ row }">
              <span class="voucher-no">{{ row.voucherNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="voucherDate" label="凭证日期" width="120">
            <template #default="{ row }">
              <span class="date-cell">{{ row.voucherDate }}</span>
            </template>
          </el-table-column>
          <el-table-column label="摘要" min-width="240">
            <template #default="{ row }">
              <span class="summary-cell">{{ row.entries?.[0]?.summary || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="借方合计" width="140" align="right">
            <template #default="{ row }">
              <span class="amount debit">{{ formatMoney(getDebitTotal(row)) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="贷方合计" width="140" align="right">
            <template #default="{ row }">
              <span class="amount credit">{{ formatMoney(getCreditTotal(row)) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="110" align="center">
            <template #default="{ row }">
              <span :class="['status-tag', row.status.toLowerCase()]">
                <span class="status-dot"></span>
                {{ statusText(row.status) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="preparer" label="制单人" width="100" />
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip content="查看详情" placement="top">
                  <el-button link @click="handleView(row)">
                    <el-icon><View /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip v-if="row.status === 'DRAFT'" content="编辑" placement="top">
                  <el-button link @click="handleEdit(row)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip v-if="row.status === 'DRAFT'" content="过账" placement="top">
                  <el-button link type="success" @click="handlePost(row)">
                    <el-icon><Check /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip v-if="row.status !== 'VOID'" content="作废" placement="top">
                  <el-button link type="danger" @click="handleVoid(row)">
                    <el-icon><Close /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="vouchers.length === 0" class="empty-state">
          <el-icon :size="48"><Document /></el-icon>
          <p>暂无凭证数据</p>
          <el-button type="primary" @click="handleAdd">新增凭证</el-button>
        </div>
        
        <!-- 分页 -->
        <div class="pagination-wrapper" v-if="vouchers.length > 0">
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

    <!-- 凭证弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle"
      width="1000px"
      top="3vh"
      :close-on-click-modal="false"
      class="voucher-dialog"
    >
      <div class="voucher-form">
        <!-- 凭证头部信息 -->
        <div class="voucher-header-form">
          <div class="form-row">
            <div class="form-item">
              <label>凭证日期 <span class="required">*</span></label>
              <el-date-picker 
                v-model="form.voucherDate" 
                type="date" 
                value-format="YYYY-MM-DD"
                :disabled="viewMode"
                style="width: 100%"
              />
            </div>
            <div class="form-item">
              <label>附件张数</label>
              <el-input-number 
                v-model="form.attachmentCount" 
                :min="0" 
                :disabled="viewMode"
                style="width: 100%"
              />
            </div>
            <div class="form-item">
              <label>制单人</label>
              <el-input 
                v-model="form.preparer" 
                placeholder="制单人姓名"
                :disabled="viewMode"
              />
            </div>
            <div class="form-item" v-if="form.voucherNo">
              <label>凭证号</label>
              <el-input :value="form.voucherNo" disabled />
            </div>
          </div>
        </div>

        <!-- 分录表格 -->
        <div class="entries-section">
          <div class="entries-header">
            <span class="entries-title">
              <el-icon><List /></el-icon>
              凭证分录
            </span>
            <el-button v-if="!viewMode" type="primary" size="small" @click="addEntry" class="add-entry-btn">
              <el-icon><Plus /></el-icon>
              添加分录
            </el-button>
          </div>

          <div class="entries-table-wrapper">
            <table class="entries-table">
              <thead>
                <tr>
                  <th style="width: 200px">摘要</th>
                  <th style="width: 280px">会计科目</th>
                  <th style="width: 150px">借方金额</th>
                  <th style="width: 150px">贷方金额</th>
                  <th v-if="!viewMode" style="width: 60px">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(entry, index) in form.entries" :key="index">
                  <td>
                    <el-input 
                      v-model="entry.summary" 
                      placeholder="输入摘要"
                      :disabled="viewMode"
                    />
                  </td>
                  <td>
                    <el-select 
                      v-model="entry.accountCode" 
                      placeholder="选择科目" 
                      filterable
                      :disabled="viewMode"
                      @change="(v) => onAccountChange(entry, v)"
                      style="width: 100%"
                    >
                      <el-option 
                        v-for="a in accounts" 
                        :key="a.code" 
                        :label="`${a.code} - ${a.name}`" 
                        :value="a.code" 
                      />
                    </el-select>
                  </td>
                  <td>
                    <el-input-number 
                      v-model="entry.debitAmount" 
                      :precision="2" 
                      :min="0" 
                      :controls="false"
                      :disabled="viewMode"
                      @change="() => { if(entry.debitAmount > 0) entry.creditAmount = 0 }"
                      placeholder="0.00"
                      style="width: 100%"
                    />
                  </td>
                  <td>
                    <el-input-number 
                      v-model="entry.creditAmount" 
                      :precision="2" 
                      :min="0" 
                      :controls="false"
                      :disabled="viewMode"
                      @change="() => { if(entry.creditAmount > 0) entry.debitAmount = 0 }"
                      placeholder="0.00"
                      style="width: 100%"
                    />
                  </td>
                  <td v-if="!viewMode">
                    <el-button 
                      link 
                      type="danger" 
                      @click="removeEntry(index)"
                      :disabled="form.entries.length <= 2"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </td>
                </tr>
              </tbody>
              <tfoot>
                <tr class="total-row">
                  <td colspan="2" class="total-label">合计</td>
                  <td class="total-amount debit">{{ formatMoney(totalDebit) }}</td>
                  <td class="total-amount credit">{{ formatMoney(totalCredit) }}</td>
                  <td v-if="!viewMode"></td>
                </tr>
              </tfoot>
            </table>
          </div>

          <!-- 借贷平衡状态 -->
          <div class="balance-status" :class="{ balanced: isBalanced, unbalanced: !isBalanced }">
            <el-icon v-if="isBalanced"><CircleCheck /></el-icon>
            <el-icon v-else><Warning /></el-icon>
            <span>{{ isBalanced ? '借贷平衡' : '借贷不平衡，差额：' + formatMoney(Math.abs(totalDebit - totalCredit)) }}</span>
          </div>
        </div>
      </div>

      <template #footer v-if="!viewMode">
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :disabled="!isBalanced" :loading="saving">
            {{ saving ? '保存中...' : '保存凭证' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { voucherApi, accountApi } from '../api'
import { usePagination } from '../composables/usePagination'
import { useLoading } from '../composables/useLoading'
import { formatMoney, getStatusText } from '../utils/format'
import { VOUCHER_STATUS } from '../utils/constants'

const vouchers = ref([])
const accounts = ref([])
const dialogVisible = ref(false)
const viewMode = ref(false)
const filters = ref({ period: '', status: '', voucherNo: '' })
const form = ref({ voucherDate: '', attachmentCount: 0, preparer: '', entries: [] })

const { currentPage, pageSize, total, handlePageChange, handleSizeChange } = usePagination(10)
const { loading: tableLoading, withLoading: withTableLoading } = useLoading()
const { loading: saving, withLoading: withSaving } = useLoading()
const { loading: refreshing, withLoading: withRefreshing } = useLoading()

const dialogTitle = computed(() => {
  if (viewMode.value) return '凭证详情'
  return form.value.id ? '编辑凭证' : '新增凭证'
})

const totalDebit = computed(() => form.value.entries.reduce((s, e) => s + (e.debitAmount || 0), 0))
const totalCredit = computed(() => form.value.entries.reduce((s, e) => s + (e.creditAmount || 0), 0))
const isBalanced = computed(() => Math.abs(totalDebit.value - totalCredit.value) < 0.01 && totalDebit.value > 0)

const getDebitTotal = (row) => row.entries?.reduce((s, e) => s + (e.debitAmount || 0), 0) || 0
const getCreditTotal = (row) => row.entries?.reduce((s, e) => s + (e.creditAmount || 0), 0) || 0

const loadData = async () => {
  await withTableLoading(async () => {
    const res = await voucherApi.page({ ...filters.value, page: currentPage.value, size: pageSize.value })
    vouchers.value = res.data?.list || []
    total.value = res.data?.total || 0
  })
}

const handleRefresh = async () => {
  await withRefreshing(async () => {
    await loadData()
    ElMessage.success('刷新成功')
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

const loadAccounts = async () => {
  try {
    const res = await accountApi.listEnabled()
    accounts.value = res.data || []
  } catch (e) {
    console.error('加载科目失败', e)
  }
}

const resetFilters = () => {
  filters.value = { period: '', status: '', voucherNo: '' }
  currentPage.value = 1
  loadData()
}

const createEntry = () => ({ summary: '', accountCode: '', accountName: '', debitAmount: 0, creditAmount: 0 })

const handleAdd = () => {
  viewMode.value = false
  form.value = { 
    voucherDate: new Date().toISOString().slice(0, 10), 
    attachmentCount: 0, 
    preparer: '', 
    entries: [createEntry(), createEntry()] 
  }
  dialogVisible.value = true
}

const handleView = async (row) => {
  viewMode.value = true
  const res = await voucherApi.getById(row.id)
  form.value = res.data
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  viewMode.value = false
  const res = await voucherApi.getById(row.id)
  form.value = res.data
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.voucherDate) {
    return ElMessage.warning('请选择凭证日期')
  }
  if (!isBalanced.value) {
    return ElMessage.error('借贷不平衡，无法保存')
  }
  
  await withSaving(async () => {
    await voucherApi.save(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  })
}

const handlePost = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入审核人姓名', '过账确认', {
      inputPlaceholder: '审核人',
      confirmButtonText: '确认过账',
      cancelButtonText: '取消'
    })
    await voucherApi.post(row.id, value)
    ElMessage.success('过账成功')
    loadData()
  } catch (e) {
    // 用户取消
  }
}

const handleVoid = async (row) => {
  await ElMessageBox.confirm('作废后凭证将无法恢复，确定要作废吗？', '作废确认', { 
    type: 'warning',
    confirmButtonText: '确认作废',
    cancelButtonText: '取消'
  })
  await voucherApi.void(row.id)
  ElMessage.success('作废成功')
  loadData()
}

const addEntry = () => form.value.entries.push(createEntry())
const removeEntry = (index) => form.value.entries.splice(index, 1)

const onAccountChange = (entry, code) => {
  const acc = accounts.value.find(a => a.code === code)
  entry.accountName = acc?.name || ''
}

onMounted(() => {
  loadData()
  loadAccounts()
})
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

/* 筛选卡片 */
.filter-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: var(--radius-lg);
  padding: 20px;
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-item label {
  font-size: 13px;
  color: #6b7280;
}

.filter-btn {
  min-width: 80px;
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

.count-badge {
  background: var(--primary-color);
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
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
  --el-table-border-color: #e5e7eb;
}

:deep(.el-table th.el-table__cell),
:deep(.el-table td.el-table__cell) {
  border-bottom: 1px solid var(--border-color) !important;
}

.voucher-no {
  font-family: 'SF Mono', Monaco, monospace;
  font-size: 13px;
  color: var(--primary-light);
  font-weight: 600;
}

.date-cell {
  color: #4b5563;
}

.summary-cell {
  color: #1f2937;
}

.amount {
  font-family: 'SF Mono', Monaco, monospace;
  font-weight: 600;
}

.amount.debit { color: #60a5fa; }
.amount.credit { color: #34d399; }

.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.draft {
  background: rgba(100, 116, 139, 0.15);
  color: #94a3b8;
}

.status-tag.posted {
  background: rgba(16, 185, 129, 0.15);
  color: #34d399;
}

.status-tag.void {
  background: rgba(239, 68, 68, 0.15);
  color: #f87171;
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
  gap: 4px;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #6b7280;
}

.empty-state .el-icon {
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  margin-bottom: 16px;
}

/* 分页样式 */
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid #e5e7eb;
}

/* 凭证弹窗 */
.voucher-form {
  max-height: 70vh;
  overflow-y: auto;
}

.voucher-header-form {
  background: #f5f7fa;
  border-radius: var(--radius-md);
  padding: 20px;
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-item label {
  font-size: 13px;
  color: #6b7280;
}

.form-item .required {
  color: var(--danger-color);
}

/* 分录区域 */
.entries-section {
  background: #f5f7fa;
  border-radius: var(--radius-md);
  padding: 20px;
}

.entries-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.entries-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}

.entries-table-wrapper {
  overflow-x: auto;
}

.entries-table {
  width: 100%;
  border-collapse: collapse;
}

.entries-table th {
  background: rgba(0, 0, 0, 0.02);
  padding: 12px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #4b5563;
  border-bottom: 1px solid #e5e7eb;
}

.entries-table td {
  padding: 8px;
  border-bottom: 1px solid #e5e7eb;
}

.entries-table .total-row {
  background: rgba(37, 99, 235, 0.05);
}

.entries-table .total-label {
  text-align: right;
  font-weight: 600;
  color: #1f2937;
  padding-right: 20px;
}

.entries-table .total-amount {
  font-family: 'SF Mono', Monaco, monospace;
  font-weight: 700;
  font-size: 15px;
  padding: 12px 8px;
}

.entries-table .total-amount.debit { color: #60a5fa; }
.entries-table .total-amount.credit { color: #34d399; }

.balance-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px 16px;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
}

.balance-status.balanced {
  background: rgba(16, 185, 129, 0.1);
  color: #34d399;
}

.balance-status.unbalanced {
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 添加分录按钮样式 */
.add-entry-btn {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark)) !important;
  border: none !important;
  border-radius: 6px !important;
  padding: 8px 16px !important;
  font-weight: 500 !important;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.25);
  transition: all 0.2s ease;
}

.add-entry-btn:hover {
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.35);
}

.add-entry-btn .el-icon {
  margin-right: 4px;
}
</style>
