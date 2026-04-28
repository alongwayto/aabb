<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDeviceStatusSummary, getOnlineRateTrend } from '@/api/device'

const summary = ref<Record<string, unknown>[]>([])
const trendDeviceId = ref<number | null>(null)

let trendChart: echarts.ECharts | null = null
const trendChartRef = ref<HTMLElement | null>(null)

let ws: WebSocket | null = null
const wsConnected = ref(false)
const wsMessages = ref<string[]>([])

function connectWs() {
  const proto = location.protocol === 'https:' ? 'wss' : 'ws'
  ws = new WebSocket(`${proto}://${location.host}/ws/device-status`)
  ws.onopen = () => {
    wsConnected.value = true
  }
  ws.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      wsMessages.value.unshift(JSON.stringify(msg, null, 2))
      if (wsMessages.value.length > 20) wsMessages.value.pop()
      if (msg.type === 'update' || msg.type === 'offline') {
        loadSummary()
      }
    } catch { /* ignore */ }
  }
  ws.onclose = () => {
    wsConnected.value = false
    setTimeout(connectWs, 5000)
  }
  ws.onerror = () => { ws?.close() }
}

async function loadSummary() {
  const res = await getDeviceStatusSummary()
  summary.value = (res.data?.data as Record<string, unknown>[]) ?? []
}

async function loadTrend() {
  if (!trendDeviceId.value) return
  const res = await getOnlineRateTrend(trendDeviceId.value, 24)
  const data = (res.data?.data as Record<string, unknown>[]) ?? []
  renderTrendChart(data)
}

function renderTrendChart(data: Record<string, unknown>[]) {
  nextTick(() => {
    if (!trendChartRef.value) return
    if (!trendChart) trendChart = echarts.init(trendChartRef.value)
    const times = data.map((d) => d['time_slot'] as string)
    const onlineRates = data.map((d) => d['online_rate'])
    const cpuUsages = data.map((d) => d['avg_cpu'])
    trendChart.setOption({
      title: { text: '设备在线率趋势（近24小时）' },
      tooltip: { trigger: 'axis' },
      legend: { data: ['在线率(%)', 'CPU均值(%)'] },
      xAxis: { type: 'category', data: times },
      yAxis: { type: 'value', max: 100 },
      series: [
        { name: '在线率(%)', type: 'line', data: onlineRates, smooth: true },
        { name: 'CPU均值(%)', type: 'line', data: cpuUsages, smooth: true }
      ]
    })
  })
}

onMounted(() => {
  loadSummary()
  connectWs()
})

onUnmounted(() => {
  ws?.close()
  trendChart?.dispose()
})
</script>

<template>
  <div class="device-monitor-page">
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span style="font-weight: 600; font-size: 16px">设备状态监控</span>
              <el-tag :type="wsConnected ? 'success' : 'danger'">
                {{ wsConnected ? 'WS已连接' : 'WS断开' }}
              </el-tag>
            </div>
          </template>

          <el-table :data="summary" border stripe>
            <el-table-column prop="device_no" label="设备编号" width="120" />
            <el-table-column prop="device_name" label="设备名称" min-width="150" />
            <el-table-column label="在线状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.online_status === 1 ? 'success' : 'info'">
                  {{ row.online_status === 1 ? '在线' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="温度" width="90">
              <template #default="{ row }">{{ row.temperature != null ? row.temperature + '°C' : '-' }}</template>
            </el-table-column>
            <el-table-column label="CPU%" width="80">
              <template #default="{ row }">{{ row.cpu_usage != null ? row.cpu_usage + '%' : '-' }}</template>
            </el-table-column>
            <el-table-column label="内存%" width="80">
              <template #default="{ row }">{{ row.memory_usage != null ? row.memory_usage + '%' : '-' }}</template>
            </el-table-column>
            <el-table-column prop="last_record_time" label="最后上报" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" link @click="() => { trendDeviceId = row.device_id; loadTrend() }">趋势</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="16">
        <el-card>
          <div ref="trendChartRef" style="height: 300px" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span style="font-weight:600">实时消息</span></template>
          <div style="height: 260px; overflow-y: auto; font-family: monospace; font-size: 11px">
            <div v-for="(msg, i) in wsMessages" :key="i" style="border-bottom: 1px solid #eee; padding: 4px 0">
              {{ msg }}
            </div>
            <div v-if="wsMessages.length === 0" style="color: #999; text-align: center; margin-top: 20px">暂无消息</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.device-monitor-page { padding: 20px; }
</style>
