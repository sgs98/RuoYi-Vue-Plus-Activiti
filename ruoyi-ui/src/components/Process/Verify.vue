<template>
<div v-if="visible">
  <!-- 提交申请开始 -->
  <el-dialog  v-if="visible"  title="提交申请" :visible.sync="visible"  width="800px" :close-on-click-modal="false"
  append-to-body destroy-on-close @close="closeDialog" >
    <el-form v-loading="loading"  :rules="rules" ref="formData" :model="formData" status-icon >
      <el-form-item label="审批意见" prop="message" v-if="businessStatus.status==='waiting'" label-width="120px">
        <el-input  type="textarea" v-model="formData.message" maxlength="300" placeholder="请输入审批意见" :autosize="{ minRows: 4 }" show-word-limit ></el-input>
      </el-form-item>
      <el-form-item v-if="nextNodes && nextNodes.length > 0" label="下一步审批人"  prop="assigneeMap" label-width="120px" >
        <div v-for="(item, index) in nextNodes" :key="index">
          <span>【{{ item.nodeName }}】：</span>
          <el-input v-show="false" v-model="formData.assigneeMap[item.nodeId]" />
          <el-input  placeholder="请选择审批人" readonly v-model="nickName[item.nodeId]" >
              <el-button  @click="choosePeople(item.chooseWay, item.assigneeId, item.nodeId) " slot="append" icon="el-icon-search" >选择</el-button>
          </el-input>
        </div>
      </el-form-item>
      <el-form-item label="是否抄送" prop="isCopy" label-width="120px" v-if="setting.isCopy">
         <el-col :span="12">
           <div class="grid-content bg-purple">
             <el-radio-group v-model="formData.isCopy" size="small">
              <el-radio :label="true" border>是</el-radio>
              <el-radio :label="false" border>否</el-radio>
            </el-radio-group>
           </div>
        </el-col>
         <el-col :span="12" v-if="formData.isCopy">
           <div class="grid-content bg-purple">
             <el-input v-show="false" v-model="formData.assigneeIds"/>
             <el-input size="small" v-model="formData.assigneeNames" readonly placeholder="请选择人员" class="input-with-select">
              <el-button slot="append" @click="chooseCopyUser" icon="el-icon-search"></el-button>
            </el-input>
           </div>
        </el-col>
      </el-form-item>
      <el-form-item align="center">
        <el-button type="primary" @click="submitForm('formData')" size="small">提交</el-button>
        <el-button type="primary" v-if="backNodeList && backNodeList.length>0" @click="openBack()" size="small">退回</el-button>
        <el-button type="primary" v-if="isMultiInstance && setting.addMultiInstance" @click="addMultiClick()" size="small">加签</el-button>
        <el-button type="primary" v-if="multiList && multiList.length>0 && setting.deleteMultiInstance" @click="deleteMultiClick()" size="small">减签</el-button>
        <el-button type="primary" v-if="setting.isDelegate" @click="delegateClick()" size="small">委托</el-button>
        <el-button type="primary" v-if="setting.isTransmit" @click="transmitClick()" size="small">转办</el-button>
        <el-button size="small" @click="closeDialog()">取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
  <!-- 提交申请结束 -->

  <!-- 选择人员组件开始 -->
  <chooseWorkflowUser :dataObj="dataObj" :nodeId="nodeId" @confirmUser="clickUser" ref="wfUserRef"/>
  <sys-dept-user :propUserList="copyUserList" ref="userCopyRef" @confirmUser="confirmCopyUser"/>
  <!-- 选择人员组件结束 -->
  <!-- 委托申请开始 -->
  <el-dialog :close-on-click-modal="false" title="委托申请" :visible.sync="delegateVisible" width="700px"  append-to-body>
    <el-form  ref="delegateData" :model="delegateForm" status-icon >
      <!-- <el-form-item label-width="80px" label="审批意见">
        <el-input  type="textarea" v-model="delegateForm.message" maxlength="300"  placeholder="请输入审批意见" :autosize="{ minRows: 4 }" show-word-limit ></el-input>
      </el-form-item> -->
      <el-form-item label-width="80px" label="委托人" prop="userName">
        <el-input  placeholder="请选择委托人" readonly v-model="delegateForm.delegateUserName" >
            <el-button  @click="delegatePeople() " slot="append" icon="el-icon-search" >选择</el-button>
        </el-input>
      </el-form-item>
      <el-form-item align="center">
        <el-button type="primary" @click="delegateSubmit()" size="small">提交</el-button>
        <el-button size="small" @click="delegateVisible = false">取消</el-button>
      </el-form-item>
    </el-form>
    <sys-dept-user :propUserList="delegateUserList" :multiple = false ref="delegateUserRef" @confirmUser="confirmDelegateUser"/>
  </el-dialog>
  <!-- 委托申请结束 -->
  <!-- 转办申请开始 -->
  <el-dialog :close-on-click-modal="false" title="转发申请" :visible.sync="transmitVisible" width="700px"  append-to-body>
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
    <sys-dept-user :propUserList="transmitUserList" :multiple = false ref="transmitUserRef" @confirmUser="confirmTransmitUser"/>
  </el-dialog>
  <!-- 转办申请结束 -->
  <!-- 加签开始 -->
  <el-dialog  :close-on-click-modal="false" title="加签" :visible.sync="addMultiVisible" width="700px"  append-to-body>
    <el-form  :model="addMultiForm" status-icon >
      <el-form-item label-width="80px" label="加签人" prop="nickNames">
        <el-input placeholder="请选择加签人" readonly v-model="addMultiForm.nickNames" >
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
  <el-dialog :close-on-click-modal="false" title="减签" :visible.sync="deleteMultiVisible" width="700px"  append-to-body>
    <el-table border  @selection-change="handleSelectionMultiList" :data="multiList" style="width: 100%">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="name" label="任务名称" width="200"/>
      <el-table-column prop="assignee" label="办理人" width="200"/>
      <el-table-column prop="assigneeId" v-show="false" label="办理人ID" width="200"/>
    </el-table>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" type="primary" @click="deleteMultiSubmit()">确定</el-button>
      <el-button size="small" @click="deleteMultiVisible = false">取消</el-button>
    </span>
  </el-dialog>
  <!-- 减签结束 -->
  <!-- 驳回开始 -->
  <back ref="backRef" :taskId = "taskId" @callBack="callBack()"
  :backNodeList = "backNodeList" :sendMessage="sendMessage"/>
  <!-- 驳回结束 -->
