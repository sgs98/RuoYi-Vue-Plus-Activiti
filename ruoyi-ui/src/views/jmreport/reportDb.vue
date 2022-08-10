<template>
<el-dialog title="报表" class="reportDb" :visible.sync="reportOpen" v-if="reportOpen" width="900px" append-to-body>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="mini" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="数据集编码" prop="dbCode">
        <el-input
          v-model="queryParams.dbCode"
          placeholder="请输入数据集编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数据集名字" prop="dbChName">
        <el-input
          v-model="queryParams.dbChName"
          placeholder="请输入数据集名字"
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

    <el-table v-loading="loading" @row-click="handdleClick" class="reportTable" :data="reportDbList" @selection-change="handleSelectionChange">
      <el-table-column label="主键字段" :show-overflow-tooltip="true" align="center" prop="jimuReportId" />
      <el-table-column label="数据集编码" :show-overflow-tooltip="true" align="center" prop="dbCode" />
      <el-table-column label="数据集名字" :show-overflow-tooltip="true" align="center" prop="dbChName" />
      <el-table-column label="数据源类型" :show-overflow-tooltip="true" align="center" prop="dbType" />
      <el-table-column label="动态查询SQL" :show-overflow-tooltip="true" align="center">
        <template slot-scope="scope">
         <span v-if="scope.row.dbDynSql.length < 30" >{{scope.row.dbDynSql}}</span>
         <span v-else >{{`${scope.row.dbDynSql.substring(0, 30)}...`}}</span>
        </template>
      </el-table-column>
      <el-table-column label="请求地址" :show-overflow-tooltip="true" align="center" prop="apiUrl" />
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
  <span slot="footer" class="dialog-footer">
    <el-button @click="reportOpen = false">取 消</el-button>
  </span>
</el-dialog>
</template>

<script>
import { listReportDb} from "@/api/report/reportDb";

export default {
  name: "ReportDb",
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
      // 报表数据表格数据
      reportDbList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dbCode: undefined,
        dbChName: undefined
      },
      reportOpen: false,
    };
  },
  created() {
    this.getList();
  },
  methods: {
    //选择
    handdleClick(row){
       this.$emit("report-db-submit",row)
       this.reportOpen = false
    },
    /** 查询报表数据列表 */
    getList() {
      this.loading = true;
      listReportDb(this.queryParams).then(response => {
        this.reportDbList = response.rows;
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    }
  }
};
</script>
<style scoped>
 .reportTable{
  height: 380px;
  overflow-y: auto;
 }
 .reportTable::-webkit-scrollbar-thumb {
	border-radius: 10px;
  }
  .reportTable::-webkit-scrollbar {
    width: 5px;
  }
</style>
