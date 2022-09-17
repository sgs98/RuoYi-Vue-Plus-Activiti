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
            <el-col :span="1.5">
              <el-button
                type="success"
                plain
                icon="el-icon-edit"
                size="mini"
                :disabled="multiple"
                @click="openAssignee"
              >修改办理人</el-button>
            </el-col>
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column align="center" type="index" label="序号" width="50"/>
            <el-table-column align="center" prop="name" label="任务名称"/>
            <el-table-column align="center" prop="processDefinitionName" label="流程定义名称" />
            <el-table-column align="center" prop="processDefinitionVersion" label="版本号" width="90" >
              <template slot-scope="{row}"> v{{row.processDefinitionVersion}}.0</template>
            </el-table-column>
            <el-table-column  align="center" prop="startUserNickName" label="流程发起人" min-width="80"/>
            <el-table-column  align="center" prop="assignee" label="当前流程办理人" min-width="80"/>
            <el-table-column  align="center" prop="isSuspended" label="流程状态" width="80">
              <template slot-scope="scope">
                <el-tag type="success" v-if="scope.row.processStatus=='激活'">激活</el-tag>
                <el-tag type="danger" v-else>挂起</el-tag>
              </template>
            </el-table-column>
            <el-table-column  align="center" prop="businessKey" :show-overflow-tooltip="true" label="流程关联业务ID" width="160"/>
            <el-table-column  align="center" prop="createTime" label="创建时间" width="160"/>
            <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-row :gutter="20" class="mb8">
                    <el-col :span="1.5">
                      <el-button
                          type="text"
                          @click="clickHistPop(scope.row)"
                          size="mini"
                          icon="el-icon-tickets"
                      >审批记录</el-button>
                    </el-col>
                    <el-col :span="1.5">
                      <el-button
                          v-if="scope.row.multiInstance && scope.row.parentTaskId===null"
                          type="text"
                          @click="addMultiPeople(scope.row)"
                          size="mini"
                          icon="el-icon-tickets"
                      >加签</el-button>
                    </el-col>
                  </el-row>
                  <el-row :gutter="20" class="mb8">
                    <el-col :span="1.5">
                      <el-button
                          type="text"
                          v-if="scope.row.parentTaskId===null"
                          @click="getInstVariable(scope.row)"
                          size="mini"
                          icon="el-icon-tickets"
                      >流程变量</el-button>
                    </el-col>
                    <el-col :span="1.5">
                      <el-button
                          v-if="scope.row.taskVoList && scope.row.taskVoList.length>0 && scope.row.parentTaskId===null"
                          type="text"
                          @click="deleteMultiClick(scope.row)"
                          size="mini"
                          icon="el-icon-tickets"
                      >减签</el-button>
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
        <!-- 驳回 -->
        <el-dialog title="审批记录" :visible.sync="visible" v-if="visible" width="60%" :close-on-click-modal="false">
          <history :processInstanceId="processInstanceId" :editMessage="true"></history>
        </el-dialog>

        <!-- 选择人员 -->
        <sys-dept-user ref="userRef" @confirmUser="clickUser" :multiple="false"/>

        <!-- 加签开始 -->
        <div v-if="addMultiVisible"><multi-user :taskId="taskId" ref="addMultiUserRef" @multiClose="multiClose" @confirmUser="confirmAddMultiUser"/></div>
        <!-- 加签结束 -->

        <!-- 减签开始 -->
        <el-dialog :close-on-click-modal="false" title="减签" :visible.sync="deleteMultiVisible" width="700px"  append-to-body>
          <el-table border @selection-change="handleSelectionMultiList" :data="multiList" style="width: 100%">
            <el-table-column type="selection" width="55"/>
            <el-table-column prop="name" label="任务名称" width="200"/>
            <el-table-column prop="assignee" label="办理人" width="200"/>
            <el-table-column prop="assigneeId" v-show="false" label="办理人ID" width="200"/>
          </el-table>
          <span slot="footer" class="dialog-footer">
            <el-button size="small" type="primary" @click="deleteMultiSubmit()">确定</el-button>
            <el-button size="small" @click="deleteMultiVisible = false">取消</el-button>
          </span>
        </el-dialog>
        <!-- 减签结束 -->

        <!-- 流程变量 -->
        <el-dialog title="流程变量" :visible.sync="variableVisible" v-if="variableVisible" width="60%" :close-on-click-modal="false">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>流程定义名称：{{processDefinitionName}}</span>
            </div>
            <div v-for="(v,index) in variableList" :key="index">
            <el-form :label-position="'right'" label-width="200px">
              <el-form-item :label="v.key+'：'">
                {{v.value}}
              </el-form-item>
            </el-form>
            </div>
          </el-card>
        </el-dialog>
    </div>
