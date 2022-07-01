<template>
  <div class="app-container">
    <!-- 添加或修改请假业务对话框 -->
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
          <el-select style="width:100%" v-model="form.leaveType" placeholder="请选择请假类型">
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
            style="width:100%"
            v-model="form.startDate"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择请假开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="请假结束时间" prop="endDate">
          <el-date-picker style="width:100%" clearable size="small"
            v-model="form.endDate"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择请假结束时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" v-show="parentTaskId===null" class="dialog-footer">
        <el-button :loading="buttonLoading" size="mini" type="info" @click="submitForm">暂存</el-button>
        <el-button :loading="buttonLoading" size="mini" type="primary" @click="submitForm">提交</el-button>
        <el-button size="mini" @click="cancel">取 消</el-button>
      </div>
      <verify ref="verifyRef" :taskId="taskId" @callSubmit="callSubmit"
      :taskVariables="taskVariables" :sendMessage="sendMessage"></verify>
  </div>
</template>

<script>
import { getLeave} from "@/api/demo/leave";
import processAip from "@/api/workflow/processInst";
 import verify from "@/components/Process/Verify";
export default {
  name: "Leave",
  dicts: ['bs_leave_type'],
  props: {
      businessKey: String, // 业务唯一标识
      parentTaskId: String, // 父级任务id
      taskId: String // 任务id
  },
  components: {
      verify
    },
  data() {
    return {
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 请假业务表格数据
      leaveList: [],
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
      taskVariables: undefined,
      //消息提醒
      sendMessage: {}
    };
  },
  watch: {
      businessKey: {
          immediate: true, // 很重要！！！
          handler (newVal) {
              if(newVal) {
                  this.getById()
              }
          }
      }
  },
  methods: {
    // 取消按钮
    cancel() {
      this.$emit("closeForm")
    },
    callSubmit(){
      this.$emit("closeForm")
    },
    async getById() {
        const {data} = await getLeave(this.businessKey)
        this.form = data;
    },
    /** 提交按钮 */
    submitForm() {
      getLeave(this.businessKey).then(response => {
            this.taskVariables = {
                 entity: response.data,
                 userId :1
            };
            this.sendMessage = {
              title:'请假申请',
              messageContent:'单据【'+this.form.id+"】申请"
            }
      });
      this.$refs.verifyRef.visible = true
    },
    //提交流程
    submitFormAppply(){
        getLeave(this.form.id).then(response => {
            const data = {
                processKey: 'testkey', // key
                classFullName: 'com.ruoyi.demo.leave.domain.BsLeave', // 全类名
                businessKey: response.data.id, // 业务id
                variables: { entity: response.data }, // 变量
            }
            processAip.startProcessApply(data).then(response => {
              if(response.code===200){
                this.$modal.msgSuccess(response.msg);
              }
            })
        });
    }
  }
};
</script>
