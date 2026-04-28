import request from './request'
import type { ApiResponse } from './types'

export interface DeviceInfo {
  id?: number
  deviceNo: string
  deviceName: string
  categoryId?: number
  model?: string
  serialNo?: string
  manufacturer?: string
  location?: string
  department?: string
  responsiblePerson?: string
  responsiblePhone?: string
  status?: number
  onlineStatus?: number
  purchaseDate?: string
  warrantyExpire?: string
  purchasePrice?: number
  tags?: string
  remark?: string
  createTime?: string
  updateTime?: string
  categoryName?: string
}

export interface DeviceCategory {
  id?: number
  name: string
  code: string
  parentId?: number
  description?: string
  sortOrder?: number
}

export interface DeviceQueryParams {
  deviceNo?: string
  deviceName?: string
  categoryId?: number
  status?: number
  department?: string
  page?: number
  size?: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// Device CRUD
export function listDevices(params: DeviceQueryParams) {
  return request.get<ApiResponse<PageResult<DeviceInfo>>>('/device', { params })
}

export function getDevice(id: number) {
  return request.get<ApiResponse<DeviceInfo>>(`/device/${id}`)
}

export function createDevice(data: DeviceInfo) {
  return request.post<ApiResponse<DeviceInfo>>('/device', data)
}

export function updateDevice(id: number, data: DeviceInfo) {
  return request.put<ApiResponse<DeviceInfo>>(`/device/${id}`, data)
}

export function deleteDevice(id: number) {
  return request.delete<ApiResponse<void>>(`/device/${id}`)
}

// Categories
export function listCategories() {
  return request.get<ApiResponse<DeviceCategory[]>>('/device/categories')
}

export function createCategory(data: DeviceCategory) {
  return request.post<ApiResponse<DeviceCategory>>('/device/categories', data)
}

export function deleteCategory(id: number) {
  return request.delete<ApiResponse<void>>(`/device/categories/${id}`)
}

// Export / Import
export function exportDevices(params: Omit<DeviceQueryParams, 'page' | 'size'>) {
  return request.get('/device/export', { params, responseType: 'blob' })
}

export function downloadTemplate() {
  return request.get('/device/template', { responseType: 'blob' })
}

export function importDevices(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<ApiResponse<string>>('/device/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// Device Status
export function getDeviceStatus(deviceNo: string) {
  return request.get<ApiResponse<Record<string, unknown>>>(`/device-status/${deviceNo}`)
}

export function getDeviceStatusSnapshot() {
  return request.get<ApiResponse<Record<string, unknown>>>('/device-status/snapshot')
}

export function getDeviceStatusSummary() {
  return request.get<ApiResponse<Record<string, unknown>[]>>('/device-status/summary')
}

export function getOnlineRateTrend(deviceId: number, hours = 24) {
  return request.get<ApiResponse<Record<string, unknown>[]>>(`/device-status/${deviceId}/trend`, { params: { hours } })
}
