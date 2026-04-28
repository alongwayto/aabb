<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listDevices, deleteDevice, listCategories, exportDevices, importDevices, downloadTemplate,
  type DeviceInfo, type DeviceCategory
} from '@/api/device'

const router = useRouter()

const loading = ref(false)
const devices = ref<DeviceInfo[]>([])
const total = ref(0)
const categories = ref<DeviceCategory[]>([])

const query = ref({
  deviceNo: '',
  deviceName: '',
  categoryId: undefined as number | undefined,
  status: undefined as number | undefined,
  department: '',
  page: 1,
  size: 20
})

const statusOptions = [
  { label: '停用', value: 0 },
  { label: '运行', value: 1 },
  { label: '维修', value: 2 },
  { label: '报废', value: 3 }
]

function getStatusTag(status: number) {
  const map: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return map[status] ?? 'info'
}

function getStatusLabel(status: number) {
  return statusOptions.find(s => s.value === status)?.label ?? '未知'
}

async function loadData() {
  loading.value = true
  try {
    const res = await listDevices(query.value)
    devices.value = res.data?.data?.records ?? []
    total.value = res.data?.data?.total ?? 0
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const res = await listCategories()
  categories.value = res.data?.data ?? []
}

function handleSearch() {
  query.value.page = 1
  loadData()
}

function handleReset() {
  query.value = { deviceNo: '', deviceName: '', categoryId: undefined, status: undefined, department: '', page: 1, size: 20 }
  loadData()
}

async function handleDelete(row: DeviceInfo) {
  await ElMessageBox.confirm(`确认删除设备 ${row.deviceName}?`, '提示', { type: 'warning' })
  await deleteDevice(row.id!)
  ElMessage.success('删除成功')
  loadData()
}

async function handleExport() {
  const { page, size, ...params } = query.value
  const res = await exportDevices(params)
  const url = URL.createObjectURL(new Blob([res.data as BlobPart]))
  const link = document.createElement('a')
  link.href = url
  link.download = '设备列表.xlsx'
  link.click()
  URL.revokeObjectURL(url)
}

async function handleDownloadTemplate() {
  const res = await downloadTemplate()
  const url = URL.createObjectURL(new Blob([res.data as BlobPart]))
  const link = document.createElement('a')
  link.href = url
  link.download = '设备导入模板.xlsx'
  link.click()
  URL.revokeObjectURL(url)
}

const uploadRef = ref<HTMLInputElement | null>(null)

async function handleImport(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  const res = await importDevices(file)
  if (res.data?.code === 200) {
    ElMessage.success(res.data.message ?? '导入成功')
    loadData()
  } else {
    ElMessage.error(res.data?.message ?? '导入失败')
  }
  if (uploadRef.value) uploadRef.value.value = ''
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<template>
  <div class="device-list-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-weight: 600; font-size: 16px">设备管理</span>
          <div>
            <el-button type="primary" @click="router.push('/device/add')">新增设备</el-button>
            <el-button @click="handleExport">导出Excel</el-button>
            <el-button @click="handleDownloadTemplate">下载模板</el-button>
            <el-button @click="uploadRef?.click()">批量导入</el-button>
            <input ref="uploadRef" type="file" accept=".xlsx,.xls" style="display:none" @change="handleImport" />
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="query" style="margin-bottom: 12px">
        <el-form-item label="设备编号">
          <el-input v-model="query.deviceNo" placeholder="设备编号" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="query.deviceName" placeholder="设备名称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" clearable placeholder="全部" style="width: 120px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 100px">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="query.department" placeholder="部门" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="devices" v-loading="loading" stripe border>
        <el-table-column prop="deviceNo" label="设备编号" width="120" />
        <el-table-column prop="deviceName" label="设备名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="location" label="位置" min-width="120" />
        <el-table-column prop="department" label="部门" width="100" />
        <el-table-column prop="responsiblePerson" label="负责人" width="90" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="在线" width="70">
          <template #default="{ row }">
            <el-tag :type="row.onlineStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.onlineStatus === 1 ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="router.push(`/device/${row.id}`)">详情</el-button>
            <el-button type="primary" link @click="router.push(`/device/edit/${row.id}`)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.device-list-page {
  padding: 20px;
}
</style>
