<template>
  <div class="app-container">
    <div v-if="dataViewVisible">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="表单key" prop="formKey">
            <el-input
            v-model="queryParams.formKey"
            placeholder="请输入表单key"
            clearable
            @keyup.enter.native="handleQuery"
            />
        </el-form-item>
        <el-form-item label="表单名称" prop="formName">
            <el-input
            v-model="queryParams.formName"
            placeholder="请输入表单名称"
            clearable
            @keyup.enter.native="handleQuery"
            />
        </el-form-item>
        <el-form-item label="单号" prop="applyCode">
            <el-input
            v-model="queryParams.applyCode"
            placeholder="请输入单号"
            clearable
            @keyup.enter.native="handleQuery"
            />
        </el-form-item>
        <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
            <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['workflow:businessForm:export']"
            >导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="businessFormList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="单号" align="center" prop="applyCode" />
        <el-table-column label="表单key" align="center" prop="formKey" />
        <el-table-column label="表单名称" align="center" prop="formName" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
            <el-button
                v-if="scope.row.actBusinessStatus.status==='draft'||scope.row.actBusinessStatus.status==='back'||scope.row.actBusinessStatus.status==='cancel'"
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['workflow:businessForm:edit']"
            >修改</el-button>
            <el-button
                size="mini"
                type="text"
                icon="el-icon-view"
                @click="handleView(scope.row)"
            >查看</el-button>
            <el-button
                v-if="scope.row.actBusinessStatus.status==='draft'||scope.row.actBusinessStatus.status==='back'||scope.row.actBusinessStatus.status==='cancel'"
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['workflow:businessForm:remove']"
            >删除</el-button>
            <el-button
                v-if="scope.row.actBusinessStatus.status==='waiting'"
                size="mini"
                type="text"
                icon="el-icon-back"
                @click="cancelProcessApply(scope.row)"
            >撤销</el-button>
            </template>
        </el-table-column>
        </el-table>

        <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
        />
    </div>
    <div class="form-container" v-if="dynamicFormEditVisible">
      <div class="form-container-header"><i class="el-dialog__close el-icon el-icon-close" @click="closeDynamicEdit"></i></div>
      <!-- 动态表单编辑开始 -->
      <el-tabs type="border-card" class="container-tab">
        <el-tab-pane label="业务单据">
          <dynamicFormEdit
              :buildData="form.formText"
              v-model="form.formValue"
              @draftForm="draftProcessForm(arguments)"
              @submitForm="submitProcessForm(arguments)"
              ref="dynamicFormEditRef"
          />
        </el-tab-pane>
        <el-tab-pane label="审批意见" v-if="processInstanceId">
            <el-table :data="historyList" border stripe style="width: 100%" max-height="570">
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
        <el-tab-pane label="流程进度" v-if="processInstanceId">
           <el-image :src="url" style="font-size: 20px; margin: 50px;">
              <div slot="placeholder"><i class="el-icon-loading"></i> 流程审批历史图加载中……</div>
           </el-image>
        </el-tab-pane>
      </el-tabs>
      <!-- 动态表单编辑结束 -->
    </div>
    <div class="form-container" v-if="dynamicFormViewVisible" >
      <div class="form-container-header"><i class="el-dialog__close el-icon el-icon-close" @click="closeDynamicView"></i></div>
      <!-- 动态表单查看开始 -->
      <el-tabs type="border-card" class="container-tab">
        <el-tab-pane label="业务单据">
          <dynamicFormView :buildData="form.formText" v-model="form.formValue"/>
        </el-tab-pane>
        <el-tab-pane label="审批意见" v-if="processInstanceId">
            <el-table :data="historyList" border stripe style="width: 100%" max-height="570">
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
        <el-tab-pane label="流程进度" v-if="processInstanceId">
           <el-image :src="url" style="font-size: 20px; margin: 50px;">
              <div slot="placeholder"><i class="el-icon-loading"></i> 流程审批历史图加载中……</div>
           </el-image>
        </el-tab-pane>
      </el-tabs>
      <!-- 动态表单查看结束-->
    </div>
    <!-- 工作流 -->
    <verify ref="verifyRef" @submitCallback="submitCallback" :taskId="taskId" :taskVariables="taskVariables" :sendMessage="sendMessage"/>
  </div>
</template>

