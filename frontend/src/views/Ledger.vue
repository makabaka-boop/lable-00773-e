<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1 class="page-title">账簿查询</h1>
        <p class="page-desc">查询明细账、科目余额表等账簿报表</p>
      </div>
    </div>

    <!-- 查询类型切换 -->
    <div class="query-tabs">
      <div 
        class="tab-item" 
        :class="{ active: queryType === 'detail' }"
        @click="queryType = 'detail'"
      >
        <el-icon><Notebook /></el-icon>
        <span>明细账</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ active: queryType === 'balance' }"
        @click="queryType = 'balance'"
      >
        <el-icon><DataBoard /></el-icon>
        <span>科目余额表</span>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label>会计期间 <span class="required">*</span></label>
          <el-date-picker 
            v-model="filters.period" 
            type="month" 
            value-format="YYYY-MM" 
            placeholder="选择期间"
            style="width: 160px"
          />
        </div>
        <div class="filter-item" v-if="queryType === 'detail'">
          <label>会计科目 <span class="required">*</span></label>
          <el-select 
            v-model="filters.accountCode" 
            placeholder="选择科目" 
            filterable 
            style="width: 280px"
          >
            <el-option 
              v-for="a in accounts" 
              :key="a.code" 
              :label="`${a.code} - ${a.name}`" 
              :value="a.code" 
            />
          </el-select>
        </div>
        <el-button type="primary" class="filter-btn" @click="handleQuery" :loading="queryLoading">
          查询
        </el-button>
      </div>
    </div>

    <!-- 明细账 -->
    <div class="data-card" v-if="queryType === 'detail'">
      <div class="card-header">
        <div class="card-title">
          <el-icon><Notebook /></el-icon>
          <span>明细账</span>
          <span class="sub-title" v-if="currentAccountName">- {{ currentAccountName }}</span>
        </div>
        <div class="card-info" v-if="filters.period">
          <span>会计期间：{{ filters.period }}</span>
        </div>
      </div>
      <div class="card-body" v-loading="queryLoading" element-loading-text="查询中...">
        <table class="ledger-table" v-if="detailData.length > 0">
          <thead>
            <tr>
              <th style="width: 110px">日期</th>
              <th style="width: 140px">凭证号</th>
              <th>摘要</th>
              <th style="width: 130px" class="text-right">借方金额</th>
              <th style="width: 130px" class="text-right">贷方金额</th>
              <th style="width: 80px" class="text-center">方向</th>
              <th style="width: 130px" class="text-right">余额</th>
            </tr>
          </thead>
          <tbody>
            <!-- 期初余额行 -->
            <tr class="opening-row">
              <td colspan="3">期初余额</td>
              <td class="text-right">-</td>
              <td class="text-right">-</td>
              <td class="text-center">
                <span class="direction-tag">{{ openingBalance >= 0 ? '借' : '贷' }}</span>
              </td>
              <td class="text-right amount">{{ formatMoney(Math.abs(openingBalance)) }}</td>
            </tr>
            <!-- 明细行 -->
            <tr v-for="(entry, index) in detailData" :key="index">
              <td>{{ entry.voucherDate }}</td>
              <td>
                <span class="voucher-link">{{ entry.voucherNo }}</span>
              </td>
              <td>{{ entry.summary }}</td>
              <td class="text-right">
                <span class="amount debit" v-if="entry.debitAmount">{{ formatMoney(entry.debitAmount) }}</span>
                <span v-else>-</span>
              </td>
              <td class="text-right">
                <span class="amount credit" v-if="entry.creditAmount">{{ formatMoney(entry.creditAmount) }}</span>
                <span v-else>-</span>
              </td>
              <td class="text-center">
                <span class="direction-tag">{{ getDirection(index) }}</span>
              </td>
              <td class="text-right amount">{{ formatMoney(getBalance(index)) }}</td>
            </tr>
          </tbody>
          <tfoot>
            <tr class="total-row">
              <td colspan="3">本期合计</td>
              <td class="text-right amount debit">{{ formatMoney(periodDebit) }}</td>
              <td class="text-right amount credit">{{ formatMoney(periodCredit) }}</td>
              <td></td>
              <td></td>
            </tr>
            <tr class="closing-row">
              <td colspan="3">期末余额</td>
              <td class="text-right">-</td>
              <td class="text-right">-</td>
              <td class="text-center">
                <span class="direction-tag">{{ closingBalance >= 0 ? '借' : '贷' }}</span>
              </td>
              <td class="text-right amount">{{ formatMoney(Math.abs(closingBalance)) }}</td>
            </tr>
          </tfoot>
        </table>

        <div v-else class="empty-state">
          <template v-if="!queryLoading">
            <el-icon :size="48"><Notebook /></el-icon>
            <p>请选择会计期间和科目进行查询</p>
          </template>
        </div>
      </div>
    </div>

    <!-- 科目余额表 -->
    <div class="data-card" v-if="queryType === 'balance'">
      <div class="card-header">
        <div class="card-title">
          <el-icon><DataBoard /></el-icon>
          <span>科目余额表</span>
        </div>
        <div class="card-info" v-if="filters.period">
          <span>会计期间：{{ filters.period }}</span>
        </div>
      </div>
      <div class="card-body" v-loading="queryLoading" element-loading-text="查询中...">
        <table class="balance-table" v-if="balanceData.length > 0">
          <thead>
            <tr>
              <th rowspan="2" style="width: 120px">科目编码</th>
              <th rowspan="2">科目名称</th>
              <th colspan="2" class="text-center group-header">期初余额</th>
              <th colspan="2" class="text-center group-header">本期发生</th>
              <th colspan="2" class="text-center group-header">期末余额</th>
            </tr>
            <tr>
              <th class="text-right sub-header" style="width: 120px">借方</th>
              <th class="text-right sub-header" style="width: 120px">贷方</th>
              <th class="text-right sub-header" style="width: 120px">借方</th>
              <th class="text-right sub-header" style="width: 120px">贷方</th>
              <th class="text-right sub-header" style="width: 120px">借方</th>
              <th class="text-right sub-header" style="width: 120px">贷方</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in balanceData" :key="item.accountCode">
              <td>
                <span class="code-cell">{{ item.accountCode }}</span>
              </td>
              <td>{{ getAccountName(item.accountCode) }}</td>
              <td class="text-right">
                <span class="amount" v-if="item.openingDebit">{{ formatMoney(item.openingDebit) }}</span>
              </td>
              <td class="text-right">
                <span class="amount" v-if="item.openingCredit">{{ formatMoney(item.openingCredit) }}</span>
              </td>
              <td class="text-right">
                <span class="amount debit" v-if="item.currentDebit">{{ formatMoney(item.currentDebit) }}</span>
              </td>
              <td class="text-right">
                <span class="amount credit" v-if="item.currentCredit">{{ formatMoney(item.currentCredit) }}</span>
              </td>
              <td class="text-right">
                <span class="amount" v-if="item.closingDebit">{{ formatMoney(item.closingDebit) }}</span>
              </td>
              <td class="text-right">
                <span class="amount" v-if="item.closingCredit">{{ formatMoney(item.closingCredit) }}</span>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-else class="empty-state">
          <template v-if="!queryLoading">
            <el-icon :size="48"><DataBoard /></el-icon>
            <p>请选择会计期间进行查询</p>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ledgerApi, accountApi } from '../api'

