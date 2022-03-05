<template>
  <el-dialog  title="提交申请" :visible.sync="visible"  width="600px"  append-to-body destroy-on-close @close="closeDialog" >
    <el-form v-loading="loading"  :rules="rules" ref="formData" :model="formData" status-icon >
      <el-form-item label="审批意见" prop="message" label-width="120px">
        <el-input  type="textarea" v-model="formData.message" maxlength="300"  placeholder="请输入审批意见" :autosize="{ minRows: 4 }" show-word-limit ></el-input>
      </el-form-item>
      <el-form-item v-if="!isDelegate&&nextNodes && nextNodes.length > 0" label="下一步审批人"  prop="assigneeMap" label-width="120px" >
        <div v-for="(item, index) in nextNodes" :key="index">
          <span>【{{ item.nodeName }}】：</span>
          <el-input v-show="false" v-model="formData.assigneeMap[item.nodeId]" />
          <el-input  placeholder="请选择审批人" readonly v-model="nickName[item.nodeId]" >
              <el-button  @click="choosePeople(item.chooseWay, item.assigneeId, item.nodeId) " slot="append" icon="el-icon-search" >选择</el-button>
          </el-input>
        </div>
      </el-form-item>
       <el-form-item label="是否委托"  prop="delegate" label-width="120px" >
         <el-col :span="12">
           <div class="grid-content bg-purple">
             <el-radio-group v-model="delegate" @change="changeDelegate" size="small">
              <el-radio label="1" border>是</el-radio>
              <el-radio label="2" border>否</el-radio>
            </el-radio-group>
           </div>
        </el-col>
         <el-col :span="12" v-show="isDelegate">
           <div class="grid-content bg-purple">
             <el-input v-show="false" v-model="formData.delegateUserId"/>
             <el-input size="small" v-model="formData.delegateUserName" readonly placeholder="请选择人员" class="input-with-select">
              <el-button slot="append" @click="chooseUser" icon="el-icon-search"></el-button>
            </el-input>
           </div>
        </el-col>
      </el-form-item>
      <el-form-item align="center">
        <el-button type="primary" @click="submitForm('formData')" size="small">提交</el-button>
        <el-button size="small" @click="visible = false">取消</el-button>
      </el-form-item>
    </el-form>
    <!-- 选择人员组件 -->
    <chooseWorkflowUser :dataObj="dataObj" :propUserList="propUserList" :nodeId="nodeId" @confirmUser="clickUser" ref="wfUserRef"/>
    <sys-user :propUserList="propUserList" :multiple = false ref="userRef" @confirmUser="confirmUser"/>
  </el-dialog>
</template>
<script>
import api from "@/api/workflow/task";
import { selectListUserByIds } from "@/api/system/user";
import ChooseWorkflowUser from "@/views/components/user/choose-workflow-user ";
import  SysUser from "@/views/components/user/sys-user";
export default {
  props: {
    taskId: String,
    taskVariables: Object,
  },
  components: {
    ChooseWorkflowUser,
    SysUser
  },
  data() {
    return {
      visible: false, // 弹出窗口，true弹出
      loading: false,
      formData: {
        // 提交表单数据
        message: null,
        assigneeMap: {},
        delegateUserId: undefined,
        delegateUserName: undefined
      },
      delegate: '2',//是否委托
      nextNodes: [], // 下一节点是否为结束节点
      rules: {
        assigneeMap: [
          { required: true, message: "请输入下一步审批人", trigger: "blur" },
        ],
      },
      nickName: {},
      // 查询审批人条件
      dataObj: {},
      // 节点id
      nodeId: undefined,
      propUserList: [],
      isDelegate: false //是否委托
    };
  },

  watch: {
    async taskVariables(newVal) {
      if (newVal) {
        try {
          this.loading = true;
          // 获取下一节点信息
          let params = {
             taskId: this.taskId,
             variables: this.taskVariables
          }
          const { data } = await api.getNextNodeInfo(params);
          this.nextNodes = data;
          this.loading = false;
        } catch (error) {
          this.loading = false;
        }
      }
    },
  },

  methods: {
    // 提交表单数据
    async submitForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          if(this.delegate === '1'){
             let data = {
                taskId: this.taskId,
                delegateUserId: this.formData.delegateUserId,
                delegateUserName: this.formData.delegateUserName
             }
             this.delegateTaskFn(formName,data)
             return false
          }
          // 校验通过，提交表单数据
          this.loading = true;
          let obj = this.formData.assigneeMap
          let objData = JSON.stringify(obj)
         /*  if(objData === '{}'){
            this.$message.error("请输入下一步审批人");
            this.loading = false;
            return
          } */
          try {
            // 审批人转为数组
             const data = {
              taskId: this.taskId,
              message: this.formData.message,
              variables: this.taskVariables, // 变量
              assigneeMap: this.formData.assigneeMap, // 审批人转为数组
            };
             let response = await api.completeTask(data);
            if (response.code === 200) {
              // 刷新数据
              this.$message.success("办理成功");
              // 将表单清空
              this.$refs[formName].resetFields();
              // 关闭窗口
              this.visible = false;
              // 事件
              this.$emit("callSubmit")
            }
            this.loading = false;
          } catch (e) {
            this.loading = false;
          }
        }
      });
    },
    // 办理委托任务
    async delegateTaskFn(formName,data){
      let response = await api.delegateTask(data);
      if(response.code === 200){
        // 刷新数据
        this.$message.success("办理成功");
        // 将表单清空
        this.$refs[formName].resetFields();
        // 关闭窗口
        this.visible = false;
        // 事件
        this.$emit("callSubmit")
      }
      this.loading = false;
    },

    // 关闭
    closeDialog() {
      // 将表单清空
      this.$refs["formData"].resetFields();
      (this.formData = { message: null, assigneeMap: {} }),
        (this.visible = false);
    },
    // 选择人员
    choosePeople(chooseWay, assigneeId, nodeId) {
      this.propUserList = [];
      if (chooseWay) {
        this.dataObj = {
          chooseWay: chooseWay,
          assigneeId: assigneeId,
        };
        this.nodeId = nodeId;
        if (this.formData.assigneeMap[nodeId]) {
          let userNameList = this.formData.assigneeMap[nodeId].split(",");
          selectListUserByIds(userNameList).then((response) => {
            this.propUserList = response.data;
          });
        }
        this.$refs.wfUserRef.visible = true;
      }
    },
    clickUser(userList, nodeId) {
      let assignee = userList.map((item) => {
        return item.userId;
      });
      let nickName = userList.map((item) => {
        return item.nickName;
      });
      let arrAssignee = assignee.join(",");
      let arrNickName = nickName.join(",");

      this.formData.assigneeMap[nodeId] = arrAssignee;
      this.nickName[nodeId] = arrNickName
      this.$forceUpdate();
    },
    //选择委托人
    chooseUser(){
      this.$refs.userRef.visible = true
    },
    confirmUser(data){
      this.formData.delegateUserId = data[0].userId
      this.formData.delegateUserName = data[0].nickName
      this.$refs.userRef.visible = false
    },
    //是否显示选择委托人
    changeDelegate(val){
      if(val == 1){
        this.isDelegate = true
      }else{
        this.isDelegate = false
      }
    }
  },
};
</script>
