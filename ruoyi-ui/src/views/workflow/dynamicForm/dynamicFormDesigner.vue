<template>
  <div>
    <form-designer ref="formDesigner" v-model="designerForm"/>
  </div>
</template>

<script>
import { getDynamicForm } from "@/api/workflow/dynamicForm";
export default {
  name: "dynamicFormDesigner",
  data() {
    return {
      designerForm: ''
     }
  },
  mounted() {
    getDynamicForm(this.$route.params.id).then(response => {
      this.designerForm = response.data.formDesignerText;
    });
  },
  methods: {
  }
}
</script>

<style>

</style>