<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申请人" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="请输入申请人"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="请假时长" prop="duration">
        <el-input
          v-model="queryParams.duration"
          placeholder="请输入请假时长"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="请假类型" prop="leaveType">
        <el-select v-model="queryParams.leaveType" placeholder="请选择请假类型" clearable size="small">
          <el-option
            v-for="dict in dict.type.bs_leave_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['demo:leave:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['demo:leave:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['demo:leave:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          :loading="exportLoading"
          @click="handleExport"
          v-hasPermi="['demo:leave:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="leaveList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" v-if="false"/>
      <el-table-column label="申请人" align="center" prop="username" />
      <el-table-column label="请假时长" align="center" prop="duration" />
      <el-table-column label="工作委托人" align="center" prop="principal" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" />
      <el-table-column label="请假类型" align="center" prop="leaveType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bs_leave_type" :value="scope.row.leaveType"/>
        </template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column label="请假原因" align="center" prop="leaveReason" />
      <el-table-column label="请假开始时间" align="center" prop="startDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="请假结束时间" align="center" prop="endDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="流程状态" align="center" prop="actBusinessStatus.status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.act_status" :value="scope.row.actBusinessStatus.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.actBusinessStatus.status==='draft'||scope.row.actBusinessStatus.status==='back'||scope.row.actBusinessStatus.status==='cancel'"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['demo:leave:edit']"
          >修改</el-button>
          <el-button
            v-if="scope.row.actBusinessStatus.status==='draft'||scope.row.actBusinessStatus.status==='back'||scope.row.actBusinessStatus.status==='cancel'"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['demo:leave:remove']"
          >删除</el-button>
          <el-button
            v-if="scope.row.actBusinessStatus.status==='waiting'"
            size="mini"
            type="text"
            icon="el-icon-back"
            @click="cancelProcessApply(scope.row.processInstanceId)"
          >撤销</el-button>
          <el-button
            v-if="scope.row.actBusinessStatus.status!=='draft'"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看</el-button>
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

    <!-- 添加或修改请假业务对话框 -->
    <el-dialog :title="title" :visible.sync="open" v-if="open" width="800px" append-to-body>
      <el-tabs  type="border-card" >
        <el-tab-pane label="业务单据" v-loading="loading">
            <el-form ref="form" :model="form" :rules="rules" label-width="120px">
              <el-form-item label="申请人用户名" prop="username">
                <el-input v-model="form.username" placeholder="请输入申请人用户名" />
              </el-form-item>
              <el-form-item label="请假时长" prop="duration">
                <el-input v-model="form.duration" placeholder="请输入请假时长，单位：天" />
              </el-form-item>
              <el-form-item label="工作委托人" prop="principal">
                <el-input v-model="form.principal" placeholder="请输入工作委托人" />
              </el-form-item>
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
              </el-form-item>
              <el-form-item label="请假类型" prop="leaveType">
                <el-select v-model="form.leaveType" placeholder="请选择请假类型">
                  <el-option
                    v-for="dict in dict.type.bs_leave_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="parseInt(dict.value)"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="标题" prop="title">
                <el-input v-model="form.title" placeholder="请输入标题" />
              </el-form-item>
              <el-form-item label="请假原因" prop="leaveReason">
                <el-input v-model="form.leaveReason" type="textarea" placeholder="请输入内容" />
              </el-form-item>
              <el-form-item label="请假开始时间" prop="startDate">
                <el-date-picker clearable size="small"
                  v-model="form.startDate"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="选择请假开始时间">
                </el-date-picker>
              </el-form-item>
              <el-form-item label="请假结束时间" prop="endDate">
                <el-date-picker clearable size="small"
                  v-model="form.endDate"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="选择请假结束时间">
                </el-date-picker>
              </el-form-item>
            </el-form>
            <div class="dialog-footer" style="float:right" v-if="flag">
              <el-button :loading="buttonLoading" type="info" @click="submitForm('zancun')">暂存</el-button>
              <el-button :loading="buttonLoading" type="primary" @click="submitForm('submit')">提交</el-button>
              <el-button @click="cancel">取 消</el-button>
            </div>
        </el-tab-pane>
        <!-- 审批历史 -->
        <el-tab-pane label="审批历史" v-if="processInstanceId">
              <history ref="historyRef" :processInstanceId="processInstanceId" ></history>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
    <!-- 提交 -->
    <verify ref="verifyRef" @callSubmit="callSubmit" :taskId="taskId" :taskVariables="taskVariables"></verify>
  </div>
