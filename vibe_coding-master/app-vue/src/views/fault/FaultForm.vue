<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { reportFault, type FaultReport } from '@/api/fault'

const router = useRouter()
const formRef = ref()

const form = ref<FaultReport>({
  deviceId: 0,
  deviceNo: '',
  deviceName: '',
  faultType: '',
  faultLevel: 2,
  faultDesc: '',
  reporterName: ''
})

const rules = {
  deviceNo: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  faultType: [{ required: true, message: '请输入故障类型', trigger: 'blur' }],
  faultDesc: [{ required: true, message: '请描述故障现象', trigger: 'blur' }]
}

const levelOptions = [
  { label: '低', value: 1 },
  { label: '中', value: 2 },
  { label: '高', value: 3 },
  { label: '紧急', value: 4 }
]

async function handleSubmit() {
  await formRef.value?.validate()
  await reportFault(form.value)
  ElMessage.success('故障上报成功')
  router.push('/fault')
}
</script>

<template>
  <div class="fault-form-page">
    <el-card>
      <template #header><span style="font-weight: 600">上报故障</span></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" style="max-width: 600px">
        <el-form-item label="设备编号" prop="deviceNo">
          <el-input v-model="form.deviceNo" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" />
        </el-form-item>
        <el-form-item label="故障类型" prop="faultType">
          <el-input v-model="form.faultType" placeholder="如：硬件故障、网络异常、传感器异常" />
        </el-form-item>
        <el-form-item label="故障级别">
          <el-select v-model="form.faultLevel" style="width: 100%">
            <el-option v-for="l in levelOptions" :key="l.value" :label="l.label" :value="l.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" prop="faultDesc">
          <el-input v-model="form.faultDesc" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="上报人">
          <el-input v-model="form.reporterName" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.fault-form-page { padding: 20px; }
</style>
