<template>
    <el-dialog title="审批驳回" :visible.sync="visible" :close-on-click-modal="false" width="600px" append-to-body destroy-on-close>
        <el-form v-loading="loading" :rules="rules" ref="formData" :model="formData" status-icon>
            <el-form-item label="审批意见" prop="message" label-width="120px">
                <el-input type="textarea" v-model="formData.comment" maxlength="300"  placeholder="请输入审批意见" :autosize="{ minRows: 4}" show-word-limit></el-input>
            </el-form-item>
            <el-form-item label="驳回到" prop="targetActivityId" label-width="120px">
                <el-select v-model="formData.targetActivityId" placeholder="请选择">
                    <el-option v-for="item in options" :key="item.nodeId"
                        :value="item.nodeId" :label="item.nodeName">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item align="center">
                <el-button type="primary" @click="submitForm('formData')" size="mini">提交</el-button>
                <el-button size="mini" @click="visible=false">取消</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>
<script>
import api from '@/api/workflow/task'

export default {
    props: {
        taskId: String,
        backNodeList: Array,
    },

    data() {
        return {
            visible: false, // 弹出窗口，true弹出
            loading: false,
            formData: {
                targetActivityId: null,
                comment: null,
            },
            rules: {
                targetActivityId: [
                    {required: true, message: '请选择驳回目标', trigger: 'change'},
                ]
            },
            options: [], // 驳回目标选项
        }
    },

    watch: {
      async visible(newVal) {
          if(newVal) {
            this.options = []
            this.formData.targetActivityId = this.backNodeList[0].nodeId
            this.options = this.backNodeList
          }
      }
    },

    methods: {
        // 提交表单数据
        submitForm(formName) {
            this.$refs[formName].validate( async (valid) => {
                if (valid) {
                    // 校验通过，提交表单数据
                    this.loading = true
                    try {
                      const params = {
                        taskId: this.taskId,
                        targetActivityId: this.formData.targetActivityId,
                        comment: this.formData.comment
                      }
                      let response = await api.backProcess(params)
                      if(response.code === 200) {
                          this.$message.success('提交成功')
                          // 将表单清空
                          this.$refs['formData'].resetFields()
                          // 关闭窗口
                          this.visible = false
                          // 事件
                          this.$emit("callBack")
                      }
                      this.loading = false
                    } catch(e) {
                      this.loading = false
                    }
                }
            })
        },

    }
}
</script>
