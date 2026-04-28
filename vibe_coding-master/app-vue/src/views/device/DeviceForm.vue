<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDevice, createDevice, updateDevice, listCategories, type DeviceInfo, type DeviceCategory } from '@/api/device'

const router = useRouter()
const route = useRoute()

const isEdit = ref(false)
const deviceId = ref<number | null>(null)
const categories = ref<DeviceCategory[]>([])

const form = ref<DeviceInfo>({
  deviceNo: '',
  deviceName: '',
  categoryId: undefined,
  model: '',
  serialNo: '',
  manufacturer: '',
  location: '',
  department: '',
  responsiblePerson: '',
  responsiblePhone: '',
  status: 0,
  onlineStatus: 0,
  purchaseDate: '',
  warrantyExpire: '',
  purchasePrice: undefined,
  tags: '',
  remark: ''
})

const formRef = ref()
const rules = {
  deviceNo: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }]
}

const statusOptions = [
  { label: '停用', value: 0 },
  { label: '运行', value: 1 },
  { label: '维修', value: 2 },
  { label: '报废', value: 3 }
]

async function loadCategories() {
  const res = await listCategories()
  categories.value = res.data?.data ?? []
}

async function loadDevice() {
  if (deviceId.value) {
    const res = await getDevice(deviceId.value)
    if (res.data?.data) {
      Object.assign(form.value, res.data.data)
    }
  }
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (isEdit.value && deviceId.value) {
    await updateDevice(deviceId.value, form.value)
    ElMessage.success('更新成功')
  } else {
    await createDevice(form.value)
    ElMessage.success('创建成功')
  }
  router.push('/device')
}

onMounted(() => {
  loadCategories()
  const id = route.params.id
  if (id && !Array.isArray(id)) {
    isEdit.value = true
    deviceId.value = parseInt(id)
    loadDevice()
  }
  // Check if route is /device/add
  if (route.path === '/device/add') {
    isEdit.value = false
    deviceId.value = null
  }
})
</script>

<template>
  <div class="device-form-page">
    <el-card>
      <template #header>
        <span style="font-weight: 600">{{ isEdit ? '编辑设备' : '新增设备' }}</span>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 700px">
        <el-form-item label="设备编号" prop="deviceNo">
          <el-input v-model="form.deviceNo" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" />
        </el-form-item>
        <el-form-item label="设备分类">
          <el-select v-model="form.categoryId" clearable placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="form.model" />
        </el-form-item>
        <el-form-item label="序列号">
          <el-input v-model="form.serialNo" />
        </el-form-item>
        <el-form-item label="生产厂商">
          <el-input v-model="form.manufacturer" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-input v-model="form.department" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.responsiblePerson" />
        </el-form-item>
        <el-form-item label="负责人电话">
          <el-input v-model="form.responsiblePhone" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="购入日期">
          <el-date-picker v-model="form.purchaseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="保修到期日">
          <el-date-picker v-model="form.warrantyExpire" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="购置金额">
          <el-input-number v-model="form.purchasePrice" :precision="2" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.device-form-page { padding: 20px; }
</style>
