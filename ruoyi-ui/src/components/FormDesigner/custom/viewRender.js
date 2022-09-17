import { isAttr,jsonClone } from '../utils';
import childrenItem from './slot/index';
import {remoteData} from './mixin';
import Vue from 'vue'
import { getToken } from "@/utils/auth";
import download from "@/plugins/download"

function vModel(self, dataObject) {
  dataObject.props.value=self.value;
  dataObject.on.input = val => {
    self.$emit('input', val)
  }
  //判断是否为上传组件
  if(self.conf.compType === 'upload'){
    // add by nbacheng 2022-09-09
    dataObject.attrs['headers'] = {"Authorization":"Bearer " + getToken()};
    if(dataObject.props.value!==undefined && dataObject.props.value !==''){
      const filevalue = JSON.parse(dataObject.props.value);
      dataObject.props['file-list'] = filevalue;
    }
    dataObject.attrs['on-preview'] = (file) => {
      console.log("on-preview file",file);
      //download(file);
      download.oss(file.ossId);
    }
  } 
}

export default {
  render(h) {
    let dataObject = {
      attrs: {},
      props: {},
      on: {},
      style: {}
    }

    //远程获取数据
    this.getRemoteData();
    const confClone = jsonClone(this.conf);
    const children = childrenItem(h,confClone);
    Object.keys(confClone).forEach(key => {
      const val = confClone[key]
      if (dataObject[key]) {
        dataObject[key] = val
      } else if(key ==='width'){
        dataObject.style= 'width:'+val+'%';
      } else if (!isAttr(key)) {
        dataObject.props[key] = val
      } else {
        dataObject.attrs[key] = val
      }
    })
    /*调整赋值模式，规避cascader组件赋值props会出现覆盖预制参数的bug */
    vModel(this, dataObject);
    return h(confClone.ele, dataObject, children)
  },
  props: ['conf','value'],
  mixins:[remoteData]
}
