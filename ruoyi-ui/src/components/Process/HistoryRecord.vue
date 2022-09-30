<template>
    <el-table :data="list" style="width: 100%" max-height="570">
        <el-table-column label="流程审批历史记录" align="center">
            <el-table-column type="index" label="序号" align="center" width="50"></el-table-column>
            <el-table-column prop="name" label="任务名称" align="center" ></el-table-column>
            <el-table-column prop="nickName" label="办理人" align="center" ></el-table-column>
            <el-table-column prop="status" label="状态" align="center" ></el-table-column>
            <el-table-column prop="comment" label="审批意见" align="center" ></el-table-column>
            <el-table-column prop="fileList" width="100" label="附件" align="center" >
              <template slot-scope="scope">
                <el-popover v-if="scope.row.fileList.length>0" placement="right" trigger="click">
                  <el-table :data="scope.row.fileList">
                    <el-table-column width="200" prop="name" :show-overflow-tooltip="true" label="附件名称"></el-table-column>
                    <el-table-column width="50" label="操作">
                      <template slot-scope="scope">
                        <el-button size="mini" type="text" @click="downloadFile(scope.row)">下载</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <el-button size="mini" type="text" slot="reference">查看附件</el-button>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间" align="center" ></el-table-column>
            <el-table-column prop="endTime" label="结束时间" align="center" ></el-table-column>
            <el-table-column prop="runDuration" label="运行时长" align="center" ></el-table-column>
        </el-table-column>
    </el-table>
</template>

<script>
import apiProcessInst from '@/api/workflow/processInst'
export default {
    props: {
      processInstanceId: String,
      editMessage: {
        type: Boolean,
        default: false
      },
    },
    data() {
      return {
        loading: false,
        list: []
      }
    },
    watch: {
      processInstanceId: {
        handler(newVal,oldVal){
          if(newVal) {
            this.loading = true
            // 审批历史数据
            this.getHistoryInfoList()
          }
        },
        immediate: true,
        deep:true
      }
    },

    methods: {
        // 查询审批历史记录
        async getHistoryInfoList() {
            const { data } = await apiProcessInst.getHistoryInfoList(this.processInstanceId)
            this.list = data
            this.loading = false
        },
        // 附件下载
        downloadFile(row) {
          this.download('workflow/task/downloadAttachment/'+row.id,{}, row.name+`${new Date().getTime()}`+'.'+row.type)
        }
    }

}
</script>
<style>
/* 修改滚动条样式 */
.el-table__body-wrapper::-webkit-scrollbar-thumb {
	border-radius: 10px;
}
.el-table__body-wrapper::-webkit-scrollbar {
  width: 5px;
}
</style>
