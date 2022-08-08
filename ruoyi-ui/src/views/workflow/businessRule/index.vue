<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="bean名称" prop="beanName">
        <el-input
          v-model="queryParams.businessRule"
          placeholder="请输入bean名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="方法名" prop="method">
        <el-input
          v-model="queryParams.method"
          placeholder="请输入方法名"
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
          v-hasPermi="['workflow:businessRule:add']"
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
          v-hasPermi="['workflow:businessRule:edit']"
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
          v-hasPermi="['workflow:businessRule:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['workflow:businessRule:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="businessRuleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" v-if="false"/>
      <el-table-column label="bean名称" align="center" prop="beanName" />
      <el-table-column label="方法名" align="center" prop="method" width="180"/>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createBy" width="120"/>
      <el-table-column label="更新人" align="center" prop="updateBy" width="120"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['workflow:businessRule:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['workflow:businessRule:remove']"
          >删除</el-button>
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

    <!-- 添加或修改业务规则对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="bean名称" prop="beanName">
          <el-input v-model="form.beanName" placeholder="请输入bean名称" />
        </el-form-item>
        <el-form-item label="方法名" prop="method">
          <el-input v-model="form.method" placeholder="请输入方法名" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <el-button type="primary" size="mini" @click="addParam">添加</el-button>
      <el-table :data="businessRuleParams">
        <el-table-column label="参数类型" align="center" prop="paramType" >
             <template slot-scope="scope">
                <el-input v-model="scope.row.paramType"/>
            </template>
        </el-table-column>
        <el-table-column label="参数" align="center" prop="param" >
             <template slot-scope="scope">
                <el-input v-model="scope.row.param"/>
            </template>
        </el-table-column>
        <el-table-column label="排序" align="center" prop="orderNo" >
             <template slot-scope="scope">
                <el-input type="number" :min="0" @blur="checkOrder(scope.row)" v-model="scope.row.orderNo"/>
            </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" >
             <template slot-scope="scope">
                <el-input v-model="scope.row.remark"/>
            </template>
        </el-table-column>
        <el-table-column label="操作">
            <template slot-scope="scope">
                <el-button @click="deleteParam(scope.$index)" type="danger" size="small">删除</el-button>
            </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBusinessRule, getBusinessRule, delBusinessRule, addbusinessRule, updateBusinessRule } from "@/api/workflow/businessRule";

export default {
  name: "businessRule",
  data() {
    return {
      // 按钮loading
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
      // 业务规则表格数据
      businessRuleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        businessRule: undefined,
        method: undefined,
      },
      // 表单参数
      form: {},
      businessRuleParams: [],
      // 表单校验
      rules: {
        id: [
          { required: true, message: "id不能为空", trigger: "blur" }
        ],
        beanName: [
          { required: true, message: "bean名称不能为空", trigger: "blur" }
        ],
        method: [
          { required: true, message: "方法名不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        updateTime: [
          { required: true, message: "更新时间不能为空", trigger: "blur" }
        ],
        createBy: [
          { required: true, message: "创建人不能为空", trigger: "blur" }
        ],
        updateBy: [
          { required: true, message: "更新人不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询业务规则列表 */
    getList() {
      this.loading = true;
      listBusinessRule(this.queryParams).then(response => {
        this.businessRuleList = response.rows;
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
        beanName: undefined,
        method: undefined,
        remark: undefined,
        createTime: undefined,
        updateTime: undefined,
        createBy: undefined,
        updateBy: undefined
      };
      this.businessRuleParams = []
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
      this.reset();
      this.open = true;
      this.title = "添加业务规则";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getBusinessRule(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.businessRuleParams = response.data.businessRuleParams;
        this.open = true;
        this.title = "修改业务规则";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.businessRuleParams = this.businessRuleParams
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateBusinessRule(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addbusinessRule(this.form).then(response => {
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
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除业务规则编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delBusinessRule(ids);
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
      this.download('workflow/businessRule/export', {
        ...this.queryParams
      }, `businessRule_${new Date().getTime()}.xlsx`)
    },
    // 添加参数
    addParam(){
        if(this.businessRuleParams === undefined || this.businessRuleParams === null) {
          this.businessRuleParams = []
        }
        let param = {
            paramType:'',
            type:'',
            remark:''
        }
        this.businessRuleParams.push(param);
    },
    // 删除参数
    deleteParam(index){
       this.businessRuleParams.splice(index,1)
    },
    // 不可小于0
    checkOrder(row){
       if(row.orderNo<0){
          row.orderNo = 0
       }
    }
  }
};
</script>
