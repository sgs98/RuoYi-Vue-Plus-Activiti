<template>
  <div>
    <i-frame :src="url" />
  </div>
</template>
<script>
import iFrame from "@/components/iFrame/index";
import {getToken} from "@/utils/auth";
export default {
  name: "JimuReport",
  components: { iFrame },
  data() {
    return {
      url: "",
    };
  },
  created() {
    this.url = process.env.VUE_APP_JMREPORT_URL + "/list?token=" + getToken();
  }
};
</script>
