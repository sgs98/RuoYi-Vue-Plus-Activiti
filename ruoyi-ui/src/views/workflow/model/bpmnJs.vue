<template>
  <div id="bpmn">
    <my-process-palette />
    <my-process-designer
      :style="{ height: `${getScreenHeight}px` }"
      :key="`designer-${reloadIndex}`"
      :options="{
        taskResizingEnabled: true,
        eventResizingEnabled: true
      }"
      v-model="xmlString"
      v-bind="controlForm"
      keyboard
      ref="processDesigner"
      @element-click="elementClick"
      @element-contextmenu="elementContextmenu"
      @init-finished="initModeler"
      @saveBpmnMode="saveMode"
    />
    <my-properties-panel :key="`penal-${reloadIndex}`" :model="model" :bpmn-modeler="modeler" :prefix="controlForm.prefix" class="process-panel" />
  </div>
</template>

<script>
import translations from "@/components/Bpmn/translations";
// 自定义渲染（隐藏了 label 标签）
import CustomRenderer from "@/components/Bpmn/modules/custom-renderer";
// 自定义元素选中时的弹出菜单（修改 默认任务 为 用户任务）
import CustomContentPadProvider from "@/components/Bpmn/package/designer/plugins/content-pad";
// 自定义左侧菜单（修改 默认任务 为 用户任务）
import CustomPaletteProvider from "@/components/Bpmn/package/designer/plugins/palette";
import Log from "@/components/Bpmn/package/Log";
// 任务resize
import resizeTask from "bpmn-js-task-resize/lib";
// bpmn theme plugin
import sketchyRendererModule from "bpmn-js-sketchy";
// 小地图
import minimapModule from "diagram-js-minimap";

// clickoutside
import clickoutside from "element-ui/lib/utils/clickoutside";

import {getEditorXml,saveModelXml,add} from "@/api/workflow/model";
export default {
  name: "App",
  directives: {
    clickoutside: clickoutside
  },
  props: {
      modelId: String,
  },
  data() {
    return {
      xmlString: "",
      modeler: null,
      reloadIndex: 0,
      controlDrawerVisible: false,
      infoTipVisible: false,
      pageMode: false,
      translationsSelf: translations,
      controlForm: {
        processId: "",
        processName: "",
        simulation: true,
        labelEditing: false,
        labelVisible: true,
        prefix: "activiti",
        headerButtonSize: "mini",
        events: ["element.click", "element.contextmenu"],
        // additionalModel: []
        additionalModel: [CustomContentPadProvider, CustomPaletteProvider, minimapModule]
      },
      addis: {
        CustomContentPadProvider,
        CustomPaletteProvider
      },
      screenHeight: document.body.clientHeight,
      model: {}
    };
  },
  created() {
    if(this.modelId !== 'new'){
        this.getModelEditorXml()
    }
    window.onresize = () => {
      //获取body的高度
      this.screenHeight = document.body.clientHeight
    }
  },
  computed: {
    getScreenHeight() {
      return this.screenHeight - 50
    }
  },
  methods: {
    //读取xml
    getModelEditorXml(){
      getEditorXml(this.modelId).then(response=>{

        this.model = response.data.model
        //if(response.data.xml!==null||response.data.xml!==''){
           // this.$refs.processDesigner.model = response.data.model
            this.xmlString = response.data.xml
            this.$refs.processDesigner.createNewDiagram(this.xmlString)
       // }

      })
    },
    saveMode(xml,svg){
      this.$modal.confirm('是否确认保存模型？').then(() => {
           if(this.modelId === 'new'){
              let params = {xml: xml,svg: svg}
              add(params).then(response=>{
                if(response.code === 200){
                  this.$modal.msgSuccess("保存成功")
                  this.$emit("close-bpmn")
                }
              })
            }else{
              let params = {modelId: this.modelId,xml: xml,svg: svg}
              saveModelXml(params).then(response=>{
                if(response.code === 200){
                  this.$modal.msgSuccess("保存成功")
                  this.$emit("close-bpmn")
                }
              })
            }
         })
    },
    initModeler(modeler) {
      setTimeout(() => {
        this.modeler = modeler;
        const canvas = modeler.get("canvas");
        const rootElement = canvas.getRootElement();
        Log.prettyPrimary("Process Id:", rootElement.id);
        Log.prettyPrimary("Process Name:", rootElement.businessObject.name);
      }, 10);
    },
    reloadProcessDesigner(notDeep) {
      this.controlForm.additionalModel = [];
      for (let key in this.addis) {
        if (this.addis[key]) {
          this.controlForm.additionalModel.push(this.addis[key]);
        }
      }
      !notDeep && (this.xmlString = undefined);
      this.reloadIndex += 1;
      this.modeler = null; // 避免 panel 异常
    },
    changeLabelEditingStatus(status) {
      this.addis.labelEditing = status ? { labelEditingProvider: ["value", ""] } : false;
      this.reloadProcessDesigner();
    },
    changeLabelVisibleStatus(status) {
      this.addis.customRenderer = status ? CustomRenderer : false;
      this.reloadProcessDesigner();
    },
    elementClick(element) {
      //console.log(element);
      this.element = element;
    },
    elementContextmenu(element) {
      //console.log("elementContextmenu:", element);
    },
    changePageMode(mode) {
      const theme = mode
        ? {
            // dark
            stroke: "#ffffff",
            fill: "#333333"
          }
        : {
            // light
            stroke: "#000000",
            fill: "#ffffff"
          };
      const elements = this.modeler.get("elementRegistry").getAll();
      this.modeler.get("modeling").setColor(elements, theme);
    }
  }
};
</script>

