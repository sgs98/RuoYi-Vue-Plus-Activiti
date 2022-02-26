<template>
<el-dialog title="用户" :visible.sync="visible" v-if="visible" width="60%" append-to-body v-dialogDrag :close-on-click-modal="false">
  <div class="app-container">
    <el-input placeholder="输入关键字进行过滤" v-model="filterText"></el-input>
    <el-tree
    :data="deptList"
    show-checkbox
    node-key="deptId"
    accordion
    ref="tree"
    default-expand-all
    :filter-node-method = "filterNode"
    :props="defaultProps"
    :check-strictly = 'isLinked'
    >
  </el-tree>
  </div>
  <div slot="footer" class="dialog-footer">
        <el-button size="small" type="primary" @click="primary">确认</el-button>
        <el-button size="small" @click="visible=false">取 消</el-button>
  </div>
</el-dialog>
</template>

<script>
import { listDept } from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "Dept",
  dicts: ['sys_normal_disable'],
  components: { Treeselect },
  props: {
    // 回显的数据
    propDeptList: {
      type: Array,
      default:()=>[]
    },
    // 是否与子级联动 false为联动
    isLinked: {
      type: Boolean,
      default:false
    }
  },
  data() {
    return {
      defaultProps: {
          children: 'children',
          label: 'deptName'
      },
      // 遮罩层
      loading: true,
      visible: false,
      // 过滤字段
      filterText: null,
      // 表格树数据
      deptList: [],
      //回显id
      deptIds: []
    };
  },

  watch: {
     propDeptList(val) {
       if(val.length>0){
          this.getList()
          val.forEach(row => {
            this.deptIds.push(row.deptId)
          })
       }else{
          this.deptIds = []
          this.getList()
       }
     },
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  methods: {
    //过滤
    filterNode(value, data) {
        if (!value) return true;
        return data.deptName.indexOf(value) !== -1;
    },
    /** 查询部门列表 */
    getList() {
      this.loading = true;
      listDept().then(response => {
        this.deptList = this.handleTree(response.data, "deptId");
        this.loading = false;
        this.$nextTick(() => {
          if(this.deptIds.length > 0){
            this.$refs.tree.setCheckedKeys(this.deptIds);
          }
        })
      });
    },
    // 确认
    primary(){
      let deptList = this.$refs.tree.getCheckedNodes();
      if(deptList.length>0){
        this.$emit("primary",deptList)
      }else{
        this.$modal.msgWarning("请选择部门！");
      }
    }
  }
};
</script>
