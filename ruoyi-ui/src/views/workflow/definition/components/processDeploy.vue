<template>
  <el-dialog title="部署流程文件" :visible.sync="visible" width="400px">
      <el-upload  accept="application/zip,application/xml,.bpmn"
          :http-request="deployProcessFile" drag action="" :show-file-list="false">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text"><em>点击上传，选择BPMN流程文件</em></div>
            <div class="el-upload__text">仅支持 .zip、.bpmn20.xml、bpmn 格式文件</div>
      </el-upload>
  </el-dialog>
</template>
<script>
import {deployProcessFile} from "@/api/workflow/definition";
export default {
    data() {
        return {
            visible: false, // 弹出窗口，true弹出
            loading: false,
        }
    },
    methods: {
        // 部署流程文件
        async deployProcessFile(file) {
            // 封装上传文件的表单对象
          const data = new FormData()
          data.append('file', file.file)
          await deployProcessFile(data)
          this.$modal.msgSuccess("部署成功");
          this.$parent.getList()
          this.visible = false

        }
    }
}
</script>
