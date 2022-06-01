<template>
  <el-dialog title="设置" :visible.sync="visible" v-if="visible" width="70%" :close-on-click-modal="false" append-to-body>
    <div class="container" v-loading="loading">
       <el-tabs :tab-position="tabPosition" v-model="activeName" @tab-click="changeSteps">
        <el-tab-pane v-for="(node, index) in nodeList" :key="index" :name="node.id" :label="node.nodeName">
          <el-form ref="form" label-position="left" :model="form">
          <el-form-item label="环节名称">
            <el-tag v-if="nodeName">{{nodeName}}</el-tag><el-tag v-else>无</el-tag>
          </el-form-item>
          <el-row>
            <el-col :span="24"><el-alert title="每个节点设置，如有修改都请保存一次，跳转节点后数据不会自动保存！" type="warning" show-icon :closable="false"/></el-col>
          </el-row>
          <el-form-item prop="chooseWay" label="选人方式">
            <el-radio-group @change="clearSelect" v-model="form.chooseWay">
              <el-radio border label="person">选择人员</el-radio>
              <el-radio border label="role">选择角色</el-radio>
              <el-radio border label="dept">选择部门</el-radio>
              <el-radio border label="rule">业务规则</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-row>
            <el-col class="line" :span="6">
              <el-form-item label="是否弹窗选人" prop="isShow">
                <el-switch :disabled = "isShowDisabled" v-model="form.isShow"></el-switch>
              </el-form-item>
            </el-col>
            <el-col class="line" :span="6">
              <el-form-item label="是否会签" prop="multiple">
                <el-switch v-model="form.multiple"></el-switch>
              </el-form-item>
           </el-col>
           <el-col class="line" :span="6">
              <el-form-item label="是否可退回" prop="isBack">
                <el-switch v-model="form.isBack"></el-switch>
              </el-form-item>
           </el-col>
          </el-row>
          <el-row v-if="form.multiple">
            <el-col :span="20">
              <el-form-item label-width="100px" label="会签KEY值" prop="multipleColumn">
                <el-input @input="onInput()" v-model="form.multipleColumn" placeholder="会签保存人员KEY值"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="20">
              <el-form-item label-width="100px" label="审批人员" prop="assignee">
                <el-input readonly v-model="form.assignee" placeholder="审批人员">
                  <el-button type="primary" slot="append" @click="openSelect">选择人员</el-button>
                  <el-button type="success" slot="append" @click="clearSelect">清空</el-button>
                </el-input>
                <el-input v-model="form.assigneeId" v-show="false" placeholder="审批人员ID"/>
              </el-form-item>
           </el-col>
          </el-row>
          <el-form-item>
            <div style="float:right;">
              <el-button type="primary" size="small" @click="onSubmit">保存</el-button>
              <el-button type="danger" size="small" @click="del">重置</el-button>
            </div>
          </el-form-item>
        </el-form>
        </el-tab-pane>
      </el-tabs>
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
import {getInfo,add,edit,del} from "@/api/workflow/actNodeAssginee";

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
        isShowDisabled: false,
        form: {
          isShow: true,
          isBack: false,
          multiple: false,
          chooseWay: undefined
        },
        definitionId: null,
        assignee: null,
        nodeName: null,
        // 人员选择
        propUserList: [],
        // 角色选择
        propRoleList: [],
        // 部门选择
        propDeptList: [],
      }
    },
    methods: {
        onInput(){
           this.$forceUpdate();
        },
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
          //this.$refs['form'].clearValidate();
          this.nodeName = this.nodeList[this.activeName].nodeName
          getInfo(this.definitionId,this.nodeList[this.activeName].nodeId).then(response => {
            if(response.data){
              this.form = response.data
              this.form.nodeName = response.data.nodeName
              this.loading = false
              this.$forceUpdate()
            }else{
              this.form.id = undefined
              this.form.nodeId = this.nodeList[this.activeName].nodeId
              this.form.nodeName = this.nodeList[this.activeName].nodeName
              this.reset()
              this.loading = false
            }
          })
        },
        //保存设置
        onSubmit(){
          if(this.nodeName){
            if(this.form.chooseWay !== 'rule'){
                this.form.fullClassId = undefined
            }
            if(this.form.chooseWay === null || this.form.chooseWay === ''||this.form.chooseWay === undefined){
                this.$modal.msgError("请选择选人方式")
                return false
            }
            if(this.form.isBack === null || this.form.isBack === ''||this.form.isBack === undefined){
                this.form.isBack = false
            }

            if(this.form.id){
              del(this.form.id)
              this.form.id = undefined
              edit(this.form).then(response => {
                this.form = response.data
                this.$modal.msgSuccess("保存成功")
              })
            }else{
              add(this.form).then(response => {
                this.form = response.data
                this.$modal.msgSuccess("保存成功")
              })
            }
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
        clearSelect(){
          this.form.assigneeId = ""
          this.form.assignee = ""
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



