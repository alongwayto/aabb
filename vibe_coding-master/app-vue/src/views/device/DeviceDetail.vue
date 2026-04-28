<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getDevice, type DeviceInfo } from '@/api/device'

const route = useRoute()
const router = useRouter()
const device = ref<DeviceInfo | null>(null)

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '停用', type: 'info' },
  1: { label: '运行', type: 'success' },
  2: { label: '维修', type: 'warning' },
  3: { label: '报废', type: 'danger' }
}

async function loadDevice() {
  const id = parseInt(route.params.id as string)
  const res = await getDevice(id)
  device.value = res.data?.data ?? null
}

onMounted(loadDevice)
</script>

<template>
  <div class="device-detail-page">
    <el-card v-if="device">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-weight: 600">设备详情</span>
          <div>
            <el-button type="primary" @click="router.push(`/device/edit/${device.id}`)">编辑</el-button>
            <el-button @click="router.back()">返回</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="设备编号">{{ device.deviceNo }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ device.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ device.model }}</el-descriptions-item>
        <el-descriptions-item label="序列号">{{ device.serialNo }}</el-descriptions-item>
        <el-descriptions-item label="生产厂商">{{ device.manufacturer }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ device.location }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ device.department }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ device.responsiblePerson }}</el-descriptions-item>
        <el-descriptions-item label="负责人电话">{{ device.responsiblePhone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusMap[device.status ?? 0]?.type">{{ statusMap[device.status ?? 0]?.label }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="在线状态">
          <el-tag :type="device.onlineStatus === 1 ? 'success' : 'info'">
            {{ device.onlineStatus === 1 ? '在线' : '离线' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="购入日期">{{ device.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="保修到期">{{ device.warrantyExpire }}</el-descriptions-item>
        <el-descriptions-item label="购置金额">{{ device.purchasePrice != null ? `¥${device.purchasePrice}` : '-' }}</el-descriptions-item>
        <el-descriptions-item label="标签">{{ device.tags }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ device.remark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ device.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ device.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<style scoped>
.device-detail-page { padding: 20px; }
</style>
