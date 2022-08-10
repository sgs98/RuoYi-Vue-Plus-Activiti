<template>
  <div id="app">
    <bpmn-modeler
      ref="refNode"
      :xml="xml"
      :users="users"
      :groups="groups"
      :categorys="categorys"
      :is-view="false"
      @save="saveModeler"
    />
  </div>
</template>

<script>
import bpmnModeler from '@/components/Bpmn/package/index'
import {getEditorXml,saveModelXml,add} from "@/api/workflow/model";
export default {
  dicts: ['act_category'],
  components: {
    bpmnModeler
  },
  props: {
      modelId: String,
      categorysBpmn: Array
  },
  data() {
    return {
      xml: '', // 后端查询到的xml
      categorys: [],
      isView: false,
      users: [
        // { name: '${assignee}', id: '${assignee}' },
        // { name: '李四', id: 'lisi' },
        // { name: '王五', id: 'wangwu' }
      ],
      groups: [
        { name: 'web组', id: 'web' },
        { name: 'java组', id: 'java' },
        { name: 'python组', id: 'python' }
      ]
    }
  },
  mounted() {
    this.getModelDetail()
  },
  methods: {
    getModelDetail() {
      this.categorysBpmn.forEach(element => {
        let param = {
          id:element.value,
          name:element.label
        }
        this.categorys.push(param)
      });
      if(this.modelId!=='new'){
        getEditorXml(this.modelId).then(response => {
           this.xml = response.data.xml
        })
      }
    },
    saveModeler(data){
      this.$modal.confirm('是否确认保存模型？').then(() => {
           if(this.modelId === 'new'){
              let params = {xml: data.xml,svg: data.svg,process:data.process}
              add(params).then(response=>{
                if(response.code === 200){
                  this.$modal.msgSuccess("保存成功")
                  this.$emit("close-bpmn")
                }
              })
            }else{
              let params = {modelId: this.modelId,xml: data.xml,svg: data.svg,process:data.process}
              saveModelXml(params).then(response=>{
                if(response.code === 200){
                  this.$modal.msgSuccess("保存成功")
                  this.$emit("close-bpmn")
                }
              })
            }
         })
    },
  }
}
</script>

<style lang="scss">
html, body, #app {
  // height: 650px;
  margin: 0;
}
</style>