const queryType = ref('detail')
const filters = ref({ period: '', accountCode: '' })
const accounts = ref([])
const detailData = ref([])
const balanceData = ref([])
const openingBalance = ref(0)
const queryLoading = ref(false)

// 分页相关（暂时保留，未来可能用于其他功能）

const currentAccountName = computed(() => {
  const acc = accounts.value.find(a => a.code === filters.value.accountCode)
  return acc ? `${acc.code} - ${acc.name}` : ''
})

const hasData = computed(() => {
  return queryType.value === 'detail' ? detailData.value.length > 0 : balanceData.value.length > 0
})

const periodDebit = computed(() => detailData.value.reduce((s, e) => s + (e.debitAmount || 0), 0))
const periodCredit = computed(() => detailData.value.reduce((s, e) => s + (e.creditAmount || 0), 0))
const closingBalance = computed(() => openingBalance.value + periodDebit.value - periodCredit.value)

const formatMoney = (v) => (v || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })

const getAccountName = (code) => {
  const acc = accounts.value.find(a => a.code === code)
  return acc?.name || ''
}

const getBalance = (index) => {
  let balance = openingBalance.value
  for (let i = 0; i <= index; i++) {
    const entry = detailData.value[i]
    balance += (entry.debitAmount || 0) - (entry.creditAmount || 0)
  }
  return Math.abs(balance)
}