<script>
import { listBusinessForm, getBusinessForm, delBusinessForm, updateBusinessForm } from "@/api/workflow/businessForm";
import dynamicFormEdit from './dynamicFormEdit'
import dynamicFormView from './dynamicFormView'
import verify from "@/components/Process/Verify";
import processApi from "@/api/workflow/processInst";
export default {
  name: "BusinessForm",
  components:{
    dynamicFormEdit,
    dynamicFormView,
    verify
  },
  data() {
    return {
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
      // 业务表单表格数据
      businessFormList: [],
      // 弹出层标题
      title: "",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        formKey: undefined,
        formName: undefined,
        applyCode: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      //页面列表展示
      dataViewVisible: true,
      //动态表单编辑
      dynamicFormEditVisible: false,
      //动态表单查看
      dynamicFormViewVisible: false,
      // 任务id
      taskId: undefined,
      // 流程变量
      taskVariables: {},
      // 站内信
      sendMessage: {},
      // 审批记录
      historyList: [],
      // 流程实例id
      processInstanceId: undefined
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询业务表单列表 */
    getList() {
      this.loading = true;
      listBusinessForm(this.queryParams).then(response => {
        this.businessFormList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 查询审批历史记录
    async getHistoryInfoList() {
        const { data } = await processApi.getHistoryInfoList(this.processInstanceId)
        this.historyList = data
    },
    // 通过流程实例id获取历史流程图
    getHistoryImage(){
      this.url = process.env.VUE_APP_BASE_API+'/workflow/processInstance/getHistoryProcessImage?processInstanceId='+this.processInstanceId
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      const id = row.id || this.ids
      getBusinessForm(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.processInstanceId = response.data.actBusinessStatus.processInstanceId
        if(this.processInstanceId){
          this.getHistoryInfoList()
          this.getHistoryImage()
        }
        this.dynamicFormEditVisible = true;
        this.dataViewVisible = false
        this.title = response.data.formName;
      });
    },
    /** 查看按钮操作 */
    handleView(row){
      this.loading = true;
      const id = row.id || this.ids
      getBusinessForm(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.processInstanceId = response.data.actBusinessStatus.processInstanceId
        if(this.processInstanceId){
          this.getHistoryInfoList()
          this.getHistoryImage()
        }
        this.dynamicFormViewVisible = true;
        this.dataViewVisible = false
        this.title = response.data.formName;
      });
    },
    //关闭编辑
    closeDynamicEdit(){
        this.dynamicFormEditVisible = false;
        this.dataViewVisible = true
        this.getList()
    },
    //关闭查看
    closeDynamicView(){
        this.dynamicFormViewVisible = false;
        this.dataViewVisible = true
        this.getList()
    },
    //暂存
    draftProcessForm(args){
      this.form.formText = args[0]
      this.form.formValue = args[1]
      this.form.status = 'draft'
      if (this.form.id != null) {
        updateBusinessForm(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
          this.dynamicFormEditVisible = false
          this.dataViewVisible = true
          this.getList();
        })
      }
    },
    /** 提交按钮 */
    submitProcessForm(args) {
      this.sendMessage = {
        title: this.form.formName,
        messageContent:'单据【'+this.form.applyCode+"】申请"
      }
      this.form.formText = args[0]
      this.form.formValue = args[1]
      this.form.status = 'waiting'
      if (this.form.id != null) {
        updateBusinessForm(this.form).then(response => {
          this.submitFormApply(response.data)
        })
      }
    },
     //提交流程
     submitFormApply(entity){
        //流程变量
        this.taskVariables = {
            entity: entity.variableMap
        }
        const data = {
            processKey: entity.actProcessDefSetting.processDefinitionKey, // key
            businessKey: entity.id, // 业务id
            variables: this.taskVariables,
            classFullName: entity.formKey
        }
        // 启动流程
        processApi.startProcessApply(data).then(response => {
            this.taskId = response.data.taskId;
            this.$refs.verifyRef.visible = true
            this.$refs.verifyRef.reset()
        })
    },
    // 提交成功回调
    submitCallback(){
      this.dynamicFormEditVisible = false;
      this.dataViewVisible = true
      this.getList();
    },
    //撤回
    cancelProcessApply(row){
         this.$modal.confirm('是否撤销申请').then(() => {
            this.loading = true;
            return processApi.cancelProcessApply(row.actBusinessStatus.processInstanceId);
         }).then(() => {
            this.getList();
            this.$modal.msgSuccess("撤回成功");
         }).finally(() => {
            this.loading = false;
         });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除业务表单编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delBusinessForm(ids);
      }).then(() => {
        this.loading = false;
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).finally(() => {
        this.loading = false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('workflow/businessForm/export', {
        ...this.queryParams
      }, `businessForm_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
<style scoped>
    .form-container-header{
        height: 30px;
        padding-bottom: 10px;
    }
    .el-icon-close{
        float: right;
        cursor: pointer;
    }
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
