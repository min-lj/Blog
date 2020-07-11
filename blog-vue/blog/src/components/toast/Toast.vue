<template>
  <v-snackbar v-model="show" top :timeout="2000" :color="color">
    <i :class="icon" style="margin-right: 10px;" />{{ message }}
  </v-snackbar>
</template>

<script>
export default {
  data: function() {
    return {
      show: false,
      message: "",
      type: "normal",
      color: "#49b1f5",
      icon: ""
    };
  },
  watch: {
    type(value) {
      if (value == "error") {
        this.color = "#F56C6C";
        this.icon = "iconfont iconcuowu";
      }
      if (value == "success") {
        this.color = "#52C41A";
        this.icon = "iconfont iconchenggong";
      }
    }
  }
};
</script>
