<template>
<div >
  <el-badge :value="messageTotle" :max="99">
    <el-popover
      placement="right"
      width="400"
      trigger="click">
      <div class="msg">
        <el-card class="box-card">
          <div class="read">
            <div><i class="el-icon-message-solid icon"></i></div>
            <div><el-link :underline="false" @click="batchReadMessage()">知道了</el-link></div>
          </div>
        </el-card>
        <el-card class="box-card" style="cursor: pointer;" v-for="(data,index) in messageList" :key="data.id" >
          <div class="title">
            <div>{{data.title}}</div>
            <div>{{ parseTime(data.createTime) }}</div>
          </div>
          <span style="cursor: pointer;" v-if="data.status === 0" @click="readMessage(data,index)">
            <i class="el-icon-message icon"></i>{{data.messageContent}}
          </span>
          <span v-else-if="data.status === 1" style="color: #ccc;">
            <i class="el-icon-message icon"></i>{{data.messageContent}}
          </span>
        </el-card>
        <el-card class="box-card" style="cursor: pointer;">
          <center @click="clickMessage()">查看更多消息<i class="el-icon-d-arrow-right"></i></center>
        </el-card>
      </div>
      <svg-icon slot="reference" icon-class="message"/>
    </el-popover>
  </el-badge>
</div>
</template>

<script>
import { readMessage, batchReadMessage} from "@/api/workflow/message";
import store from '@/store'
export default {
    name: 'RuoYiMsg',
    data() {
      return {

      }
      },
      computed: {
        messageList() {
          if (store.state.message.list !== null && typeof store.state.message.list !== 'undefined') {
            return store.state.message.list;
          } else {
            return [];
          }
        },
        messageTotle() {
          if (store.state.message.total !== null && typeof store.state.message.total !== 'undefined') {
            return store.state.message.total;
          } else {
            return 0;
          }
        }
      },
      created() {},
      methods: {
        //查看更多消息
        clickMessage(){
          this.$router.push("/workflow/message");
        },
        //阅读通知
        readMessage(data,index){
          readMessage(data.id).then(response=>{
            if(response.code===200 && data.status === 0){
              store.state.message.list[index].status = 1
              store.state.message.total = store.state.message.total-1
            }
          })
        },
        //批量阅读通知
        batchReadMessage(){
          batchReadMessage()
          store.state.message.total = 0
          store.state.message.list = this.messageList.forEach(e=>{
            e.status = 1
          })
        }
      }
}
</script>
<style scoped>
 .msg{
  font-size: 10px;
 }
 .icon{
  font-size:20px;
  vertical-align: middle;
  padding-right: 10px;
 }
 .read{
  display: flex;
  justify-content: space-between;
 }
 .title{
  display: flex;
  justify-content: space-between;
  padding: 0px 0 10px 0;
  font-size: 13px;
  font-weight: 900;
 }
</style>
