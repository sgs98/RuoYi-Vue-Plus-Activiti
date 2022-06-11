<template>
  <el-dialog title="设置" :visible.sync="visible" v-if="visible" width="70%" :close-on-click-modal="false" append-to-body>
    <div class="container" v-loading="loading">
       <el-tabs :tab-position="tabPosition" class="tabs" v-model="activeName" @tab-click="changeSteps">
        <el-tab-pane v-for="(node, index) in nodeList" :key="index" :name="node.id" :label="node.nodeName">
          <el-form style="height:inherit" ref="form" size="small" label-position="left" :model="form">
            <el-form-item label="环节名称">
              <el-tag v-if="nodeName">{{nodeName}}</el-tag><el-tag v-else>无</el-tag>
            </el-form-item>
            <el-row>
              <el-form-item>
                  <el-col :span="24" style="line-height: 20px">
                    <el-alert title="每个节点设置，如有修改都请保存一次，跳转节点后数据不会自动保存！" type="warning" show-icon :closable="false"/>
                  </el-col>
              </el-form-item>
            </el-row>
            <el-form-item v-if="node.index === 1" prop="chooseWay" label="选人方式">
              <el-radio-group @change="clearSelect(form.chooseWay)" v-model="form.chooseWay">
                <el-radio border label="person">选择人员</el-radio>
                <el-radio border label="role">选择角色</el-radio>
                <el-radio border label="dept">选择部门</el-radio>
                <el-radio border label="rule">业务规则</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-row v-if="node.index === 1">
              <el-col class="line" :span="8">
                <el-form-item label="是否弹窗选人" prop="isShow">
                  <el-switch v-model="form.isShow"></el-switch>
                </el-form-item>
              </el-col>
              <el-col class="line" :span="8">
                <el-form-item label="是否能会签" prop="multiple">
                  <el-switch disabled v-model="form.multiple"></el-switch>
                </el-form-item>
              </el-col>
              <el-col class="line" :span="8">
                  <el-form-item label="是否能退回" prop="isBack">
                    <el-switch v-model="form.isBack"></el-switch>
                  </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col class="line" :span="8">
                <el-form-item label="是否可以委托" prop="isDelegate">
                  <el-switch v-model="form.isDelegate"></el-switch>
                </el-form-item>
              </el-col>
              <el-col class="line" :span="8">
                <el-form-item label="是否能转办" prop="isTransmit">
                  <el-switch v-model="form.isTransmit"></el-switch>
                </el-form-item>
              </el-col>
              <el-col class="line" :span="8">
                  <el-form-item label="是否能抄送" prop="isCopy">
                    <el-switch v-model="form.isCopy"></el-switch>
                  </el-form-item>
              </el-col>
            </el-row>
            <el-row v-if="form.multiple">
              <el-col class="line" :span="8">
                <el-form-item label-width="100px" label="会签集合" prop="multipleColumn">
                  <el-tag>{{form.multipleColumn}}</el-tag>
                </el-form-item>
              </el-col>
              <el-col class="line" :span="8">
                <el-form-item label="是否能加签" prop="addMultiInstance">
                  <el-switch v-model="form.addMultiInstance"></el-switch>
                </el-form-item>
              </el-col>
              <el-col class="line" :span="8">
                  <el-form-item label="是否能减签" prop="deleteMultiInstance">
                    <el-switch v-model="form.deleteMultiInstance"></el-switch>
                  </el-form-item>
              </el-col>
            </el-row>
            <el-row v-if="node.index === 1">
              <el-col :span="20">
                <el-form-item label-width="100px" label="审批人员" prop="assignee">
                  <el-input readonly v-model="form.assignee" placeholder="审批人员">
                    <el-button type="primary" slot="append" @click="openSelect" v-text="btnText"></el-button>
                    <el-button type="success" slot="append" @click="clearSelect">清空</el-button>
                  </el-input>
                  <el-input v-model="form.assigneeId" v-show="false" placeholder="审批人员ID"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <div style="float:right;position:relative;bottom:20px;">
        <el-button type="primary" size="small" @click="onSubmit">保存</el-button>
        <el-button type="danger" size="small" v-if="index === 1" @click="del">重置</el-button>
      </div>
    </div>
    <!-- 选择人员 -->
    <sys-user ref="userRef" @confirmUser="clickUser" :propUserList = 'propUserList'/>
    <!-- 选择角色 -->
    <sys-role ref="roleRef" @confirmUser="clickRole" :propRoleList = 'propRoleList'/>
    <!-- 选择部门 -->
    <sys-dept ref="deptRef" @confirmUser="clickDept" :propDeptList = 'propDeptList'/>
    <!-- 选择业务规则 -->
    <process-Rule ref="processRuleRef" @primary="clickRule" :propDeptList = 'propDeptList'/>
  </el-dialog>
</template>

<script>
import  SysUser from "@/views/components/user/sys-user";
import  SysRole from "@/views/components/role/sys-role";
import  SysDept from "@/views/components/dept/sys-dept";
import  ProcessRule from "@/views/workflow/definition/components/processRule";
import {setting} from "@/api/workflow/definition";
import {getInfoSetting,add,del} from "@/api/workflow/actNodeAssginee";

