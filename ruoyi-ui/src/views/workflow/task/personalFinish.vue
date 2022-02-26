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
            <el-table-column  align="center" prop="processDefinitionName" label="流程定义名称"/>
            <el-table-column  align="center" prop="processDefinitionKey" label="流程定义Key"/>
            <el-table-column align="center" prop="version" label="版本号" width="90" >
              <template slot-scope="{row}"> v{{row.version}}.0</template>
            </el-table-column>
            <el-table-column  align="center" prop="assignee" label="流程办理人"  min-width="130"/>
            <el-table-column  align="center" prop="startTime" label="启动时间" width="160"/>
            <el-table-column  align="center" prop="endTime" label="结束时间" width="160"/>
        </el-table>

        <pagination v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList" />
    </div>
</template>

<script>
  import api from '@/api/workflow/task'

  export default {
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
        }
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
          api.getTaskFinishByPage(this.queryParams).then(response => {
            this.dataList = response.rows;
            this.total = response.total;
            this.loading = false;
          })
      }
    }
  }
</script>