</template>

<script>
import { listLeave, getLeave, delLeave, addLeave, updateLeave } from "@/api/demo/leave";
import processAip from "@/api/workflow/processInst";
import history from "@/components/Process/History";
import verify from "@/components/Process/Verify";
export default {
  name: "Leave",
  dicts: ['bs_leave_type','act_status'],
  components: {
    history,
    verify
  },
  data() {
    return {
      // 流程实例
      processInstanceId: '',
      // 按钮loading
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
      // 请假业务表格数据
      leaveList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        username: undefined,
        duration: undefined,
        leaveType: undefined,
        title: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: "主键ID不能为空", trigger: "blur" }
        ],
        username: [
          { required: true, message: "申请人用户名不能为空", trigger: "blur" }
        ],
        duration: [
          { required: true, message: "请假时长，单位：天不能为空", trigger: "blur" }
        ],
        principal: [
          { required: true, message: "工作委托人不能为空", trigger: "blur" }
        ],
        contactPhone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" }
        ],
        leaveType: [
          { required: true, message: "请假类型不能为空", trigger: "change" }
        ],
        title: [
          { required: true, message: "标题不能为空", trigger: "blur" }
        ],
        leaveReason: [
          { required: true, message: "请假原因不能为空", trigger: "blur" }
        ],
        startDate: [
          { required: true, message: "请假开始时间不能为空", trigger: "blur" }
        ],
        endDate: [
          { required: true, message: "请假结束时间不能为空", trigger: "blur" }
        ]
      },
      taskVariables: {}, //流程变量
      taskId: undefined, //任务id
      flag: true
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询请假业务列表 */
    getList() {
      this.loading = true;
      listLeave(this.queryParams).then(response => {
        this.leaveList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        username: undefined,
        duration: undefined,
        principal: undefined,
        contactPhone: undefined,
        leaveType: undefined,
        title: undefined,
        leaveReason: undefined,
        startDate: undefined,
        endDate: undefined
      };
      this.processInstanceId = undefined
      this.resetForm("form");
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
    /** 新增按钮操作 */
    handleAdd() {
      this.flag = true
      this.reset();
      this.open = true;
      this.title = "添加请假业务";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.flag = true
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getLeave(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.$nextTick(() => {
          this.processInstanceId=response.data.processInstanceId
        })
        this.open = true;
        this.title = "修改请假业务";
      });
    },
    //查看
    handleView(row){
      this.flag = false
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getLeave(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.$nextTick(() => {
          this.processInstanceId=response.data.processInstanceId
        })
        this.open = true;
        this.title = "查看请假业务";
      });
    },
    /** 提交按钮 */
    submitForm(flag) {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateLeave(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              if(flag === 'submit'){
                this.submitFormAppply(response.data)
              }
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addLeave(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          }
        }
      });
    },
    // 提交成功回调
    callSubmit(){
      this.open = false;
      this.getList();
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除请假业务编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delLeave(ids);
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
        this.$download.excel('/demo/leave/export', this.queryParams);
    },
    //提交流程
    submitFormAppply(entity){
        let variables = {
            entity: entity
        }
        const data = {
            processKey: 'test', // key
            businessKey: entity.id, // 业务id
            variables: variables,
            classFullName: 'com.ruoyi.demo.domain.BsLeave'
        }
        // 启动流程
        let assigneeList = []
        assigneeList.push(1)
        assigneeList.push(2)
        processAip.startProcessApply(data).then(response => {
            this.taskId = response.data.taskId;
            // 查询下一节点的变量
            this.taskVariables = {
                entity: entity,  // 变量
                assignee: '1', // key
                //assigneeList: assigneeList
            }
            this.$refs.verifyRef.visible = true
            this.$refs.verifyRef.reset()
        })
    },
    //撤回
    cancelProcessApply(processInstanceId){
         this.$modal.confirm('是否撤销申请').then(() => {
            this.loading = true;
            return processAip.cancelProcessApply(processInstanceId);
         }).then(() => {
            this.getList();
            this.$modal.msgSuccess("撤回成功");
         }).finally(() => {
            this.loading = false;
         });
    },
  }
};
</script>
