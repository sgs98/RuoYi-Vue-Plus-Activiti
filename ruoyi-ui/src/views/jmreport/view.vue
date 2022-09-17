<template>
<el-container class="container">
  <el-aside class="aside">
    <el-form :model="queryParams" ref="queryForm" size="mini" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="" prop="reportName">
            <el-input
            v-model="queryParams.reportName"
            placeholder="请输入报表名称"
            clearable
            @keyup.enter.native="handleQuery"
            >
            <el-button slot="append" icon="el-icon-search" @click="handleQuery">搜索</el-button>
            </el-input>
        </el-form-item>
    </el-form>

    <el-row :gutter="8" class="mb8">
        <el-col :span="1.5">
            <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['report:reportView:add']"
            >新增</el-button>
        </el-col>
        <el-col :span="1.5">
            <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['report:reportView:export']"
            >导出</el-button>
        </el-col>
    </el-row>
    <div v-loading="loading" >
      <div class="item_font">报表名称</div>
      <div v-for="(item,index) in reportViewList" :key="index" class="item">
          <span @click="handdle(item)" v-if="item.reportName.length < 13" >{{item.reportName}}</span>
          <el-tooltip v-else effect="dark" :content="item.reportName" placement="bottom-end">
              <span @click="handdle(item)">{{`${item.reportName.substring(0, 13)}...`}}</span>
          </el-tooltip>
          <span style="float:right;">
              <el-button
              type="text"
              icon="el-icon-edit"
              size="mini"
              @click="handleUpdate(item)"
              v-hasPermi="['report:reportView:edit']"
              >修改</el-button>
              <el-button
              type="text"
              icon="el-icon-delete"
              size="mini"
              @click="handleDelete(item)"
              v-hasPermi="['report:reportView:remove']"
              >删除</el-button>
          </span>
      </div>
    </div>
    <!-- 添加或修改报表查看对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="报表" prop="reportId" v-show="false">
          <el-input v-model="form.reportId" readonly placeholder="请输入报表"/>
        </el-form-item>
        <el-form-item label="报表名称" prop="reportName">
          <el-input v-model="form.reportName" readonly placeholder="请输入报表名称" >
            <el-button slot="append" @click="handleReportOpen">选择</el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="排序" prop="orderNo">
          <el-input type="number" v-model="form.orderNo" placeholder="请输入排序" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <ReportDb @report-db-submit="reportDbSubmit" ref="reportDb"/>
    
  </el-aside>
  
  <el-main class="main">
    <i-frame v-if="url" :src="url" />
    <el-empty class="el-empty-icon" v-else description="暂无数据"></el-empty>
  </el-main>
</el-container>
</template>

<script>
import { listReportView, getReportView, delReportView, addReportView, updateReportView } from "@/api/report/reportView";
import ReportDb from "@/views/jmreport/reportDb";
import iFrame from "@/components/iFrame/index";
import {getToken} from "@/utils/auth";
export default {
  name: "ReportView",
  components: {
      ReportDb,
      iFrame
    },
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
      // 报表查看表格数据
      reportViewList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      reportOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10000,
        reportId: undefined,
        reportName: undefined,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        reportId: [
          { required: true, message: "报表不能为空", trigger: "blur" }
        ],
        reportName: [
          { required: true, message: "报表名称不能为空", trigger: "blur" }
        ]
      },
      url:''
    };
  },
  created() {
    this.createdList();
  },
  methods: {
    //报表确认
    reportDbSubmit(row){
       this.form.reportId = row.jimuReportId
       this.form.reportName = row.dbChName
    },
    //打开报表
    handleReportOpen(){
      this.$refs.reportDb.reportOpen = true
    },
    //切换报表
    handdle(row){
       this.url = process.env.VUE_APP_JMREPORT_URL + "/view/"+ row.reportId +"?token=" + getToken();
    },
    /** 查询报表查看列表 */
    createdList() {
      this.loading = true;
      listReportView(this.queryParams).then(response => {
        this.reportViewList = response.rows;
        this.total = response.total;
        this.loading = false;
        if(this.reportViewList && this.reportViewList.length>0){
          this.url = process.env.VUE_APP_JMREPORT_URL + "/view/"+ this.reportViewList[0].reportId +"?token=" + getToken();
        }
      });
    },
    getList() {
      this.loading = true;
      listReportView(this.queryParams).then(response => {
        this.reportViewList = response.rows;
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
        reportId: undefined,
        reportName: undefined,
        createTime: undefined,
        updateTime: undefined,
        createBy: undefined,
        updateBy: undefined
      };
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
      this.title = "添加报表";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.reset();
      const id = row.id || this.ids
      getReportView(id).then(response => {
        this.loading = false;
        this.form = response.data;
        this.open = true;
        this.title = "修改报表";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.buttonLoading = true;
          if (this.form.id != null) {
            updateReportView(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).finally(() => {
              this.buttonLoading = false;
            });
          } else {
            addReportView(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除报表查看编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true;
        return delReportView(ids);
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
      this.download('report/reportView/export', {
        ...this.queryParams
      }, `reportView_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
<style scoped>
  .container{
    padding: 0;
    margin: 0;
    height: calc(100vh-84px);
  }
  .container::-webkit-scrollbar-thumb {
    border-radius: 10px;
  }
  .container::-webkit-scrollbar {
    width: 5px;
  }
  /* 修改滚动条样式 */
  .aside::-webkit-scrollbar-thumb {
    border-radius: 10px;
  }
  .aside::-webkit-scrollbar {
    width: 5px;
  }
  .main::-webkit-scrollbar-thumb {
    border-radius: 10px;
  }
  .main::-webkit-scrollbar {
    width: 5px;
  }
  .item{
    border-bottom: 1px solid #eff3fa;
    white-space: nowrap;
    line-height: 42px;
    cursor: pointer;
    font-size: 13px;
  }
  .item_font{
    border-bottom: 1px solid #eff3fa;
    white-space: nowrap;
    line-height: 42px;
    font-weight: 600;
  }
  .aside{
    width: 270px !important;
    background: #fff;
  }
  .main{
    padding: 0;
    margin: 0;
    width: calc(100%-350px);
  }
  .el-empty-icon{
    height: 100%;
    padding: 200px;
  }
  
</style>>

