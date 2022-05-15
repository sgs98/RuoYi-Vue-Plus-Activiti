<template>
  <el-dialog title="流程审批进度跟踪" :visible.sync="visible" align="center" append-to-body>
      <el-tabs  type="border-card" >
        <el-tab-pane label="业务单据" v-loading="loading">
            <component :is="currProcessForm" :businessKey="businessKey" :parentTaskId="parentTaskId" @closeForm="closeForm" :taskId="taskId"></component>
        </el-tab-pane>
        <el-tab-pane label="流程进度">
          <el-table :data="list" border stripe style="width: 100%" max-height="300">
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
            <el-image :src="url" style="font-size: 20px; margin: 50px;">
              <div slot="placeholder"><i class="el-icon-loading"></i> 流程审批历史图加载中……</div>
            </el-image>
        </el-tab-pane>
      </el-tabs>
  </el-dialog>

</template>

<script>
import api from '@/api/workflow/processInst'
import  leaveForm from "@/views/components/form/leaveForm";

export default {

    props: {
      processInstanceId: String, // 流程实例id
      businessKey: String, // 业务唯一标识
      taskId: String, // 任务id
      parentTaskId: String, // 父级任务id
      currProcessForm: String // 当前流程表单组件
    },

    components: {
      leaveForm
    },
    data() {
      return {
        loading: false,
        visible: false,
        url: null,
        list: []
      }
    },

    watch: {
      visible(newVal) {
          if(newVal) {
            // 审批历史数据
            this.getHistoryInfoList()
            // 通过流程实例id获取历史流程图
            this.url = process.env.VUE_APP_BASE_API+'/workflow/processInstance/getHistoryProcessImage?processInstanceId='+this.processInstanceId
          }
      }
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
          this.$emit("refresh")
        }
    }

}
</script>