const getDirection = (index) => {
  let balance = openingBalance.value
  for (let i = 0; i <= index; i++) {
    const entry = detailData.value[i]
    balance += (entry.debitAmount || 0) - (entry.creditAmount || 0)
  }
  if (Math.abs(balance) < 0.01) return '平'
  return balance >= 0 ? '借' : '贷'
}

const handleQuery = async () => {
  if (!filters.value.period) {
    return ElMessage.warning('请选择会计期间')
  }
  
  queryLoading.value = true
  try {
    if (queryType.value === 'detail') {
      if (!filters.value.accountCode) {
        queryLoading.value = false
        return ElMessage.warning('请选择会计科目')
      }
      // 明细账不分页
      const res = await ledgerApi.detail(filters.value.accountCode, filters.value.period)
      const data = res.data
      detailData.value = data.entries || []
      openingBalance.value = (data.balance?.openingDebit || 0) - (data.balance?.openingCredit || 0)
    } else {
      // 科目余额表也不分页
      const res = await ledgerApi.balance(filters.value.period)
      balanceData.value = res.data || []
    }
    ElMessage.success('查询完成')
  } catch (e) {
    console.error('查询失败', e)
  } finally {
    queryLoading.value = false
  }
}

const loadAccounts = async () => {
  try {
    const res = await accountApi.listEnabled()
    accounts.value = res.data || []
  } catch (e) {
    console.error('加载科目失败', e)
  }
}

onMounted(loadAccounts)
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

/* 查询类型切换 */
.query-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: var(--radius-md);
  color: #4b5563;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-item:hover {
  background: #f5f7fa;
  color: #1f2937;
}

.tab-item.active {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border-color: var(--primary-color);
  color: white;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
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

.filter-item .required {
  color: var(--danger-color);
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
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.card-title .sub-title {
  color: var(--primary-light);
  font-weight: 500;
}

.card-info {
  font-size: 13px;
  color: #6b7280;
}

.card-body {
  padding: 0;
  overflow-x: auto;
}

/* 明细账表格 */
.ledger-table {
  width: 100%;
  border-collapse: collapse;
}

.ledger-table th {
  background: rgba(0, 0, 0, 0.02);
  padding: 14px 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #4b5563;
  border-bottom: 1px solid #e5e7eb;
}

.ledger-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
  color: #4b5563;
  font-size: 13px;
}

.ledger-table .text-right { text-align: right; }
.ledger-table .text-center { text-align: center; }

.ledger-table .opening-row,
.ledger-table .closing-row {
  background: rgba(37, 99, 235, 0.05);
}

.ledger-table .opening-row td,
.ledger-table .closing-row td {
  font-weight: 600;
  color: #1f2937;
}

.ledger-table .total-row {
  background: rgba(16, 185, 129, 0.05);
}

.ledger-table .total-row td {
  font-weight: 600;
  color: #1f2937;
}

.voucher-link {
  color: var(--primary-light);
  cursor: pointer;
}

.voucher-link:hover {
  text-decoration: underline;
}

.direction-tag {
  display: inline-block;
  padding: 2px 10px;
  background: #f5f7fa;
  border-radius: 10px;
  font-size: 12px;
  color: #4b5563;
}

.amount {
  font-family: 'SF Mono', Monaco, monospace;
  font-weight: 500;
}

.amount.debit { color: #60a5fa; }
.amount.credit { color: #34d399; }

/* 科目余额表 */
.balance-table {
  width: 100%;
  border-collapse: collapse;
}

.balance-table th {
  background: rgba(0, 0, 0, 0.02);
  padding: 12px 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #4b5563;
  border-bottom: 1px solid #e5e7eb;
}

.balance-table .group-header {
  background: rgba(37, 99, 235, 0.1);
  color: var(--primary-light);
}

.balance-table .sub-header {
  background: rgba(0, 0, 0, 0.15);
  font-size: 12px;
}

.balance-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
  color: #4b5563;
  font-size: 13px;
}

.balance-table .text-right { text-align: right; }

.code-cell {
  font-family: 'SF Mono', Monaco, monospace;
  font-size: 12px;
  color: var(--primary-light);
  background: rgba(37, 99, 235, 0.1);
  padding: 3px 8px;
  border-radius: var(--radius-sm);
}

/* 空状态 */
.empty-state {
  padding: 80px 20px;
  text-align: center;
  color: #6b7280;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-state .el-icon {
  margin-bottom: 16px;
  opacity: 0.3;
}

.empty-state p {
  font-size: 14px;
}

/* 分页样式 */
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid #e5e7eb;
}
</style>
