import request from './request'
import type { ApiResponse } from './types'

export interface OverviewData {
  totalDevices: number
  onlineDevices: number
  onlineRate: number
  runningDevices: number
  faultDevices: number
}

export function getOverview() {
  return request.get<ApiResponse<OverviewData>>('/analysis/overview')
}

export function getFaultTypeStatForAnalysis() {
  return request.get<ApiResponse<Record<string, unknown>[]>>('/analysis/fault-type')
}

export function getFaultTrendForAnalysis(days = 30) {
  return request.get<ApiResponse<Record<string, unknown>[]>>('/analysis/fault-trend', { params: { days } })
}

export function getDeviceStatusDist() {
  return request.get<ApiResponse<Record<string, unknown>[]>>('/analysis/device-status-dist')
}
