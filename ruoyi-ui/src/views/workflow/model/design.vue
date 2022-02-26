
<template>
  <el-dialog title="Activiti工作流在线流程设计器" :visible.sync="visible" :before-close="handleClose" fullscreen >
    <div class="modeler">
        <iframe class="iframe" :src="src"></iframe>
    </div>
  </el-dialog>
</template>

<script>
export default {
    props: {
      modelId: String,
    },

    data() {
      return {
        visible: false,
      }
    },

    computed:{
        src(){
          return "/modeler.html?modelId="+this.modelId
        },
        apiUrl(){
          return  process.env.VUE_APP_BASE_API // "/dev-api";//后台部署的api服务
        }
    },

    mounted(){
      //全局存入当前vue实例，供activiti调用
      window.getMyVue = this;
    },

    methods:{
      goto(){
        this.$message.success("保存模型成功");
        this.$router.push({name:"/workflow/model"})
      },

      handleClose(done) {
        this.$confirm('请记得点击左上角保存按钮，确定关闭设计窗口?', '确认关闭',{
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
            // 关闭
            done();
            // 刷新数据
            this.$parent.getList()
        }).catch(() => {})
      }
    }
  }
</script>
<style scoped>
.iframe {
  width: 100%;
  height: calc(100vh - 120px);
  border: 0px;
}
</style>
