<template>
    <el-image v-if="processInstanceId" :src="url" style="font-size: 20px; margin: 50px;">
        <div slot="placeholder"><i class="el-icon-loading"></i> 流程审批历史图加载中……</div>
    </el-image>
</template>

<script>
export default {
    props: {
      processInstanceId: String,
      editMessage: {
        type: Boolean,
        default: false
      },
    },
    data() {
      return {
        url: null
      }
    },
    watch: {
      processInstanceId: {
        handler(newVal,oldVal){
          if(newVal) {
            // 通过流程实例id获取历史流程图
            this.url = process.env.VUE_APP_BASE_API+'/workflow/processInstance/getHistoryProcessImage?processInstanceId='+newVal
          }
        },
        immediate: true,
        deep:true
      }
    }

}
</script>
