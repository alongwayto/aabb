<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getFaultTypeStatForAnalysis, getFaultTrendForAnalysis, type OverviewData } from '@/api/analysis'

const overview = ref<OverviewData>({
  totalDevices: 0,
  onlineDevices: 0,
  onlineRate: 0,
  runningDevices: 0,
  faultDevices: 0
})

const pieChartRef = ref<HTMLElement | null>(null)
const barChartRef = ref<HTMLElement | null>(null)
let pieChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null

async function loadOverview() {
  const res = await getOverview()
  overview.value = res.data?.data ?? overview.value
}

async function loadCharts() {
  const [typeRes, trendRes] = await Promise.all([
    getFaultTypeStatForAnalysis(),
    getFaultTrendForAnalysis(30)
  ])
  nextTick(() => {
    if (pieChartRef.value) {
      if (!pieChart) pieChart = echarts.init(pieChartRef.value)
      const typeData = (typeRes.data?.data ?? []) as Record<string, unknown>[]
      pieChart.setOption({
        title: { text: '故障类型分布', left: 'center' },
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: 0, type: 'scroll' },
        series: [{
          type: 'pie',
          radius: ['40%', '65%'],
          data: typeData.map(d => ({ name: d['fault_type'] as string, value: d['cnt'] }))
        }]
      })
    }
    if (barChartRef.value) {
      if (!barChart) barChart = echarts.init(barChartRef.value)
      const trendData = (trendRes.data?.data ?? []) as Record<string, unknown>[]
      barChart.setOption({
        title: { text: '近30天故障趋势', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: trendData.map(d => d['date_str'] as string), axisLabel: { rotate: 45 } },
        yAxis: { type: 'value' },
        series: [{ type: 'bar', data: trendData.map(d => d['cnt']), name: '故障数' }]
      })
    }
  })
}

onMounted(() => {
  loadOverview()
  loadCharts()
})
</script>

<template>
  <div class="analysis-page">
    <el-row :gutter="16" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card>
          <div style="text-align: center">
            <div style="font-size: 32px; font-weight: 700; color: #409EFF">{{ overview.totalDevices }}</div>
            <div style="color: #666; margin-top: 8px">设备总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div style="text-align: center">
            <div style="font-size: 32px; font-weight: 700; color: #67C23A">{{ overview.onlineDevices }}</div>
            <div style="color: #666; margin-top: 8px">在线设备</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div style="text-align: center">
            <div style="font-size: 32px; font-weight: 700; color: #E6A23C">{{ overview.onlineRate }}%</div>
            <div style="color: #666; margin-top: 8px">在线率</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div style="text-align: center">
            <div style="font-size: 32px; font-weight: 700; color: #F56C6C">{{ overview.faultDevices }}</div>
            <div style="color: #666; margin-top: 8px">故障设备</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <div ref="pieChartRef" style="height: 320px" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div ref="barChartRef" style="height: 320px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.analysis-page { padding: 20px; }
</style>
