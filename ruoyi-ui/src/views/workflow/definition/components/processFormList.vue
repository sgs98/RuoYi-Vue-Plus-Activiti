<template>
  <el-dialog title="表单" :visible.sync="visible" v-if="visible" width="60%" :close-on-click-modal="false" append-to-body>
    <el-form label-width="110px" :model="formData" :rules="rulesFrom" ref="formDataRef">
      <el-row>
        <el-col class="line" :span="12">
          <el-form-item label="流程定义Key" prop="processDefinitionKey">
            <el-input v-model="formData.processDefinitionKey" disabled></el-input>
          </el-form-item>
        </el-col>
        <el-col class="line" :span="12">
          <el-form-item label="流程定义名称" prop="processDefinitionName">
            <el-input v-model="formData.processDefinitionName" disabled></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col class="line" :span="24">
          <el-form-item label="表单类型" prop="businessType">
            <el-radio-group @change="change($event)" v-model="formData.businessType">
              <el-radio :label="0" border>动态表单</el-radio>
              <el-radio :label="1" border>业务表单</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData.businessType === 0">
        <el-col class="line" :span="12">
          <el-form-item label="表单Key" prop="formKey">
            <el-input v-model="formData.formKey" placeholder="请选择表单" disabled>
             <el-button slot="append" @click="handerOpenForm" icon="el-icon-search"></el-button>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col class="line" :span="12">
          <el-form-item label="表单名称" prop="formName">
            <el-input v-model="formData.formName" disabled></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData.businessType === 0">
        <el-col class="line" :span="24">
          <el-form-item label="表单参数">
            <el-input type="textarea" placeholder="请输入表单参数,动态表单中参数id,多个用英文逗号隔开" v-model="formData.formVariable" @input="change($event)"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="formData.businessType === 1">
        <el-col class="line" :span="12">
          <el-form-item label="组件名称" prop="componentName">
            <el-input placeholder="请输入组件名称" v-model="formData.componentName"/>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-dialog title="表单" :visible.sync="formVisible" v-if="visible" width="70%" :close-on-click-modal="false" append-to-body>
      <div class="app-container">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="表单key" prop="formKey">
            <el-input
              v-model="queryParams.formKey"
              placeholder="请输入表单key"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="表单名称" prop="formName">
            <el-input
              v-model="queryParams.formName"
              placeholder="请输入表单名称"
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

        <el-table v-loading="loading" :highlight-current-row="true" :data="dynamicFormList" @row-click="handleChange">
          <el-table-column label="主键" align="center" prop="id" v-if="false"/>
          <el-table-column label="表单key" align="center" prop="formKey" />
          <el-table-column label="表单名称" align="center" prop="formName" />
          <el-table-column label="表单备注" align="center" prop="formRemark" />
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </el-dialog>
     <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="primary" @click="submitForm('formDataRef')">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listDynamicFormEnable } from "@/api/workflow/dynamicForm";
import { addProcessDefSetting,checkProcessDefSetting } from "@/api/workflow/processDefSetting";
export default {
  props:{
    formData: {
      type: Object,
      default:()=>{}
    }
  },
  name: "DynamicFormList",
  data() {
    return {
      // 显示隐藏
      visible: false,
      formVisible: false,
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 流程单表格数据
      dynamicFormList: [],
      // 弹出层标题
      title: "",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        formKey: undefined,
        formName: undefined,
      },
      // 表单校验
      rulesFrom: {
        processDefinitionKey: [
          { required: true, message: '流程定义Key不能为空', trigger: 'blur' }
        ],
        processDefinitionName: [
          { required: true, message: '流程定义名称不能为空', trigger: 'blur' }
        ],
        formKey: [
          { required: true, message: '表单Key不能为空', trigger: 'blur' }
        ],
        formName: [
          { required: true, message: '表单名称不能为空', trigger: 'blur' }
        ],
        businessType: [
          { required: true, message: '业务类型不能为空', trigger: 'blur' }
        ],
        componentName: [
          { required: true, message: '组件名称不能为空', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
  },
  methods: {
    change(e){
        this.$forceUpdate(e)
        this.$refs["formDataRef"].clearValidate()
    },
    /** 查询流程单列表 */
    getList() {
      this.loading = true;
      listDynamicFormEnable(this.queryParams).then(response => {
        this.dynamicFormList = response.rows;
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
    // 选中数据
    handleChange(row) {
      this.$set(this.formData,'formId',row.id)
      this.$set(this.formData,'formKey',row.formKey)
      this.$set(this.formData,'formName',row.formName)
      this.formVisible = false;
    },
    // 打开表单
    handerOpenForm(){
      this.getList();
      this.formVisible = true
    },
    // 确认
    submitForm(formName){
      this.loading = true;
      this.$refs[formName].validate((valid) => {
      if (valid) {
        let param = ''
        if(this.formData.businessType === 0){
          param = this.formData.formId
        }else{
          param = this.formData.componentName
        }
        checkProcessDefSetting(this.formData.processDefinitionId,param,this.formData.businessType).then(response => {
          if(response.data){
            this.$confirm(response.msg, '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              this.formData.ids = response.data
              addProcessDefSetting(this.formData).then(response => {
                this.$modal.msgSuccess("保存成功");
                this.loading = false;
                this.visible = false;
                this.$emit("callbackFn")
              });
            })
          }else{
              addProcessDefSetting(this.formData).then(response => {
                this.$modal.msgSuccess("保存成功");
                this.loading = false;
                this.visible = false;
                this.$emit("callbackFn")
              });
          }
        })
      }
    })
    }
  }
};
</script>
<style scoped>
.line{
  padding-bottom: 20px;
}
</style>
