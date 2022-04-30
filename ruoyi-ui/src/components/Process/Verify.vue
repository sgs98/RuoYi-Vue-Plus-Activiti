<template>
<div v-if="visible">
  <!-- 提交申请开始 -->
  <el-dialog  title="提交申请" :visible.sync="visible"  width="800px" :close-on-click-modal="false"
  append-to-body destroy-on-close @close="closeDialog" >
    <el-form v-loading="loading"  :rules="rules" ref="formData" :model="formData" status-icon >
      <el-form-item label="审批意见" prop="message" label-width="120px">
        <el-input  type="textarea" v-model="formData.message" maxlength="300" placeholder="请输入审批意见" :autosize="{ minRows: 4 }" show-word-limit ></el-input>
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
       <el-form-item label="是否委托" prop="delegate" label-width="120px" >
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
        <el-button type="primary" v-if="backNodeList && backNodeList.length>0" @click="openBack()" size="small">退回</el-button>
        <el-button type="primary" v-if="isMultiInstance" @click="addMultiClick()" size="small">加签</el-button>
        <el-button type="primary" v-if="multiList && multiList.length>0" @click="deleteMultiClick()" size="small">减签</el-button>
        <el-button type="primary" @click="transmitClick()" size="small">转办</el-button>
        <el-button size="small" @click="visible = false">取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
  <!-- 提交申请结束 -->

  <!-- 选择人员组件开始 -->
  <chooseWorkflowUser :dataObj="dataObj" :nodeId="nodeId" @confirmUser="clickUser" ref="wfUserRef"/>
  <sys-user :propUserList="delegateUserList" :multiple = false ref="userRef" @confirmUser="confirmUser"/>
  <!-- 选择人员组件结束 -->

  <!-- 转办申请开始 -->
  <el-dialog :close-on-click-modal="false" title="转发申请" :visible.sync="transmitVisible" width="500px"  append-to-body>
    <el-form  ref="transmitData" :model="transmitForm" :rules="transmitRules"  status-icon >
      <el-form-item label-width="80px" label="审批意见">
        <el-input  type="textarea" v-model="transmitForm.message" maxlength="300"  placeholder="请输入审批意见" :autosize="{ minRows: 4 }" show-word-limit ></el-input>
      </el-form-item>
      <el-form-item label-width="80px" label="转办人" prop="userName">
        <el-input  placeholder="请选择转办人" readonly v-model="transmitForm.userName" >
            <el-button  @click="transmitPeople() " slot="append" icon="el-icon-search" >选择</el-button>
        </el-input>
      </el-form-item>
      <el-form-item align="center">
        <el-button type="primary" @click="transmitSubmit('transmitData')" size="small">提交</el-button>
        <el-button size="small" @click="transmitVisible = false">取消</el-button>
      </el-form-item>
    </el-form>
    <sys-user :propUserList="transmitUserList" :multiple = false ref="transmitUserRef" @confirmUser="confirmTransmitUser"/>
  </el-dialog>
  <!-- 转办申请结束 -->
  <!-- 加签开始 -->
  <el-dialog  :close-on-click-modal="false" title="加签" :visible.sync="addMultiVisible" width="500px"  append-to-body>
    <el-form  :model="addMultiForm" status-icon >
      <!-- <el-form-item label-width="80px" label="审批意见">
        <el-input  type="textarea" v-model="transmitForm.message" maxlength="300"  placeholder="请输入审批意见" :autosize="{ minRows: 4 }" show-word-limit ></el-input>
      </el-form-item> -->
      <el-form-item label-width="80px" label="加签人" prop="userName">
        <el-input placeholder="请选择加签人" readonly v-model="addMultiForm.userName" >
            <el-button  @click="addMultiPeople() " slot="append" icon="el-icon-search" >选择</el-button>
        </el-input>
      </el-form-item>
      <el-form-item align="center">
        <el-button type="primary" @click="addMultiSubmit()" size="small">提交</el-button>
        <el-button size="small" @click="addMultiVisible = false">取消</el-button>
      </el-form-item>
    </el-form>
    <multi-user :taskId="taskId" :propUserList="addMultiUserList" ref="addMultiUserRef" @confirmUser="confirmAddMultiUser"/>
  </el-dialog>
  <!-- 加签结束 -->
  <!-- 减签开始 -->
  <el-dialog :close-on-click-modal="false" title="减签" :visible.sync="deleteMultiVisible" width="500px"  append-to-body>
    <el-table border  @selection-change="handleSelectionMultiList" :data="multiList" style="width: 100%">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="name" label="任务名称" width="180"/>
      <el-table-column prop="assignee" label="办理人" width="180"/>
      <el-table-column prop="assigneeId" v-show="true" label="办理人ID" width="180"/>
    </el-table>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" type="primary" @click="deleteMultiSubmit()">确定</el-button>
      <el-button size="small" @click="deleteMultiVisible = false">取消</el-button>
    </span>
  </el-dialog>
  <!-- 减签结束 -->
  <!-- 驳回开始 -->
  <back ref="backRef" :taskId = "taskId" @callBack="callBack()" :backNodeList = "backNodeList"/>
  <!-- 驳回结束 -->
