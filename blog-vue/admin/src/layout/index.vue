<template>
  <el-container>
    <!-- 侧边栏 -->
    <el-aside width="auto">
      <SideBar />
    </el-aside>
    <el-container :class="'main-container ' + isHide">
      <!-- 导航栏 -->
      <el-header height="84px" style="padding:0">
        <NavBar />
      </el-header>
      <!-- 内容 -->
      <el-main style="background:#F0F2F5">
        <router-view :key="$route.fullPath" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import NavBar from "./components/NavBar";
import SideBar from "./components/SideBar";
export default {
  components: {
    NavBar,
    SideBar
  },
  computed: {
    isHide() {
      return this.$store.state.collapse ? "hideSideBar" : "";
    }
  }
};
</script>

<style scoped>
.main-container {
  transition: margin-left 0.45s;
  margin-left: 210px;
  min-height: 100vh;
}
.hideSideBar {
  margin-left: 64px;
}
</style>
