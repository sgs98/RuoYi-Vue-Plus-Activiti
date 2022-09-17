<template>
    <el-tabs type="border-card" class="container-tab">
    <el-tab-pane label="业务单据" v-loading="loading">
        <component :is="currProcessForm" 
            :businessKey="businessKey" 
            :parentTaskId="parentTaskId" 
            @closeForm="closeForm" 
            :taskId="taskId" 
            :buildData="dynamicFormData.formText"
             v-model="dynamicFormData.formValue"
             :dynamicFormData="dynamicFormData"
         ></component>
    </el-tab-pane>
    <el-tab-pane label="审批意见" v-loading="loading">
        <el-table :data="list" border stripe style="width: 100%" max-height="570">
            <el-table-column label="流程审批历史记录" align="center">
            <el-table-column type="index" label="序号" align="center" width="50"></el-table-column>
            <el-table-column prop="name" label="任务名称" align="center" ></el-table-column>
            <el-table-column prop="nickName" label="办理人" align="center" ></el-table-column>
            <el-table-column prop="status" label="状态" align="center" ></el-table-column>
            <el-table-column prop="comment" label="审批意见" align="center" ></el-table-column>
            <el-table-column prop="startTime" label="开始时间" align="center" ></el-table-column>
            <el-table-column prop="endTime" label="结束时间" align="center" ></el-table-column>
            </el-table-column>
        </el-table>
    </el-tab-pane>
    <el-tab-pane label="流程进度">
        <el-image v-if="processInstanceId" :src="url" style="font-size: 20px; margin: 50px;">
            <div slot="placeholder"><i class="el-icon-loading"></i> 流程审批历史图加载中……</div>
        </el-image>
    </el-tab-pane>
    </el-tabs>
</template>

<script>
// 导入@/views/components/form 目录下所有组件
const allComponents = require.context('@/views/components/form', false,/\.vue$/)
// 组件名: 引用的组件
let components = {}
allComponents.keys().forEach(fileName => {
  let componentName = allComponents(fileName)
  components[fileName.replace(/^\.\/(.*)\.\w+$/, '$1')] = componentName.default
})
import api from '@/api/workflow/processInst'
export default {
    props: {
      processInstanceId: String, // 流程实例id
      businessKey: String, // 业务唯一标识
      taskId: String, // 任务id
      parentTaskId: String, // 父级任务id
      currProcessForm: String, // 当前流程表单组件
      dynamicFormData: {} //动态表单数据
    },
    components: components,
    data() {
      return {
        loading: false,
        visible: false,
        url: null,
        list: []
      }
    },
    created(){
        // 审批历史数据
        this.getHistoryInfoList()
        // 通过流程实例id获取历史流程图
        this.url = process.env.VUE_APP_BASE_API+'/workflow/processInstance/getHistoryProcessImage?processInstanceId='+this.processInstanceId
    },
    methods: {
        // 查询审批历史记录
        async getHistoryInfoList() {
            const { data } = await api.getHistoryInfoList(this.processInstanceId)
            this.list = data
        },
        // 关闭弹窗
        closeForm(){
          this.visible = false
          this.$emit("closeForm")
        }
    }

}
</script>
<style scoped>
.container-tab{
    height: calc(100vh - 155px);
    overflow-y: auto;
}
/* 修改滚动条样式 */
.container-tab::-webkit-scrollbar {
    width: 4px;
}
.container-tab::-webkit-scrollbar-thumb {
    border-radius: 10px;
}
</style>
