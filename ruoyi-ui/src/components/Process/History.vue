<template>
    <div>
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
      <el-image v-if="processInstanceId" :src="url" style="font-size: 20px; margin: 50px;">
        <div slot="placeholder"><i class="el-icon-loading"></i> 流程审批历史图加载中……</div>
      </el-image>
    </div>
</template>

<script>
import api from '@/api/workflow/processInst'


export default {

    props: {
      processInstanceId: String,
    },


    data() {
      return {
        loading: false,
        url: null,
        list: [],
      }
    },

    watch: {
      processInstanceId: {
        handler(newVal,oldVal){
          if(newVal) {
            // 审批历史数据
            this.getHistoryInfoList()
            // 通过流程实例id获取历史流程图
            this.url = process.env.VUE_APP_BASE_API+'/workflow/processInstance/getHistoryProcessImage?processInstanceId='+newVal
          }
        },
        immediate: true,
        deep:true
      }
    },

    methods: {
        // 查询审批历史记录
        async getHistoryInfoList() {
            const { data } = await api.getHistoryInfoList(this.processInstanceId)
            this.list = data
        },
    }

}
</script>