</div>
</template>
<script>
import api from "@/api/workflow/task";
import ChooseWorkflowUser from "@/views/components/user/choose-workflow-user ";
import  SysDeptUser from "@/views/components/user/sys-dept-user";
import  MultiUser from "@/views/components/user/multi-user";
import Back from "@/components/Process/Back";
export default {
  props: {
    taskId: String,
    taskVariables: Object,
    sendMessage: Object
  },
  components: {
    ChooseWorkflowUser,
    SysDeptUser,
    Back,
    MultiUser
  },
  data() {
    return {
      //弹出窗口，true弹出
      visible: false,
      loading: false,
      transmitVisible: false,
      delegateVisible: false,
      addMultiVisible: false,
      deleteMultiVisible: false,
      //提交表单数据
      formData: {
        message: null,
        assigneeMap: {},
        //是否抄送
        isCopy: false,
        //抄送人id
        assigneeIds: undefined,
        //抄送人名称
        assigneeNames: undefined,
        //消息提醒
        sendMessage: {}
      },
      //转办
      transmitForm: {
        message: null,
        userId: null,
        userName: null,
        taskId: null,
        sendMessage: {}
      },
      //委托
      delegateForm:{
        delegateUserId: null,
        delegateUserName: null,
        taskId: null,
        sendMessage: {}
      },
      //是否抄送
      isCopy: '2',
      //下一节点
      nextNodes: [],
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
      //查询审批人条件
      dataObj: {},
      //节点id
      nodeId: undefined,
      //委托用户反选
      delegateUserList: [],
      //抄送用户反选
      copyUserList: [],
      //转发用户反选
      transmitUserList: [],
      //加签用户反选
      addMultiUserList: [],
      //是否为会签
      isMultiInstance: false,
      //加签
      addMultiForm: {},
      //减签
      deleteMultiForm: {},
      //可以减签的集合
      multiList: [],
      //可驳回的集合
      backNodeList: [],
      //按钮设置
      setting: {},
      //流程状态
      businessStatus: {},
      //消息提醒类型1.站内信,2.邮箱,3.短信
      sendMessageType:[1],
    };
  },

  watch: {
    async visible(newVal) {
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
          this.setting = data.setting
          this.businessStatus = data.businessStatus
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
          this.loading = true;
          try {
            this.formData.variables = this.taskVariables
            this.formData.taskId = this.taskId
            //消息提醒
            this.formData.sendMessage = {
                title: this.sendMessage.title,
                type: this.sendMessageType,
                messageContent: this.$store.state.user.name+"提交了"+this.sendMessage.messageContent
            }
            if(this.businessStatus.status === 'draft'){
                 this.formData.message = '提交单据'
            }
            let response = await api.completeTask(this.formData);
            if (response.code === 200) {
              // 刷新数据
              this.$message.success("办理成功");
              // 将表单清空
              this.$refs[formName].resetFields();
              // 关闭窗口
              this.visible = false;
              // 回调事件
              this.$emit("submitCallback")
            }
            this.loading = false;
          } catch (e) {
            this.loading = false;
          }
        }
      });
    },
    // 关闭
    closeDialog() {
      // 将表单清空
      this.$refs["formData"].resetFields();
      this.formData = {
          message: null,
          assigneeMap: {},
          isCopy: false
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
    //提交委托申请
    async delegateSubmit(){
      this.delegateForm.taskId = this.taskId
      //消息提醒
      this.delegateForm.sendMessage = {
          title: this.sendMessage.title,
          type: this.sendMessageType,
          messageContent: this.$store.state.user.name+"委托了"+this.sendMessage.messageContent
      }
      let response = await api.delegateTask(this.delegateForm);
      if(response.code === 200){
        // 刷新数据
        this.$message.success("办理成功");
        // 将表单清空
        this.delegateForm = {};
        // 关闭窗口
        this.visible = false;
        // 回调事件
        this.$emit("submitCallback")
      }
      this.loading = false;
    },
    //打开委托申请
    delegateClick(){
      this.delegateForm = {}
      this.delegateVisible = true
    },
    //打开委托人员组件
    delegatePeople(){
      this.delegateUserList = []
      this.delegateUserList.push(this.delegateForm.delegateUserId)
      this.$refs.delegateUserRef.visible = true
    },
    //确认委托人员
    confirmDelegateUser(data){
      this.delegateForm.delegateUserId = data[0].userId
      this.delegateForm.delegateUserName = data[0].nickName
      this.$forceUpdate()
      this.$refs.delegateUserRef.visible = false
    },
    //选择抄送人
    chooseCopyUser(){
      this.copyUserList = []
      this.copyUserList.push(this.formData.delegateUserId)
      this.$refs.userCopyRef.visible = true
    },
    //确认抄送人员
    confirmCopyUser(data){
      let assignee = data.map((item) => {
        return item.userId;
      });
      let nickName = data.map((item) => {
        return item.nickName;
      });
      this.formData.assigneeIds = assignee.join(",")
      this.formData.assigneeNames = nickName.join(",")
      this.$refs.userCopyRef.visible = false
    },
    //打开转发窗口
    transmitClick(){
       this.transmitForm = {}
       this.transmitVisible = true
    },
    //提交转发
    transmitSubmit(formName){
        this.transmitForm = {
           transmitUserId: this.transmitForm.userId,
           taskId: this.taskId,
           comment: this.transmitForm.message
        }
        //消息提醒
        this.transmitForm.sendMessage = {
            title: this.sendMessage.title,
            type: this.sendMessageType,
            messageContent: this.$store.state.user.name+"转发了"+this.sendMessage.messageContent
        }
        api.transmitTask(this.transmitForm).then(response=>{
          if(response.code === 200){
            // 刷新数据
            this.$message.success("办理成功");
            // 将表单清空
            this.$refs[formName].resetFields();
            // 关闭窗口
            this.visible = false;
            this.transmitVisible = false;
            // 回调事件
            this.$emit("submitCallback")
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
      let assigneeNames = data.map((item) => {
        return item.nickName;
      });
      this.addMultiForm = {
        taskId: this.taskId,
        assignees: assignees,
        assigneeNames: assigneeNames,
        nickNames: assigneeNames.join(",")
      }
      this.$forceUpdate()
      this.$refs.addMultiUserRef.visible = false
    },
    //加签
    addMultiSubmit(){
      api.addMultiInstanceExecution(this.addMultiForm).then(response => {
        if(response.code === 200){
          // 刷新数据
            this.$message.success("办理成功");
            // 将表单清空
            this.addMultiForm = {}
            // 关闭窗口
            this.visible = false;
            this.addMultiVisible = false;
            // 回调事件
            this.$emit("submitCallback")
        }
      })
    },
     //打开减签窗口
    deleteMultiClick(){
       this.deleteMultiVisible = true
    },
    //减签复选框
    handleSelectionMultiList(val){
      let executionIds = val.map((item) => {
        return item.executionId;
      });
      let taskIds = val.map((item) => {
        return item.id;
      });
      let assigneeIds = val.map((item) => {
        return item.assigneeId;
      });
      let assigneeNames = val.map((item) => {
        return item.assignee;
      });
      this.deleteMultiForm = {
        taskId: this.taskId,
        taskIds: taskIds,
        executionIds: executionIds,
        assigneeIds: assigneeIds,
        assigneeNames: assigneeNames
      }
    },
    //减签
    deleteMultiSubmit(){
      api.deleteMultiInstanceExecution(this.deleteMultiForm).then(response => {
        if(response.code === 200){
          // 刷新数据
            this.$message.success("办理成功");
            // 关闭窗口
            this.visible = false;
            this.deleteMultiVisible = false;
            // 回调事件
            this.$emit("submitCallback")
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
        this.$emit("submitCallback")
    },
    //重置表单
    reset(){
        this.isCopy = false
        this.delegate = '2'
        this.transmitForm = {}
        this.formData.message = null
        this.formData.assigneeMap = {}
        this.nickName = {}
    }
  }
};
</script>
