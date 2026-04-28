import request from './request'
import type { ApiResponse } from './types'

export interface FaultReport {
  id?: number
  faultNo?: string
  deviceId: number
  deviceNo: string
  deviceName?: string
  faultType?: string
  faultLevel?: number
  faultDesc?: string
  reporterId?: number
  reporterName?: string
  reportTime?: string
  assigneeId?: number
  assigneeName?: string
  assignTime?: string
  handlerId?: number
  handlerName?: string
  handleTime?: string
  handleDesc?: string
  resolveTime?: string
  status?: number
  cost?: number
  createTime?: string
  updateTime?: string
}

export interface FaultQueryParams {
  deviceNo?: string
  faultType?: string
  status?: number
  faultLevel?: number
  page?: number
  size?: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

export function listFaults(params: FaultQueryParams) {
  return request.get<ApiResponse<PageResult<FaultReport>>>('/fault', { params })
}

export function getFault(id: number) {
  return request.get<ApiResponse<FaultReport>>(`/fault/${id}`)
}

export function reportFault(data: FaultReport) {
  return request.post<ApiResponse<FaultReport>>('/fault', data)
}

export function assignFault(id: number, data: { assigneeId: number; assigneeName: string }) {
  return request.put<ApiResponse<FaultReport>>(`/fault/${id}/assign`, data)
}

export function handleFault(id: number, data: { handlerId: number; handlerName: string; handleDesc: string }) {
  return request.put<ApiResponse<FaultReport>>(`/fault/${id}/handle`, data)
}

export function resolveFault(id: number, data: { resolveDesc: string; cost?: number }) {
  return request.put<ApiResponse<FaultReport>>(`/fault/${id}/resolve`, data)
}

export function closeFault(id: number) {
  return request.put<ApiResponse<FaultReport>>(`/fault/${id}/close`)
}

export function getFaultTypeStat() {
  return request.get<ApiResponse<Record<string, unknown>[]>>('/fault/stat/type')
}

export function getFaultTrend(days = 30) {
  return request.get<ApiResponse<Record<string, unknown>[]>>('/fault/stat/trend', { params: { days } })
}
