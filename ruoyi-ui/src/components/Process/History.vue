<template>
    <div>
      <el-tabs  type="border-card" >
        <el-tab-pane label="审批意见" v-loading="loading">
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
                <el-table-column fixed="right" label="操作" v-if="editMessage" align="center" width="80">
                  <template slot-scope="scope">
                    <el-button @click="handleClick(scope.row)" type="text" v-if="scope.row.commentId" size="small">编辑意见</el-button>
                  </template>
                </el-table-column>
              </el-table-column>
            </el-table>
        </el-tab-pane>
        <el-tab-pane label="流程进度">
           <el-image v-if="processInstanceId" :src="url" style="font-size: 20px; margin: 50px;">
              <div slot="placeholder"><i class="el-icon-loading"></i> 流程审批历史图加载中……</div>
           </el-image>
        </el-tab-pane>
      </el-tabs>
      <el-dialog title="编辑意见" :close-on-click-modal="false" :visible.sync="dialogVisible" v-if="dialogVisible" append-to-body width="60%">
        <el-form>
          <el-form-item label="审批意见" prop="comment" label-width="120px">
            <el-input  type="textarea" v-model="comment" maxlength="300" placeholder="请输入意见" :autosize="{ minRows: 4 }" show-word-limit />
          </el-form-item>
          <el-form-item label="附件" prop="message" label-width="120px">
            <el-upload ref="redmineUpload" :file-list="attachmentList"
                        :multiple="true"
                        :limit="5"
                        action="'#'"
                        :on-change="handleFileChange"
                        :on-preview="downloadFile"
                        :before-remove="beforeRemove"
                        :auto-upload="false"
                        :show-file-list="true" >
            <el-button slot="trigger" size="small" type="primary" icon="el-icon-upload">点击上传</el-button>
            </el-upload>
        </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" size="small" @click="clickUpdate">确 定</el-button>
          <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        </span>
      </el-dialog>
    </div>
</template>

<script>
import apiProcessInst from '@/api/workflow/processInst'
import taskApi from '@/api/workflow/task'

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
        url: null,
        list: [],
        dialogVisible: false,
        commentId: undefined,
        comment: undefined,
        attachmentList: [],
        taskId: undefined
      }
    },
    watch: {
      processInstanceId: {
        handler(newVal,oldVal){
          if(newVal) {
            this.loading = true
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
            const { data } = await apiProcessInst.getHistoryInfoList(this.processInstanceId)
            this.list = data
            this.loading = false
        },
        // 打开编辑意见
        handleClick(row){
            this.attachmentList = []
            this.commentId = row.commentId
            this.comment = row.comment
            this.taskId = row.id
            if(row.fileList.length>0){
                this.attachmentList = row.fileList
            }
            this.dialogVisible = true
        },
        // 附件上传
        handleFileChange(file, fileList) {
            this.attachmentList = fileList
        },
        // 删除附件
        beforeRemove(file,fileList){
            this.$confirm('是否确认删除？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                fileList.forEach((item,index) => {
                    if(item.uid === file.uid){
                        fileList.splice(index,1)
                    }
                })
                if(fileList.length === 0){
                    this.attachmentList = []
                }
                if(file.id){
                    taskApi.deleteAttachment(file.id).then(response => {
                        this.$modal.msgSuccess("删除成功");
                        this.getHistoryInfoList()
                    })
                } 
            })
            return false
        },
        // 附件上传
        async submitUpload() {
            if(this.attachmentList.length>0){
                const formData = new FormData()
                this.attachmentList.forEach((file) => {
                    if(file.raw){
                        formData.append('file', file.raw)
                    }
                })
                await taskApi.editAttachment(formData,this.taskId,this.processInstanceId)
            }
        },
        // 编辑意见
        async clickUpdate(){
            // 上传附件
            this.submitUpload()
            const {code,msg} = await taskApi.editComment(this.commentId,this.comment)
            if(code === 200){
                this.$modal.msgSuccess(msg);
                this.dialogVisible = false
                this.getHistoryInfoList()
            }
        },
        // 附件下载
        downloadFile(row) {
            if(row.id){
                this.download('workflow/task/downloadAttachment/'+row.id,{}, row.name+`${new Date().getTime()}`+'.'+row.type)
            }
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
