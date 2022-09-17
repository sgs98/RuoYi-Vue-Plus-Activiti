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
        <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
         <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
        <div class="container-box" v-loading="loading" v-if="dynamicFormList && dynamicFormList.length>0">
            <div v-for="(item,index) in dynamicFormList" :key="index" class="container-card">
                <el-card shadow="hover" class="card-item">
                    <div slot="header" class="clearfix">
                    <el-tooltip class="item" effect="dark" :content="'表单KEY:'+item.formKey" placement="top-start">
                        <span>{{item.formName}}</span>
                    </el-tooltip>
                    <span style="float: right;" @click="handleApply(item)"><el-link type="primary">提交申请</el-link></span>
                    </div>
                    <div>
                    {{item.formRemark}}
                    </div>
                </el-card>
            </div>
        </div>
        <el-empty class="el-empty-icon" v-else description="暂无数据"></el-empty>
        <div class="pagination-box" v-show="total>0">
            <pagination
            v-show="total>0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
            />
        </div>
    </div>
    <!-- 动态表单编辑 -->
    <div v-if="dynamicFormEditVisible">
       <div class="container-header"><i class="el-dialog__close el-icon el-icon-close" @click="closeDynamicEdit"></i></div>
       <el-tabs type="border-card" class="container-tab">
        <el-tab-pane label="业务单据">
          <dynamicFormEdit
            :buildData="formData.formDesignerText"
            @draftForm="draftProcessForm(arguments)"
            @submitForm="submitProcessForm(arguments)"
            ref="dynamicFormEditRef"
          />
        </el-tab-pane>
      </el-tabs>
       <!-- 工作流 -->
       <verify ref="verifyRef" @submitCallback="submitCallback" :taskId="taskId" :taskVariables="taskVariables" :sendMessage="sendMessage"/>
    </div>
  </div>
</template>

<script>
import { listDynamicFormEnable } from "@/api/workflow/dynamicForm";
import dynamicFormEdit from '@/views/workflow/businessForm/dynamicFormEdit'
import { addBusinessForm} from "@/api/workflow/businessForm";
import verify from "@/components/Process/Verify";
import processApi from "@/api/workflow/processInst";
export default {
  name: "ProcessForm",
  components:{
    dynamicFormEdit,
    verify
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 流程单表格数据
      dynamicFormList: [],
      // 弹出层标题
      title: "",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 15,
        formKey: undefined,
        formName: undefined,
      },
      //页面列表展示
      dataViewVisible: true,
      // 表单显示隐藏
      dynamicFormEditVisible: false,
      // 表单数据
      formData: {},
      // 任务id
      taskId: '',
      // 流程变量
      taskVariables: {},
      // 站内信
      sendMessage: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {

    /** 查询流程单列表 */
    getList() {
      this.loading = true;
      listDynamicFormEnable(this.queryParams).then(response => {
        this.dynamicFormList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
    //打开表单
    handleApply(row){
       this.formData = row
       this.dynamicFormEditVisible = true
       this.dataViewVisible = false
    },
    //关闭编辑
    closeDynamicEdit(){
        this.dynamicFormEditVisible = false;
        this.dataViewVisible = true
        this.getList()
    },
    //暂存
    draftProcessForm(args){
      let data = {
        formId: this.formData.id,
        formKey: this.formData.formKey,
        formName: this.formData.formName,
        formText: args[0],
        formValue: args[1],
        status: 'draft'
      }
      addBusinessForm(data).then(response => {
        this.$modal.msgSuccess("保存成功");
        this.$router.push('/workflow/from/businessForm')
        this.dynamicFormEditVisible = false
        this.getList();
      })
    },
    //提交
    submitProcessForm(args){
      let data = {
        formId: this.formData.id,
        formKey: this.formData.formKey,
        formName: this.formData.formName,
        formText: args[0],
        formValue: args[1],
        status: 'waiting'
      }
      addBusinessForm(data).then(response => {
        this.sendMessage = {
          title: response.data.formName,
          messageContent:'单据【'+response.data.applyCode+"】申请"
        }
        this.submitFormApply(response.data)
      })
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
      this.$router.push('/workflow/from/businessForm')
    },
  }
};
</script>
<style scoped>
  .card-item{
    cursor: pointer;
    height: 160px !important;
  }
  .clearfix{
    font-size: 14px;
    font-family: '幼圆' !important;
  }
  .container-box{
    height: calc(100vh - 263px);
    overflow-y: auto;
    display: flex;
    flex-wrap: wrap;
  }
  .container-card{
    width: 280px;
    padding: 0px 10px 0px 10px;
  }
  .container-header{
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
