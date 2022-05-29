<template>
    <el-dialog title="历史版本" :visible.sync="visible" width="90%" :close-on-click-modal="false" append-to-body>
        <div class="app-container">
            <!-- 表格数据 -->
            <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
                <!-- <el-table-column type="selection" width="55" align="center" /> -->
                <el-table-column align="center" type="index" label="序号" width="50"></el-table-column>
                <el-table-column align="center" prop="name" label="模型名称"  min-width="160"></el-table-column>
                <el-table-column align="center" prop="key" label="标识Key"  min-width="120"></el-table-column>
                <el-table-column align="center" prop="version" label="版本号" width="80" >
                <template slot-scope="{row}"> v{{row.version}}.0</template>
                </el-table-column>
                <el-table-column align="center" prop="resourceName" label="流程XML" min-width="190">
                <template slot-scope="{row}">
                <!-- 注意组件上使用原生事件，要加 .active -->
                <el-link type="primary" @click.native="clickExportXML(row.id)">{{ row.resourceName }}</el-link>
                </template>
                </el-table-column>
                <el-table-column align="center" prop="diagramResourceName" label="流程图片" min-width="240">
                <template slot-scope="{row}">
                <el-link type="primary" @click="clickPreviewImg(row.id)">{{ row.diagramResourceName }}</el-link>
                </template>
                </el-table-column>
                <el-table-column align="center" prop="suspensionState" label="状态"  min-width="50">
                <template slot-scope="scope">
                    <el-tag type="success" v-if="scope.row.suspensionState==1">激活</el-tag>
                    <el-tag type="danger" v-else>挂起</el-tag>
                </template>
                </el-table-column>
                <el-table-column align="center" prop="deploymentTime" label="部署时间" width="160"></el-table-column>
                <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-dropdown>
                  <span class="el-dropdown-link">
                    更多<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item>
                      <el-button
                        v-if="scope.row.suspensionState == 1"
                        @click="clickUpdateProcDefState(scope.row)"
                        type="text"
                        size="mini"
                        icon="el-icon-lock"
                    >挂起</el-button>
                    <el-button
                        v-else type="text"
                        @click="clickUpdateProcDefState(scope.row)"
                        size="mini"
                        icon="el-icon-unlock"
                    >激活</el-button>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <el-button
                          type="text"
                          @click="convertToModel(scope.row)"
                          size="mini"
                          icon="el-icon-sort"
                      >转换模型</el-button>
                    </el-dropdown-item>
                    <el-dropdown-item>
                      <el-button
                          size="mini"
                          type="text"
                          icon="el-icon-copy-document"
                          @click="copySetting(scope.row)"
                      >复制流程</el-button>
                      </el-dropdown-item>

                    <el-dropdown-item>
                      <el-button
                          size="mini"
                          type="text"
                          icon="el-icon-setting"
                          @click="handleSetting(scope.row)"
                      >设置</el-button>
                    </el-dropdown-item>

                    <el-dropdown-item>
                      <el-button
                          size="mini"
                          type="text"
                          icon="el-icon-delete"
                          @click="handleDelete(scope.row)"
                      >删除</el-button>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                  </el-dropdown>
                </template>
                </el-table-column>
            </el-table>

            <!-- 预览图片 -->
            <process-preview ref="previewRef" :url="url" :type="type"/>
            <!-- 流程设置 -->
            <process-setting ref="settingRef"/>
        </div>
    </el-dialog>
</template>
<script>
import {hisList,del,updateProcDefState} from "@/api/workflow/definition";
import {copy} from "@/api/workflow/actNodeAssginee";
import processDeploy from './processDeploy'
import processPreview from './processPreview'
import processSetting from './processSetting'
import {convertToModel} from "@/api/workflow/model";

export default {
    components: { processDeploy, processPreview, processSetting},
    props:{
      propKey: {
        type: String
      },
      definitionId: {
        type:String
      }
    },
    data() {
        return {
            // 弹出层
            visible: false,
            // 流程图
            url: null,
            // 按钮loading
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
            // 是否显示弹出层
            open: false,
            // 表格数据
            list: [],
            type: '',//png,xml
        }
    },
    methods: {
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length!==1
        this.multiple = !selection.length
      },
      //查询列表
      getList(data){
          this.loading = true;
          hisList(data).then(response => {
            this.list = response.data;
            this.loading = false;
          })
      },
      // 将流程定义转换为模型
      convertToModel(row){
          this.loading = true;
          convertToModel(row.id).then(response => {
            if(response.code === 200){
             this.$modal.msgSuccess("转换成功");
             this.propKey = row.key
             this.definitionId = row.id
             this.$refs.hisListRef.visible = true
             let data = {id:this.definitionId,key:this.propKey}
             this.getList(data);
             this.loading = false;
            }
          }).finally(() => {
             this.loading = false;
          });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
         const deploymentId = row.deploymentId;
         this.$modal.confirm('是否确认删除标识key为"' + row.key + '"的数据项？').then(() => {
           this.loading = true;
           return del(deploymentId);
         }).then(() => {
           this.loading = false;
           let data = {id:this.definitionId,key:this.propKey}
           this.getList(data);
           this.$modal.msgSuccess("删除成功");
         }).finally(() => {
           this.loading = false;
         });
      },
      // 导出xml流程文件
      clickExportXML(id) {
         window.open(process.env.VUE_APP_BASE_API+'/workflow/definition/export/xml/'+id)
      },

      // 预览图片 downFile
      clickPreviewImg(id) {
        this.type = 'png'
        this.url = process.env.VUE_APP_BASE_API+'/workflow/definition/export/png/'+id
        this.$refs.previewRef.visible = true
      },
      //激活或挂起流程
      clickUpdateProcDefState(row){
        let msg='';
        if(row.suspensionState===1){
          msg=`暂停后，此流程下的所有任务都不允许往后流转，您确定暂停【${row.name || row.key}】吗？`
        }else{
          msg=`启动后，此流程下的所有任务都允许往后流转，您确定启动【${row.name || row.key}】吗？`
        }
        this.$modal.confirm(msg).then(() => {
           this.loading = true;
           return updateProcDefState(row.id);
         }).then(() => {
           this.loading = false;
           let data = {id:this.definitionId,key:this.propKey}
           this.getList(data);
           this.$modal.msgSuccess("操作成功");
         }).finally(() => {
           this.loading = false;
         });
      },
      //打开设置
      handleSetting(row){
        this.$nextTick(() => {
          this.$refs.settingRef.visible = true
          this.$refs.settingRef.init(row.id)
        })
      },
      copySetting(row){
        this.$modal.confirm('是否确认将此设置复制为最新？').then(() => {
           this.loading = true;
           copy(row.id,row.key).then(response => {
              if(response.code === 200){
                this.$modal.msgSuccess("操作成功");
              }else{
                this.$modal.msgError(response.msg);
              }
            })
         }).finally(() => {
           this.loading = false;
         });
      }
    }
}
</script>