</template>

<script>
  import api from '@/api/workflow/task'
  import History from "@/components/Process/History";
  import Back from "@/components/Process/Back";
  import SysDeptUser from "@/views/components/user/sys-dept-user";
  import MultiUser from "@/views/components/user/multi-user";
  export default {
    components: {
      Back,
      History,
      SysDeptUser,
      MultiUser
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
        variableVisible: false,
        processInstanceId: undefined,
        businessKey: undefined, // 业务唯一标识
        visible: false,
        updateAssignee:{
            taskIdList:[],
            userId: undefined
        },
        //是否为会签
        isMultiInstance: false,
        //加签
        addMultiForm: {},
        //减签
        deleteMultiForm: {},
        //可以减签的集合
        multiList: [],
        addMultiVisible: false,
        deleteMultiVisible: false,
        //流程定义名称
        processDefinitionName: undefined,
        //流程变量
        variableList:[]
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
      //修改办理人
      openAssignee(){
         this.$refs.userRef.visible = true
      },
      //选择人员
      clickUser(userList){
        this.$modal.confirm('是否确认修改？').then(() => {
            let userId= userList.map(item => { return item.userId })
            this.loading = true;
            this.updateAssignee.taskIdList = this.ids
            this.updateAssignee.userId = userId.toString()
            this.$refs.userRef.visible = false
            api.updateAssignee(this.updateAssignee).then(()=>{
                this.$modal.msgSuccess("修改成功");
                this.getList();
            })
        })
      },
      //查询流程变量
      getInstVariable(row){
        this.variableVisible = true
        this.processDefinitionName = row.processDefinitionName
        api.getProcessInstVariable(row.id).then(response=>{
          this.variableList = response.data
        })
      },
      //关闭加签人员组件
      multiClose(){
        this.addMultiVisible = false
      },
      //打开加签人员组件
      addMultiPeople(row){
        this.addMultiForm = {}
        this.addMultiVisible = true
        this.taskId = row.id
        this.$nextTick(()=>{
          this.$refs.addMultiUserRef.visible = true
        })
      },
      //确认加签人员
      confirmAddMultiUser(data){
        let assignees = data.map((item) => {
          return item.userId;
        });
        let assigneeNames = data.map((item) => {
          return item.nickName;
        });
        this.addMultiForm = {
          taskId: this.taskId,
          assignees: assignees,
          assigneeNames: assigneeNames,
          nickNames: assigneeNames.join(",")
        }
        this.$forceUpdate()
        this.$modal.confirm('是否确认加签？').then(() => {
        api.addMultiInstanceExecution(this.addMultiForm).then(response => {
            if(response.code === 200){
                // 刷新数据
                  this.$message.success("办理成功");
                  // 将表单清空
                  this.addMultiForm = {}
                  // 关闭窗口
                  this.visible = false;
                  this.addMultiVisible = false;
                  // 刷新
                  this.getList()
              }
            })
            this.$refs.addMultiUserRef.visible = false
        })
      },
      //打开减签窗口
      deleteMultiClick(row){
        this.multiList = row.taskVoList
        this.deleteMultiVisible = true
      },
      //减签复选框
      handleSelectionMultiList(val){
        let executionIds = val.map((item) => {
          return item.executionId;
        });
        let taskIds = val.map((item) => {
          return item.id;
        });
        let assigneeIds = val.map((item) => {
          return item.assigneeId;
        });
        let assigneeNames = val.map((item) => {
          return item.assignee;
        });
        this.deleteMultiForm = {
          taskId: this.taskId,
          taskIds: taskIds,
          executionIds: executionIds,
          assigneeIds: assigneeIds,
          assigneeNames: assigneeNames
        }
      },
      //减签
      deleteMultiSubmit(){
        this.$modal.confirm('是否确认减签？').then(() => {
          api.deleteMultiInstanceExecution(this.deleteMultiForm).then(response => {
            if(response.code === 200){
              // 刷新数据
                this.$message.success("办理成功");
                // 关闭窗口
                this.visible = false;
                this.deleteMultiVisible = false;
                // 刷新
                this.getList()
            }
          })
        })
      }
    }
  }
</script>
<style scoped>
.box-card {
  word-break:break-all;
  overflow-y: scroll;
  overflow-x: hidden;
  box-sizing: border-box;
  padding: 8px 0px;
  height: 630px;
  width: inherit;
  line-height: 8px;
}
/* 修改滚动条样式 */
.box-card::-webkit-scrollbar {
	width: 4px;
}
.box-card::-webkit-scrollbar-thumb {
	border-radius: 10px;
}
</style>
