<template>
  <div>
    <!-- banner -->
    <div class="about-banner banner">
      <h1 class="banner-title">关于我</h1>
    </div>
    <!-- 关于我内容 -->
    <v-card class="blog-container">
      <div class="about-content markdown-body" v-html="aboutContent"></div>
    </v-card>
  </div>
</template>

<script>
export default {
  created() {
    this.getAboutContent();
  },
  data: function() {
    return {
      aboutContent: ""
    };
  },
  methods: {
    getAboutContent() {
      this.axios.get("/api/about").then(({ data }) => {
        const MarkdownIt = require("markdown-it");
        const md = new MarkdownIt();
        this.aboutContent = md.render(data.data);
      });
    }
  }
};
</script>

<style scoped>
.about-banner {
  background: url(https://www.static.talkxj.com/8xy.jpg) center center / cover
    no-repeat;
  background-color: #49b1f5;
}
.about-content {
  word-break: break-word;
  font-size: 1rem;
  line-height: 1.8;
}
</style>
