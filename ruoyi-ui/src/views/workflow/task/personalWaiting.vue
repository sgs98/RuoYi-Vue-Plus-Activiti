<template>
    <div class="app-container">
        <div v-if="dataViewVisible">
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
                <el-table-column fixed align="center" type="index" label="序号" width="50"/>
                <el-table-column fixed align="center" prop="name" label="任务名称"/>
                <el-table-column  align="center" prop="processDefinitionName" label="流程定义名称" />
                <el-table-column align="center" prop="processDefinitionVersion" label="版本号" width="90" >
                <template slot-scope="{row}"> v{{row.processDefinitionVersion}}.0</template>
                </el-table-column>
                <el-table-column  align="center" prop="startUserNickName" label="流程发起人"  min-width="130"/>
                <el-table-column  align="center" prop="assignee" label="当前流程办理人"  min-width="130"/>
                <el-table-column  align="center" prop="isSuspended" label="流程状态" width="160">
                <template slot-scope="scope">
                    <el-tag type="success" v-if="scope.row.processStatus=='激活'">激活</el-tag>
                    <el-tag type="danger" v-else>挂起</el-tag>
                </template>
                </el-table-column>
                <el-table-column  align="center" prop="businessKey" :show-overflow-tooltip="true" label="流程关联业务ID" width="160"/>
                <el-table-column  align="center" prop="createTime" label="创建时间" width="160"/>
                <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
                  <template slot-scope="scope">
                    <el-button size="mini" icon="el-icon-sort" v-if="scope.row.isClaim===false" type="text"
                    @click="clickClaim(scope.row)">签收 &nbsp;</el-button>
                    <el-button size="mini" icon="el-icon-sort" v-if="scope.row.isClaim===true" type="text"
                    @click="returnTask(scope.row)">归还 &nbsp;</el-button>
                    <el-button
                        v-if="scope.row.isClaim===null||scope.row.isClaim===true"
                        type="text"
                        @click="clickTaskPop(scope.row)"
                        size="mini"
                        icon="el-icon-s-check"
                    >办理</el-button>
                  </template>
                </el-table-column>
            </el-table>

            <pagination v-show="total>0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList" />
        </div>
        <!-- 单据信息开始 -->
        <div class="form-container" v-if="dynamicFormEditVisible">
            <div class="form-container-header form-container-header"><i class="el-dialog__close el-icon el-icon-close" @click="closeDynamicEdit"></i></div>
            <approvalForm ref="approvalForm" :businessKey = 'businessKey' :taskId = 'taskId' :parentTaskId = 'parentTaskId'
            @closeForm = 'closeDynamicEdit' :currProcessForm = 'currProcessForm' :processInstanceId = 'processInstanceId' :dynamicFormData = 'dynamicFormData'/>
        </div>
        <!-- 单据信息结束 -->
    </div>
</template>

<script>
  import api from '@/api/workflow/task'
  import history from "@/components/Process/History";
  import approvalForm from "@/views/components/approvalForm";
  import { getBusinessForm } from "@/api/workflow/businessForm";
  export default {
    components: {
      history,
      approvalForm
    },
    data () {
      return {
        //按钮loading
        buttonLoading: false,
        // 遮罩层
        loading: true,
        dataViewVisible: true,
        dynamicFormEditVisible: false,
        // 导出遮罩层
        exportLoading: false,
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
            name: undefined
        },
        // 任务id
        taskId:undefined,
        taskVariables: undefined,
        processInstanceId: undefined,
        parentTaskId: undefined,
        businessKey: undefined, // 业务唯一标识
        currProcessForm: '', //表单组件名称
        dynamicFormData: '' //表单组件名称
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
        api.getTaskWaitByPage(this.queryParams).then(response => {
          this.dataList = response.rows;
          this.total = response.total;
          this.loading = false;
        })
      },
      //刷新
      refresh(){
        this.getList()
      },
      //办理任务弹出层
      clickTaskPop(row){
          this.businessKey = row.businessKey
          this.processInstanceId = row.processInstanceId
          this.taskId = row.id
          this.parentTaskId = row.parentTaskId
          if(row.actProcessDefSetting && row.actProcessDefSetting.businessType === 0){
            getBusinessForm(this.businessKey).then(response => {
                this.dynamicFormData = response.data;
                this.dynamicFormEditVisible = true;
                this.dataViewVisible = false
                this.currProcessForm = 'dynamicFormEdit'
            });
          }else{
            this.currProcessForm = row.actProcessDefSetting.componentName
            this.dynamicFormEditVisible = true    
            this.dataViewVisible = false  
          }
      },
      //关闭办理弹出层
      closeDynamicEdit(){
         this.dynamicFormEditVisible = false;
         this.dataViewVisible = true
         this.getList()
       },
      //签收
      clickClaim(row){
         this.$modal.confirm('是否确认签收此任务？').then(() => {
           this.loading = true;
           return api.claim(row.id)
         }).then(() => {
           this.loading = false;
           this.getList();
           this.$modal.msgSuccess("签收成功");
         }).finally(() => {
           this.loading = false;
         });
      },
      //归还任务
      returnTask(row){
         this.$modal.confirm('是否确认归还此任务？').then(() => {
           this.loading = true;
           return api.returnTask(row.id)
         }).then(() => {
           this.loading = false;
           this.getList();
           this.$modal.msgSuccess("归还成功");
         }).finally(() => {
           this.loading = false;
         });
      }
    }
  }
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
</style>