export default {
    components: {
       SysUser,
       SysRole,
       SysDept,
       ProcessRule
    },
    data() {
      return {
        tabPosition: 'left',
        activeName:'',
        loading: false,
        nodeList: [],
        visible: false,
        active: null,
        form: {
          isShow: true,
          isBack: false,
          multiple: false,
          chooseWay: undefined,
          isDelegate: false,
          isTransmit: false,
          isCopy: false,
          addMultiInstance: false,
          deleteMultiInstance: false
        },
        // 按钮值
        btnText:"选择人员",
        // 流程定义id
        definitionId: null,
        // 环节名称
        nodeName: null,
        // 下标
        index: 0,
        // 人员选择
        propUserList: [],
        // 角色选择
        propRoleList: [],
        // 部门选择
        propDeptList: [],
      }
    },
    methods: {
        // 查询流程节点
        async init(definitionId) {
           this.loading = true
           this.definitionId = definitionId
           this.form.chooseWay = 'person'
           const data = await setting(definitionId)
           this.nodeList = data.data
           this.activeName = '0'
           this.changeSteps()
        },
        //切换节点
        changeSteps() {
          this.nodeList[this.activeName]
          this.form.assignee = undefined
          this.form.multipleColumn = undefined
          this.form.multiple = false
          this.loading = true
          this.nodeName = this.nodeList[this.activeName].nodeName
          this.index = this.nodeList[this.activeName].index
          getInfoSetting(this.definitionId,this.nodeList[this.activeName].nodeId).then(response => {
            if(response.code === 200){
              this.form = response.data
              this.form.nodeName = response.data.nodeName
              this.loading = false
              if(this.form.id === undefined){
                this.form.isBack = false
              }
              if(this.form.chooseWay === "person"){
                this.btnText = "选择人员"
              }else if(this.form.chooseWay === "role"){
                this.btnText = "选择角色"
              }else if(this.form.chooseWay === "dept"){
                this.btnText = "选择部门"
              }else if(this.form.chooseWay === "rule"){
                this.btnText = "选择规则"
              }
              this.$forceUpdate()
            }
          })
        },
        //保存设置
        onSubmit(){
          if(this.nodeName){
            this.form.nodeName = this.nodeName
            this.form.index = this.index
            add(this.form).then(response => {
              this.form = response.data
              this.$modal.msgSuccess("保存成功")
            })
          }else{
            this.$modal.msgError("请选择节点")
          }
        },
        // 删除
        del(){
          if(this.form.id){
             del(this.form.id).then(response => {
              if(response.code === 200){
                this.$modal.msgSuccess("重置成功")
                this.reset()
              }
             })
          }else{
              this.$modal.msgSuccess("重置成功")
              this.reset()
          }
        },
        // 重置
        reset(){
          this.form.assigneeId =undefined
          this.form.assignee = undefined
          this.form.chooseWay = 'person'
          this.form.processDefinitionId = this.definitionId
        },
        //清空选择的人员
        clearSelect(chooseWay){
          this.form.assigneeId = ""
          this.form.assignee = ""
          if(chooseWay === "person"){
            this.btnText = "选择人员"
          }else if(chooseWay === "role"){
            this.btnText = "选择角色"
          }else if(chooseWay === "dept"){
            this.btnText = "选择部门"
          }else if(chooseWay === "rule"){
            this.btnText = "选择规则"
          }
        },
        //选择弹出层
        async openSelect(){
          if(this.form.chooseWay === 'person'){
            this.propUserList = [];
            if(this.form.assigneeId){
              let userIds = this.form.assigneeId.split( ',' )
              if(userIds.length>0){
                this.propUserList = userIds
              }
            }
            this.$refs.userRef.visible = true
          }else if(this.form.chooseWay === 'role'){
           this.propRoleList = [];
            if(this.form.assigneeId){
              let roleIds = this.form.assigneeId.split( ',' )
              if(roleIds.length>0){
                this.propRoleList = roleIds
              }
            }
            this.$refs.roleRef.visible = true
          }else if(this.form.chooseWay === 'dept'){
            this.propDeptList = [];
            if(this.form.assigneeId){
              let deptIds = this.form.assigneeId.split( ',' )
              if(deptIds.length>0){
                this.propDeptList = deptIds
              }
            }
            this.$refs.deptRef.visible = true
          }else if(this.form.chooseWay === 'rule'){
            this.$refs.processRuleRef.visible = true
          }
        },
        //选择人员
        clickUser(userList){
          let arrAssignee= userList.map(item => {
            return item.nickName
          })
          let arrAssigneeId= userList.map(item => {
            return item.userId
          })
          let resultAssignee = arrAssignee.join(",")
          let resultAssigneeId = arrAssigneeId.join(",")
          this.$set(this.form,'assignee',resultAssignee)
          this.$set(this.form,'assigneeId',resultAssigneeId)
          this.$refs.userRef.visible = false
          this.$forceUpdate()
        },
        //选择角色
        clickRole(roleList){
          let arrAssignee= roleList.map(item => {
            return item.roleName
          })
          let arrAssigneeId= roleList.map(item => {
            return item.roleId
          })
          let resultAssignee = arrAssignee.join(",")
          let resultAssigneeId = arrAssigneeId.join(",")
          this.$set(this.form,'assignee',resultAssignee)
          this.$set(this.form,'assigneeId',resultAssigneeId)
          this.$refs.roleRef.visible = false
        },
        //选择部门
        clickDept(deptList){
          let arrAssignee= deptList.map(item => {
            return item.deptName
          })
          let arrAssigneeId= deptList.map(item => {
            return item.deptId
          })
          let resultAssignee = arrAssignee.join(",")
          let resultAssigneeId = arrAssigneeId.join(",")
          this.$set(this.form,'assignee',resultAssignee)
          this.$set(this.form,'assigneeId',resultAssigneeId)
          this.$refs.deptRef.visible = false
        },
        //业务规则
        clickRule(rule){
          this.$set(this.form,'assignee',rule.fullClass+"."+rule.method)
          this.$set(this.form,'fullClassId',rule.id)
          this.$refs.processRuleRef.visible = false
        }
    }
}
</script>
<style scoped>
    .container {
        height: 550px;
    }
    .tabs{
        height: 550px;
    }
</style>



