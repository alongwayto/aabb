<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFault, assignFault, handleFault, resolveFault, closeFault, type FaultReport } from '@/api/fault'

const route = useRoute()
const router = useRouter()
const fault = ref<FaultReport | null>(null)
const loading = ref(false)

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待处理', type: 'warning' },
  1: { label: '已派单', type: 'primary' },
  2: { label: '处理中', type: '' },
  3: { label: '已解决', type: 'success' },
  4: { label: '已关闭', type: 'info' }
}
const levelMap: Record<number, string> = { 1: '低', 2: '中', 3: '高', 4: '紧急' }

const assignDialog = ref(false)
const handleDialog = ref(false)
const resolveDialog = ref(false)
const assignForm = ref({ assigneeId: 0, assigneeName: '' })
const handleForm = ref({ handlerId: 0, handlerName: '', handleDesc: '' })
const resolveForm = ref({ resolveDesc: '', cost: 0 })

async function loadFault() {
  loading.value = true
  const id = parseInt(route.params.id as string)
  const res = await getFault(id)
  fault.value = res.data?.data ?? null
  loading.value = false
}

async function doAssign() {
  await assignFault(fault.value!.id!, assignForm.value)
  ElMessage.success('派单成功')
  assignDialog.value = false
  loadFault()
}

async function doHandle() {
  await handleFault(fault.value!.id!, handleForm.value)
  ElMessage.success('已开始处理')
  handleDialog.value = false
  loadFault()
}

async function doResolve() {
  await resolveFault(fault.value!.id!, resolveForm.value)
  ElMessage.success('已标记解决')
  resolveDialog.value = false
  loadFault()
}

async function doClose() {
  await closeFault(fault.value!.id!)
  ElMessage.success('已关闭')
  loadFault()
}

onMounted(loadFault)
</script>

<template>
  <div class="fault-detail-page">
    <el-card v-loading="loading">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-weight: 600">故障详情</span>
          <div v-if="fault">
            <el-button v-if="fault.status === 0" type="primary" @click="assignDialog = true">派单</el-button>
            <el-button v-if="fault.status === 1" type="warning" @click="handleDialog = true">开始处理</el-button>
            <el-button v-if="fault.status === 2" type="success" @click="resolveDialog = true">解决</el-button>
            <el-button v-if="fault.status !== 4" type="info" @click="doClose">关闭</el-button>
            <el-button @click="router.back()">返回</el-button>
          </div>
        </div>
      </template>

      <el-descriptions v-if="fault" :column="2" border>
        <el-descriptions-item label="故障单号">{{ fault.faultNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusMap[fault.status ?? 0]?.type">{{ statusMap[fault.status ?? 0]?.label }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="设备编号">{{ fault.deviceNo }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ fault.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="故障类型">{{ fault.faultType }}</el-descriptions-item>
        <el-descriptions-item label="故障级别">{{ levelMap[fault.faultLevel ?? 1] }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ fault.faultDesc }}</el-descriptions-item>
        <el-descriptions-item label="上报人">{{ fault.reporterName }}</el-descriptions-item>
        <el-descriptions-item label="上报时间">{{ fault.reportTime }}</el-descriptions-item>
        <el-descriptions-item label="指派人">{{ fault.assigneeName }}</el-descriptions-item>
        <el-descriptions-item label="指派时间">{{ fault.assignTime }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ fault.handlerName }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ fault.handleTime }}</el-descriptions-item>
        <el-descriptions-item label="处理描述" :span="2">{{ fault.handleDesc }}</el-descriptions-item>
        <el-descriptions-item label="解决时间">{{ fault.resolveTime }}</el-descriptions-item>
        <el-descriptions-item label="维修费用">{{ fault.cost != null ? `¥${fault.cost}` : '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-dialog v-model="assignDialog" title="派单" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="处理人ID"><el-input-number v-model="assignForm.assigneeId" /></el-form-item>
        <el-form-item label="处理人名"><el-input v-model="assignForm.assigneeName" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialog = false">取消</el-button>
        <el-button type="primary" @click="doAssign">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="handleDialog" title="开始处理" width="400px">
      <el-form :model="handleForm" label-width="80px">
        <el-form-item label="处理人ID"><el-input-number v-model="handleForm.handlerId" /></el-form-item>
        <el-form-item label="处理人名"><el-input v-model="handleForm.handlerName" /></el-form-item>
        <el-form-item label="处理描述"><el-input v-model="handleForm.handleDesc" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialog = false">取消</el-button>
        <el-button type="primary" @click="doHandle">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resolveDialog" title="标记解决" width="400px">
      <el-form :model="resolveForm" label-width="80px">
        <el-form-item label="解决描述"><el-input v-model="resolveForm.resolveDesc" type="textarea" /></el-form-item>
        <el-form-item label="维修费用"><el-input-number v-model="resolveForm.cost" :precision="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resolveDialog = false">取消</el-button>
        <el-button type="primary" @click="doResolve">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.fault-detail-page { padding: 20px; }
</style>