</div>
</template>
<script>
import api from "@/api/workflow/task";
import ChooseWorkflowUser from "@/views/components/user/choose-workflow-user ";
import  SysUser from "@/views/components/user/sys-user";
import  MultiUser from "@/views/components/user/multi-user";
import Back from "@/components/Process/Back";
export default {
  props: {
    taskId: String,
    taskVariables: Object,
  },
  components: {
    ChooseWorkflowUser,
    SysUser,
    Back,
    MultiUser
  },
  data() {
    return {
      visible: false, // 弹出窗口，true弹出
      loading: false,
      transmitVisible: false,
      addMultiVisible: false,
      deleteMultiVisible: false,
      formData: {
        // 提交表单数据
        message: null,
        assigneeMap: {},
        delegateUserId: undefined,
        delegateUserName: undefined
      },
      transmitForm: {
        message: null,
        userId: null,
        userName: null
      },
      delegate: '2',//是否委托
      nextNodes: [], // 下一节点是否为结束节点
      rules: {
        assigneeMap: [
          { required: true, message: "请输入下一步审批人", trigger: "blur" },
        ],
      },
      transmitRules: {
        userName: [
          { required: true, message: "请输入转办人", trigger: "blur" },
        ],
      },
      nickName: {},
      // 查询审批人条件
      dataObj: {},
      // 节点id
      nodeId: undefined,
      // 委托用户反选
      delegateUserList: [],
      // 转发用户反选
      transmitUserList: [],
      // 加签用户反选
      addMultiUserList: [],
      isDelegate: false, //是否委托
      isMultiInstance: false, //是否为并行会签
      addMultiForm: {},//加签
      multiList: {},//可以减签的集合
      selectionMultiExecutionIds: [], //选中的减签执行ID
      selectionMultiTaskIds: [], //选中的减签任务ID
      selectionMultiAssigneeIds: [], //选中的减签人员ID
      backNodeList: [] //可驳回的节点

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
          this.nextNodes = data.list;
          this.isMultiInstance = data.isMultiInstance;
          this.multiList = data.multiList;
          this.backNodeList = data.backNodeList;
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
              // 回调事件
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
        // 回调事件
        this.$emit("callSubmit")
      }
      this.loading = false;
    },

    // 关闭
    closeDialog() {
      // 将表单清空
      this.$refs["formData"].resetFields();
      this.formData = {
          message: null,
          assigneeMap: {}
      }
      this.visible = false;
    },
    // 选择人员
    choosePeople(chooseWay, assigneeId, nodeId) {
      this.propUserList = [];
      if (chooseWay) {
        if (this.formData.assigneeMap[nodeId]) {
          this.dataObj = {
            chooseWay: chooseWay,
            assigneeId: assigneeId,
            ids: this.formData.assigneeMap[nodeId].split(",")
          };
        }else{
           this.dataObj = {
            chooseWay: chooseWay,
            assigneeId: assigneeId,
            ids: []
          };
        }
        this.nodeId = nodeId;
        this.$refs.wfUserRef.visible = true;
      }
    },
    //确定选择人员
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
      this.delegateUserList = []
      this.delegateUserList.push(this.formData.delegateUserId)
      this.$refs.userRef.visible = true
    },
    //确认委托人员
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
    },
    //打开转发窗口
    transmitClick(){
       this.transmitForm = {}
       this.transmitVisible = true
    },
    //提交转发
    transmitSubmit(formName){
        let params = {
          transmitUserId: this.transmitForm.userId,
          taskId: this.taskId,
          comment: this.transmitForm.message
        }
        api.transmitTask(params).then(response=>{
          if(response.code === 200){
            // 刷新数据
            this.$message.success("办理成功");
            // 将表单清空
            this.$refs[formName].resetFields();
            // 关闭窗口
            this.visible = false;
            this.transmitVisible = false;
            // 回调事件
            this.$emit("callSubmit")
          }
          this.loading = false;
        })
    },
    //打开转办人员组件
    transmitPeople(){
      this.transmitUserList = []
      this.transmitUserList.push(this.transmitForm.userId)
      this.$refs.transmitUserRef.visible = true
    },
    //确认转办人员
    confirmTransmitUser(data){
      this.transmitForm.userId = data[0].userId
      this.transmitForm.userName = data[0].nickName
      this.$forceUpdate()
      this.$refs.transmitUserRef.visible = false
    },
    //打开加签窗口
    addMultiClick(){
       this.addMultiForm = {}
       this.addMultiVisible = true
    },
    //打开加签人员组件
    addMultiPeople(){
      this.addMultiUserList = []
      this.addMultiUserList = this.addMultiForm.assignees
      this.$refs.addMultiUserRef.visible = true
    },
    //确认加签人员
    confirmAddMultiUser(data){
      let assignees = data.map((item) => {
        return item.userId;
      });
      let nickNames = data.map((item) => {
        return item.nickName;
      });
      this.addMultiForm.assignees = assignees
      this.addMultiForm.userName = nickNames.join(",")
      this.$forceUpdate()
      this.$refs.addMultiUserRef.visible = false
    },
    //加签
    addMultiSubmit(){
      let params = {
        taskId: this.taskId,
        assignees: this.addMultiForm.assignees
      }
      api.addMultiInstanceExecution(params).then(response => {
        if(response.code === 200){
          // 刷新数据
            this.$message.success("办理成功");
            // 将表单清空
            this.addMultiForm = {}
            // 关闭窗口
            this.visible = false;
            this.addMultiVisible = false;
            // 回调事件
            this.$emit("callSubmit")
        }
      })
    },
     //打开减签窗口
    deleteMultiClick(){
       this.deleteMultiVisible = true
    },
    //减签复选框
    handleSelectionMultiList(val){
      this.selectionMultiExecutionIds = val.map((item) => {
        return item.executionId;
      });
      this.selectionMultiTaskIds = val.map((item) => {
        return item.id;
      });
      this.selectionMultiAssigneeIds = val.map((item) => {
        return item.assigneeId;
      });
    },
    //减签
    deleteMultiSubmit(){
      let params = {
        taskId: this.taskId,
        executionIds: this.selectionMultiExecutionIds,
        taskIds: this.selectionMultiTaskIds,
        assigneeIds: this.selectionMultiAssigneeIds,
      }
      api.deleteMultiInstanceExecution(params).then(response => {
        if(response.code === 200){
          // 刷新数据
            this.$message.success("办理成功");
            // 关闭窗口
            this.visible = false;
            this.deleteMultiVisible = false;
            // 回调事件
            this.$emit("callSubmit")
        }
      })
    },
    //打开驳回弹窗
    openBack(){
        this.$refs.backRef.visible = true
    },
    //关闭退出弹窗
    callBack(){
        // 关闭窗口
        this.visible = false;
        // 回调事件
        this.$emit("callSubmit")
    },
    //重置表单
    reset(){
        this.isDelegate = false
        this.delegate = '2'
        this.transmitForm = {}
        this.formData.message = null
        this.formData.assigneeMap = {}
        this.nickName = {}
    }
  },
};
</script>
