<template>
<div >
  <el-badge :value="badgeValue" :max="99">
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
export default {
    name: 'RuoYiMsg',
    data() {
      return {
          badgeValue: 0, // 消息总条数
          messageList:[],
          // websocket连结url
          wsUrl: process.env.VUE_APP_WEBSOCKET_URL+'/'+this.$store.state.user.name,
          // socket参数
          socket: null,
          timeout: 15000, // 15秒一次心跳
          timeoutObj: null, // 心跳心跳倒计时
          serverTimeoutObj: null, // 心跳倒计时
          timeoutnum: null, // 断开 重连倒计时
          lockReconnect: false, // 防止
          websocket: null // websocket实例
        }
      },
      created() {
        // 连接webSocket
        this.initWebSocket();
      },
      methods: {
        //查看更多消息
        clickMessage(){
          this.$router.push("/workflow/message");
        },
        //阅读通知
        readMessage(data,index){
          readMessage(data.id).then(response=>{
            if(response.code===200 && data.status === 0){
              this.messageList[index].status = 1
              this.badgeValue = this.badgeValue-1
            }
          })
        },
        //批量阅读通知
        batchReadMessage(){
          batchReadMessage()
          this.badgeValue = 0
          this.messageList = this.messageList.forEach(e=>{
            e.status = 1
          })
        },
        //初始化
        initWebSocket() {
          window.clearTimeout(this.timeoutObj);
          window.clearTimeout(this.serverTimeoutObj)
          this.createWebSocket();
        },
        // 创建websocket
        createWebSocket() {
          try {
            this.websocket = new WebSocket(this.wsUrl);
            this.initWsEventHandle();
          } catch (e) {
            this.reconnect();
          }
        },
        // websocket消息提醒
        initWsEventHandle() {
          try {
            this.websocket.onopen = this.onWsOpen;
            this.websocket.onerror = this.onWsError;
            this.websocket.onmessage = this.onWsMessage;
            this.websocket.onclose = this.onWsClose;
          } catch (error) {
            this.reconnect()
          }
        },
        onWsOpen(){
          //清除延时器
          this.timeoutObj && clearTimeout(this.timeoutObj);
          this.serverTimeoutObj && clearTimeout(this.serverTimeoutObj);
          this.timeoutObj = setTimeout(() => {
          if (this.websocket && this.websocket.readyState == 1) {
            //发送消息，服务端返回信息
            this.websocket.send('heartBath');
          } else {
            //重连
            this.reconnect();
          }
          //定义一个延时器等待服务器响应，若超时，则关闭连接，重新请求server建立socket连接
          this.serverTimeoutObj = setTimeout(() => {
              this.websocket.close();
            }, this.timeout)
          }, this.timeout)
        },
        onWsMessage(event){
          let data = JSON.parse(event.data)
          if(data.page){
            this.badgeValue = data.page.total
            this.messageList = data.page.rows
          }
        },
        onWsClose(){
          this.websocket.close();
          clearTimeout(this.timeoutObj);
          clearTimeout(this.serverTimeoutObj);
          this.reconnect();
        },
        onWsError(){
          clearTimeout(this.timeoutObj);
          clearTimeout(this.serverTimeoutObj);
          this.reconnect();
        },
        // 重新链接websocket
        reconnect() {
          if (this.lockReconnect) return
          this.lockReconnect = true;
          // 没连接上会一直重连，设置延迟避免请求过多
          this.timeoutnum && clearTimeout(this.timeoutnum);
          this.timeoutnum = setTimeout(() => {
          this.initWebSocket();
              this.lockReconnect = false;
          }, 5000)
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
