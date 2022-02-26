<template>
  <el-dialog title="流程图展示" :visible.sync="visible" align="center" width="70%" append-to-body>
      <el-image :src="url" >
        <div slot="placeholder">流程图加载中 <i class="el-icon-loading"></i></div>
      </el-image>
  </el-dialog>
</template>

<script>
export default {
    props: {
      url: String
    },

    data() {
      return {
        visible: false,
      }
    },
}
</script>
