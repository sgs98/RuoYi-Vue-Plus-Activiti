<template>
<el-dialog title="业务规则" :visible.sync="visible" v-if="visible" width="60%" append-to-body v-dialogDrag :close-on-click-modal="false">

  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
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
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="businessRuleList" :highlight-current-row="true"
     @row-click = "onRowClick">
      <el-table-column label="id" align="center" prop="id" v-if="false"/>
      <el-table-column label="bean名称" :show-overflow-tooltip="true" align="center" prop="beanName" />
      <el-table-column label="方法名" :show-overflow-tooltip="true" align="center" prop="method" />
      <el-table-column label="备注" :show-overflow-tooltip="true" align="center" prop="remark" />
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
  <div slot="footer" class="dialog-footer">
        <el-button size="small" type="primary" @click="primary">确认</el-button>
        <el-button size="small" @click="cancel">取 消</el-button>
  </div>
</el-dialog>
</template>

<script>
import { listBusinessRule } from "@/api/workflow/businessRule";

export default {
  props: {

  },
  name: "processRule",
  data() {
    return {
      visible: false,
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 业务规则表格数据
      businessRuleList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        beanName: undefined,
        method: undefined,
      },
      businessRuleParam: [],
      // 选中行数据
      row: {}
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
      this.visible = false;
      this.resetForm("queryForm");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
      this.resetForm("queryForm");
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    onRowClick (row, event, column) {
      this.row = row
    },
    // 确认
    primary(){
      this.$emit("primary",this.row)
    }
  }
};
</script>
