<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listFaults, closeFault, type FaultReport } from '@/api/fault'

const router = useRouter()
const loading = ref(false)
const faults = ref<FaultReport[]>([])
const total = ref(0)

const query = ref({
  deviceNo: '',
  faultType: '',
  status: undefined as number | undefined,
  faultLevel: undefined as number | undefined,
  page: 1,
  size: 20
})

const statusOptions = [
  { label: '待处理', value: 0 },
  { label: '已派单', value: 1 },
  { label: '处理中', value: 2 },
  { label: '已解决', value: 3 },
  { label: '已关闭', value: 4 }
]

const levelOptions = [
  { label: '低', value: 1 },
  { label: '中', value: 2 },
  { label: '高', value: 3 },
  { label: '紧急', value: 4 }
]

function getStatusInfo(status: number) {
  const map: Record<number, { label: string; type: string }> = {
    0: { label: '待处理', type: 'warning' },
    1: { label: '已派单', type: 'primary' },
    2: { label: '处理中', type: '' },
    3: { label: '已解决', type: 'success' },
    4: { label: '已关闭', type: 'info' }
  }
  return map[status] ?? { label: '未知', type: 'info' }
}

function getLevelInfo(level: number) {
  const map: Record<number, { label: string; type: string }> = {
    1: { label: '低', type: 'info' },
    2: { label: '中', type: 'warning' },
    3: { label: '高', type: 'danger' },
    4: { label: '紧急', type: 'danger' }
  }
  return map[level] ?? { label: '-', type: 'info' }
}

async function loadData() {
  loading.value = true
  try {
    const res = await listFaults(query.value)
    faults.value = res.data?.data?.records ?? []
    total.value = res.data?.data?.total ?? 0
  } finally {
    loading.value = false
  }
}

async function handleClose(row: FaultReport) {
  await ElMessageBox.confirm('确认关闭该故障单?', '提示', { type: 'warning' })
  await closeFault(row.id!)
  ElMessage.success('已关闭')
  loadData()
}

onMounted(loadData)
</script>

<template>
  <div class="fault-list-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-weight: 600; font-size: 16px">故障管理</span>
          <el-button type="primary" @click="router.push('/fault/add')">上报故障</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="query" style="margin-bottom: 12px">
        <el-form-item label="设备编号">
          <el-input v-model="query.deviceNo" clearable style="width: 130px" />
        </el-form-item>
        <el-form-item label="故障类型">
          <el-input v-model="query.faultType" clearable style="width: 130px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 100px">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="query.faultLevel" clearable placeholder="全部" style="width: 80px">
            <el-option v-for="l in levelOptions" :key="l.value" :label="l.label" :value="l.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="() => { query.page = 1; loadData() }">查询</el-button>
          <el-button @click="() => { query = { deviceNo: '', faultType: '', status: undefined, faultLevel: undefined, page: 1, size: 20 }; loadData() }">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="faults" v-loading="loading" stripe border>
        <el-table-column prop="faultNo" label="故障单号" width="180" />
        <el-table-column prop="deviceNo" label="设备编号" width="120" />
        <el-table-column prop="deviceName" label="设备名称" min-width="130" />
        <el-table-column prop="faultType" label="故障类型" width="120" />
        <el-table-column label="级别" width="70">
          <template #default="{ row }">
            <el-tag :type="getLevelInfo(row.faultLevel)?.type" size="small">{{ getLevelInfo(row.faultLevel)?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusInfo(row.status)?.type" size="small">{{ getStatusInfo(row.status)?.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reporterName" label="上报人" width="80" />
        <el-table-column prop="reportTime" label="上报时间" width="160" />
        <el-table-column prop="handlerName" label="处理人" width="80" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="router.push(`/fault/${row.id}`)">详情</el-button>
            <el-button v-if="row.status < 4" type="warning" link @click="handleClose(row)">关闭</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.fault-list-page { padding: 20px; }
</style>
