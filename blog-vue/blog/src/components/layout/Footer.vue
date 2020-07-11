<template>
  <v-footer app padless absolute style="background:rgba(0,0,0,0)">
    <v-col class="text-center footer" cols="12">
      <div>Copyright &copy; 2019-{{ new Date().getFullYear() }}</div>
      <a href="http://www.beian.miit.gov.cn" target="_blank">
        湘ICP备19021924号</a
      >
    </v-col>
  </v-footer>
</template>

<style scoped>
.footer {
  font-size: 0.75rem;
}
.footer div {
  line-height: 1.75;
}
</style>
