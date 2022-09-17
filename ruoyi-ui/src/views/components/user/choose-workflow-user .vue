<template>
<el-dialog title="用户" :visible.sync="visible" @close="close"  width="60%" append-to-body v-dialogDrag :close-on-click-modal="false">
  <div class="app-container">
    <el-row :gutter="20">
      <!--用户数据-->
      <el-col :span="20" :xs="24">
        <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="归属部门" prop="deptId">
            <treeselect v-model="queryParams.deptId" style="width: 240px" :options="deptOptions" :show-count="true" placeholder="请选择归属部门" />
          </el-form-item>
          <el-form-item label="用户名称" prop="userName">
            <el-input
              v-model="queryParams.userName"
              placeholder="请输入用户名称"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="手机号码" prop="phonenumber">
            <el-input
              v-model="queryParams.phonenumber"
              placeholder="请输入手机号码"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search"  @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh"  @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="userList" ref="multipleTable" :row-key="getRowKey" @selection-change="handleSelectionChange">
          <el-table-column type="selection" :reserve-selection="true" width="50" align="center" />
          <el-table-column label="用户编号" align="center" key="userId" prop="userId" v-if="columns[0].visible" />
          <el-table-column label="用户名称" align="center" key="userName" prop="userName" v-if="columns[1].visible" :show-overflow-tooltip="true" />
          <el-table-column label="用户昵称" align="center" key="nickName" prop="nickName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
          <el-table-column label="部门" align="center" key="deptName" prop="dept.deptName" v-if="columns[3].visible" :show-overflow-tooltip="true" />
          <el-table-column label="手机号码" align="center" key="phonenumber" prop="phonenumber" v-if="columns[4].visible" width="120" />
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[5].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
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
      </el-col>
    </el-row>
  </div>
  <!-- 选中的用户 -->
  <div>
    <el-tag v-for="user in chooseUserList" :key="user.userName" style="margin:2px"
    closable @close="handleCloseTag(user)" >{{user.userName}} </el-tag>
  </div>
  <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="confirmUser">确认</el-button>
        <el-button @click="cancel">取 消</el-button>
  </div>
</el-dialog>
</template>

<script>
import { getWorkflowUserListByPage } from "@/api/workflow/workflowUser";
import { deptTreeSelect } from "@/api/system/user";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  props: {
    //是否多选
    multiple: {
      type: Boolean,
      default: true,
    },
    //传入的条件
    dataObj: {
      type: Object,
      default: {}
    },
    // 节点id
    nodeId: {
      type: String,
      default: ''
    }
  },
  name: "User",
  dicts: ['sys_user_sex'],
  components: { Treeselect },
  data() {
    return {
      // 遮罩层
      loading: true,
      buttonLoading: false,
      // 是否显示弹出层
      visible: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      userList: [],
      // 部门树选项
      deptOptions: undefined,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        deptId: undefined,
        type: undefined,
        params: undefined,
        ids:[]
      },
      // 列信息
      columns: [
        { key: 0, label: `用户编号`, visible: true },
        { key: 1, label: `用户名称`, visible: true },
        { key: 2, label: `用户昵称`, visible: true },
        { key: 3, label: `部门`, visible: true },
        { key: 4, label: `手机号码`, visible: true },
        { key: 5, label: `创建时间`, visible: true }
      ],
      // 保存选择的用户
      chooseUserList: [],
      getRowKey(row) {
        return row.userId
      }
    };
  },
  watch: {
    dataObj(val) {
       this.queryParams.params = val.assigneeId
       this.queryParams.type = val.chooseWay
       this.queryParams.ids = val.ids
       this.$nextTick(() => {
           this.$refs.multipleTable.clearSelection();
       });
       this.getList()
    },
  },
  created() {
    this.getTreeselect();
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      this.chooseUserList = []
      getWorkflowUserListByPage(this.queryParams).then(response => {
          let res = response.data.page
          this.userList = res.rows;
          this.total = res.total;
          //反选
          if(response.data.list){
            this.chooseUserList = response.data.list
            response.data.list.forEach(row => {
              this.$refs.multipleTable.toggleRowSelection(row,true);
            })
          }
          this.loading = false;
      });
    },
    /** 查询部门下拉树结构 */
    getTreeselect() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },
    // 取消按钮
    cancel() {
      this.visible = false
      this.chooseUserList = []
    },
    // 关闭
    close() {
      this.visible = false
      this.chooseUserList = []
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      //this.resetForm("queryForm");
    },
    // 多选框选中数据
    handleSelectionChange(val) {
        if(this.multiple){
          this.chooseUserList = val.filter((element,index,self)=>{
             return self.findIndex(x=>x.userId===element.userId) === index
          })
        }else{
          this.chooseUserList = val
          if (val.length > 1) {
            let delRow = val.shift();
            this.$refs.multipleTable.toggleRowSelection(delRow, false);
          }
          if(val.length === 0){
            this.chooseUserList = null
          }
        }
    },
    // 删除tag
    handleCloseTag(user){
      this.chooseUserList.splice(this.chooseUserList.indexOf(user), 1);
       this.$refs.multipleTable.toggleRowSelection(user,false)
       this.userList.forEach((row,index)=>{
          if(user.userId === row.userId){
             this.$refs.multipleTable.toggleRowSelection(this.userList[index],false)
          }
       })
    },
    // 确认
    confirmUser(){
      if(this.chooseUserList.length>0){
        this.$emit("confirmUser",this.chooseUserList,this.nodeId)
        this.visible = false
      }else{
        this.$modal.msgWarning("请选择人员！");
      }
    }
  }
};
</script>
<style scoped>

</style>