<style lang="scss">
body {
  overflow: hidden;
  margin: 0;
  box-sizing: border-box;
}
#bpmn {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  display: inline-grid;
  grid-template-columns: 100px auto max-content;
}
.demo-info-bar {
  position: fixed;
  right: 8px;
  bottom: 108px;
  z-index: 1;
}
.demo-control-bar {
  position: fixed;
  right: 8px;
  bottom: 48px;
  z-index: 1;
}
.open-model-button {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 32px;
  background: rgba(64, 158, 255, 1);
  color: #ffffff;
  cursor: pointer;
}
.zoom-in-right-enter-active,
.zoom-in-right-leave-active {
  opacity: 1;
  transform: scaleY(1) translateY(-48px);
  transition: all 300ms cubic-bezier(0.23, 1, 0.32, 1);
  transform-origin: right center;
}
.zoom-in-right-enter,
.zoom-in-right-leave-active {
  opacity: 0;
  transform: scaleX(0) translateY(-48px);
}
.info-tip {
  position: absolute;
  width: 480px;
  top: 0;
  right: 64px;
  z-index: 10;
  box-sizing: border-box;
  padding: 0 16px;
  color: #333333;
  background: #f2f6fc;
  transform: translateY(-48px);
  border: 1px solid #ebeef5;
  border-radius: 4px;
  &::before,
  &::after {
    content: "";
    width: 0;
    height: 0;
    border-width: 8px;
    border-style: solid;
    position: absolute;
    right: -15px;
    top: 50%;
  }
  &::before {
    border-color: transparent transparent transparent #f2f6fc;
    z-index: 10;
  }
  &::after {
    right: -16px;
    border-color: transparent transparent transparent #ebeef5;
    z-index: 1;
  }
}
.control-form {
  .el-radio {
    width: 100%;
    line-height: 32px;
  }
}
.element-overlays {
  box-sizing: border-box;
  padding: 8px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 4px;
  color: #fafafa;
}

body,
body * {
  /* 滚动条 */
  &::-webkit-scrollbar-track-piece {
    background-color: #fff; /*滚动条的背景颜色*/
    -webkit-border-radius: 0; /*滚动条的圆角宽度*/
  }
  &::-webkit-scrollbar {
    width: 10px; /*滚动条的宽度*/
    height: 8px; /*滚动条的高度*/
  }
  &::-webkit-scrollbar-thumb:vertical {
    /*垂直滚动条的样式*/
    height: 50px;
    background-color: rgba(153, 153, 153, 0.5);
    -webkit-border-radius: 4px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
  }
  &::-webkit-scrollbar-thumb {
    /*滚动条的hover样式*/
    background-color: rgba(159, 159, 159, 0.3);
    -webkit-border-radius: 4px;
  }
  &::-webkit-scrollbar-thumb:hover {
    /*滚动条的hover样式*/
    background-color: rgba(159, 159, 159, 0.5);
    -webkit-border-radius: 4px;
  }
}
</style>
