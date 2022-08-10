<template>
  <el-dialog title="预览" :visible.sync="visible" width="70%" append-to-body>
     <div v-if="type==='png'" style="align:center">
        <el-image :src="url[0]" v-if="type==='png'">
          <div slot="placeholder">流程图加载中 <i class="el-icon-loading"></i></div>
        </el-image>
      </div>
      <div class="xml-data" v-if="type==='xml'">
        <div v-for="(xml,index) in url" :key="index">
          <pre class="font">{{xml}}</pre>
       </div>
      </div>
      <span slot="footer" v-if="type==='xml'" class="dialog-footer">
        <el-button class="copyText" :data-clipboard-text="copyText" type="primary" size="mini" @click="copy">复制</el-button>
      </span>
  </el-dialog>
</template>

<script>
import ClipboardJS from 'clipboard'
export default {
    props: {
      url: {
        type: Array,
        default:()=>[]
      },
      type: String,
    },
    data() {
      return {
        visible: false,
        previewType: 'xml',
        width: '450px',
        height: '300px',
        copyText: ''
      }
    },
    methods: {
      copy(){
          const clipboard = new ClipboardJS('.copyText');
          this.copyText =  this.url.join("")
          clipboard.on('success', e => {
            this.$message.success('复制成功');
            clipboard.destroy();
          });
          clipboard.on('error', e => {
            this.$message.error('复制失败');
            clipboard.destroy();
          });
      }
    }
}
</script>
<style>
.xml-data {
  background-color: #2B2B2B;
  border-radius: 5px;
  color: #C6C6C6;
  word-break:break-all;
  overflow-y: scroll;
  overflow-x: hidden;
  box-sizing: border-box;
  padding: 8px 0px;
  height: 500px;
  width: inherit;
  line-height: 1px;
}
.font{
  font-family:'幼圆';
  font-weight:500;
}
/* 修改滚动条样式 */
.xml-data::-webkit-scrollbar {
	width: 4px;
}
.xml-data::-webkit-scrollbar-thumb {
	border-radius: 10px;
}
</style>
