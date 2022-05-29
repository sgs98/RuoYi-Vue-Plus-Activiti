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
            <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                  <el-button
                      type="text"
                      @click="clickHistPop(scope.row)"
                      size="mini"
                      icon="el-icon-tickets"
                  >审批记录</el-button>
                </template>
            </el-table-column>
        </el-table>

        <pagination v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList" />
        <!-- 驳回 -->
        <el-dialog title="审批记录" :visible.sync="visible" v-if="visible" width="60%" :close-on-click-modal="false">
          <history :processInstanceId="processInstanceId"></history>
        </el-dialog>

        <approvalForm ref="approvalForm" :businessKey = 'businessKey' :processInstanceId = 'processInstanceId'/>
    </div>
</template>

<script>
  import api from '@/api/workflow/task'
  import verify from "@/components/Process/Verify";
  import history from "@/components/Process/History";
  import Back from "@/components/Process/Back";
  import { getLeave} from "@/api/demo/leave";
  import  approvalForm from "@/views/components/approvalForm";

  export default {
    components: {
      verify,
      Back,
      history,
      approvalForm
    },
    data () {
      return {
        //按钮loading
        buttonLoading: false,
        // 遮罩层
        loading: true,
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
        // 点击的行数据
        task: {},
        taskVariables: undefined,
        propUserList: [],
        processInstanceId: undefined,
        businessKey: undefined, // 业务唯一标识
        visible: false
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
          api.getAllTaskWaitByPage(this.queryParams).then(response => {
            this.dataList = response.rows;
            this.total = response.total;
            this.loading = false;
          })
      },
      //审批记录
      clickHistPop(row){
         this.processInstanceId = row.processInstanceId
         this.visible = true
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
      }
    }
  }
</script>
