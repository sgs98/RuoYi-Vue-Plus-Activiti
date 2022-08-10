<template>
    <div class="app-container">
         <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="100px">
          <el-form-item label="流程名称" prop="name">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入流程名称"
              clearable
              size="small"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="任务发起人" prop="key">
            <el-input
              v-model="queryParams.startUserId"
              placeholder="请输入任务发起人"
              clearable
              size="small"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

         <el-row :gutter="10" class="mb8">
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column fixed align="center" type="index" label="序号" width="50"></el-table-column>
            <el-table-column fixed align="center" prop="name" label="流程定义名称" width="150"></el-table-column>
            <el-table-column  align="center" prop="processDefinitionKey" label="流程定义KEY" width="120"></el-table-column>
            <el-table-column align="center" prop="processDefinitionVersion" label="版本号" width="90" >
              <template slot-scope="{row}"> v{{row.processDefinitionVersion}}.0</template>
            </el-table-column>
            <el-table-column  align="center" prop="startUserNickName" label="流程发起人" width="120"></el-table-column>
            <el-table-column  align="center" prop="isSuspended" label="流程状态" width="75">
              <template slot-scope="scope">
                <el-tag type="success" v-if="scope.row.isSuspended=='激活'">激活</el-tag>
                <el-tag type="danger" v-else>挂起</el-tag>
              </template>
            </el-table-column>
            <el-table-column  align="center" prop="currTaskInfo" :show-overflow-tooltip="true" label="当前办理人"></el-table-column>
            <el-table-column  align="center" prop="businessKey" :show-overflow-tooltip="true" label="流程关联业务ID" width="150"></el-table-column>
            <el-table-column  align="center" prop="startTime" :show-overflow-tooltip="true" label="流程启动时间" width="150"></el-table-column>
            <el-table-column  align="center" prop="actBusinessStatus.suspendedReason" :show-overflow-tooltip="true" label="挂起或激活原因" width="180"></el-table-column>
            <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-row :gutter="10" class="mb8">
                <el-col :span="1.5">
                  <el-button
                  v-if="scope.row.isSuspended == '激活'"
                  @click="openDialog(scope.row)"
                  type="text"
                  size="mini"
                  icon="el-icon-lock"
                >挂起</el-button>
                <el-button
                  v-else type="text"
                  @click="openDialog(scope.row)"
                  size="mini"
                  icon="el-icon-unlock"
                >激活</el-button>
                </el-col>
                <el-col :span="1.5">
                  <el-button size="mini" type="text"  icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
                </el-col>
              </el-row>
              <el-row :gutter="10" class="mb8">
                <el-col :span="1.5">
                  <el-button size="mini" type="text"  icon="el-icon-circle-close" @click="invalidRuntimeProcessInst(scope.row)">作废</el-button>
                </el-col>
              </el-row>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList" />
        <el-dialog
          title="挂起或激活流程"
          :close-on-click-modal="false"
          :visible.sync="dialogVisible"
          v-if="dialogVisible"
          width="60%">
          <el-input  type="textarea" v-model="reason" maxlength="300" placeholder="请输入原因"
          :autosize="{ minRows: 4 }" show-word-limit ></el-input>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" size="small" v-loading = "buttonLoading" @click="clickUpdateProcDefState">确 定</el-button>
            <el-button size="small" @click="dialogVisible = false">取 消</el-button>
          </span>
        </el-dialog>
    </div>
</template>

<script>
  import api from '@/api/workflow/processInst'

  export default {
    data () {
      return {
        // 弹窗
        dialogVisible: false,
        // 原因
        reason: '',
        //按钮loading
        buttonLoading: false,
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // 是否显示弹出层
        open: false,
        // 表格数据
        dataList: [],
        // 查询参数
        queryParams: {
            pageNum: 1,
            pageSize: 10,
            name: undefined,
            key : undefined
        },
        // 流程实例对象
        processInstanceData: {}
      }
    },
    created() {
      this.getList();
    },
    methods: {
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.daterangeCreateTime = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length!==1
        this.multiple = !selection.length
      },
      //分页
      getList(){
          this.loading = true;
          api.getProcessInstRunningByPage(this.queryParams).then(response => {
            this.dataList = response.rows;
            this.total = response.total;
            this.loading = false;
          })
      },
      handleDelete(row){
        this.$modal.confirm('是否确认删除流程实例ID为"' + row.processInstanceId + '"的数据项？').then(() => {
           this.loading = true;
           return api.deleteRuntimeProcessAndHisInst(row.processInstanceId);
         }).then(() => {
           this.loading = false;
           this.getList();
           this.$modal.msgSuccess("删除成功");
         }).finally(() => {
           this.loading = false;
         });
      },
      invalidRuntimeProcessInst(row){
        this.$modal.confirm('是否确认作废流程实例ID为"' + row.processInstanceId + '"的数据项？').then(() => {
           this.loading = true;
           return api.deleteRuntimeProcessInst(row.processInstanceId);
         }).then(() => {
           this.loading = false;
           this.getList();
           this.$modal.msgSuccess("作废成功");
         }).finally(() => {
           this.loading = false;
         });
      },
      //打开弹窗
      openDialog(row){
        this.processInstanceData = row
        if(row.actBusinessStatus.suspendedReason === null){
          this.reason = ''
        }else{
          this.reason = row.actBusinessStatus.suspendedReason
        }
        this.dialogVisible = true
      },
      // 激活或挂起流程
      clickUpdateProcDefState(){
        let msg='';
        if(this.processInstanceData.isSuspended==='激活'){
          msg=`暂停后，此任务不允许往后流转，您确定挂起【${this.processInstanceData.name || this.processInstanceData.processDefinitionKey}】吗？`
        }else{
          msg=`启动后，此任务允许往后流转，您确定激活【${this.processInstanceData.name || this.processInstanceData.processDefinitionKey}】吗？`
        }
        let params = {
          processInstId: this.processInstanceData.processInstanceId,
          reason: this.reason
        }
        this.$modal.confirm(msg).then(() => {
           this.buttonLoading = true;
           return api.state(params);
         }).then(() => {
           this.buttonLoading = false;
           this.getList();
           this.$modal.msgSuccess("操作成功");
           this.dialogVisible = false
         }).finally(() => {
           this.buttonLoading = false;
         });

      }
    }
  }
</script>
