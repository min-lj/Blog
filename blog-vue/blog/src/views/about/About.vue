<template>
  <div>
    <!-- banner -->
    <div class="banner" :style="cover">
      <h1 class="banner-title">关于我</h1>
    </div>
    <!-- 关于我内容 -->
    <v-card class="blog-container">
      <!-- 博主头像 -->
      <div class="my-wrapper">
        <v-avatar size="110">
          <img class="author-avatar" :src="avatar" />
        </v-avatar>
      </div>
      <!-- 介绍 -->
      <div
        ref="about"
        class="about-content markdown-body"
        v-html="aboutContent"
      />
    </v-card>
  </div>
</template>

<script>
import Clipboard from "clipboard";
export default {
  created() {
    this.getAboutContent();
  },
  destroyed() {
    this.clipboard.destroy();
  },
  data: function() {
    return {
      aboutContent: "",
      clipboard: null,
      imgList: []
    };
  },
  methods: {
    getAboutContent() {
      const that = this;
      this.axios.get("/api/about").then(({ data }) => {
        this.markdownToHtml(data);
        this.$nextTick(() => {
          // 添加代码复制功能
          this.clipboard = new Clipboard(".copy-btn");
          this.clipboard.on("success", () => {
            this.$toast({ type: "success", message: "复制成功" });
          });
          // 添加图片预览功能
          const imgList = this.$refs.about.getElementsByTagName("img");
          for (var i = 0; i < imgList.length; i++) {
            this.imgList.push(imgList[i].src);
            imgList[i].addEventListener("click", function(e) {
              that.previewImg(e.target.currentSrc);
            });
          }
        });
      });
    },
    markdownToHtml(data) {
      const MarkdownIt = require("markdown-it");
      const hljs = require("highlight.js");
      const md = new MarkdownIt({
        html: true,
        linkify: true,
        typographer: true,
        highlight: function(str, lang) {
          // 当前时间加随机数生成唯一的id标识
          var d = new Date().getTime();
          if (
            window.performance &&
            typeof window.performance.now === "function"
          ) {
            d += performance.now();
          }
          const codeIndex = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
            /[xy]/g,
            function(c) {
              var r = (d + Math.random() * 16) % 16 | 0;
              d = Math.floor(d / 16);
              return (c == "x" ? r : (r & 0x3) | 0x8).toString(16);
            }
          );
          // 复制功能主要使用的是 clipboard.js
          let html = `<button class="copy-btn iconfont iconfuzhi" type="button" data-clipboard-action="copy" data-clipboard-target="#copy${codeIndex}"></button>`;
          const linesLength = str.split(/\n/).length - 1;
          // 生成行号
          let linesNum = '<span aria-hidden="true" class="line-numbers-rows">';
          for (let index = 0; index < linesLength; index++) {
            linesNum = linesNum + "<span></span>";
          }
          linesNum += "</span>";
          if (lang && hljs.getLanguage(lang)) {
            // highlight.js 高亮代码
            const preCode = hljs.highlight(lang, str, true).value;
            html = html + preCode;
            if (linesLength) {
              html += '<b class="name">' + lang + "</b>";
            }
            // 将代码包裹在 textarea 中，由于防止textarea渲染出现问题，这里将 "<" 用 "<" 代替，不影响复制功能
            return `<pre class="hljs"><code>${html}</code>${linesNum}</pre><textarea style="position: absolute;top: -9999px;left: -9999px;z-index: -9999;" id="copy${codeIndex}">${str.replace(
              /<\/textarea>/g,
              "</textarea>"
            )}</textarea>`;
          }
        }
      });
      // 将markdown替换为html标签
      this.aboutContent = md.render(data.data);
    },
    previewImg(img) {
      this.$imagePreview({
        images: this.imgList,
        index: this.imgList.indexOf(img)
      });
    }
  },
  computed: {
    avatar() {
      return this.$store.state.blogInfo.websiteConfig.websiteAvatar;
    },
    cover() {
      var cover = "";
      this.$store.state.blogInfo.pageList.forEach(item => {
        if (item.pageLabel == "about") {
          cover = item.pageCover;
        }
      });
      return "background: url(" + cover + ") center center / cover no-repeat";
    }
  }
};
</script>

<style scoped>
.about-content {
  word-break: break-word;
  line-height: 1.8;
}
.my-wrapper {
  text-align: center;
}
.author-avatar {
  transition: all 0.5s;
}
.author-avatar:hover {
  transform: rotate(360deg);
}
</style>

<style lang="scss">
pre.hljs {
  padding: 12px 2px 12px 40px !important;
  border-radius: 5px !important;
  position: relative;
  font-size: 14px !important;
  line-height: 22px !important;
  overflow: hidden !important;
  &:hover .copy-btn {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  code {
    display: block !important;
    margin: 0 10px !important;
    overflow-x: auto !important;
    &::-webkit-scrollbar {
      z-index: 11;
      width: 6px;
    }
    &::-webkit-scrollbar:horizontal {
      height: 6px;
    }
    &::-webkit-scrollbar-thumb {
      border-radius: 5px;
      width: 6px;
      background: #666;
    }
    &::-webkit-scrollbar-corner,
    &::-webkit-scrollbar-track {
      background: #1e1e1e;
    }
    &::-webkit-scrollbar-track-piece {
      background: #1e1e1e;
      width: 6px;
    }
  }
  .line-numbers-rows {
    position: absolute;
    pointer-events: none;
    top: 12px;
    bottom: 12px;
    left: 0;
    font-size: 100%;
    width: 40px;
    text-align: center;
    letter-spacing: -1px;
    border-right: 1px solid rgba(0, 0, 0, 0.66);
    user-select: none;
    counter-reset: linenumber;
    span {
      pointer-events: none;
      display: block;
      counter-increment: linenumber;
      &:before {
        content: counter(linenumber);
        color: #999;
        display: block;
        text-align: center;
      }
    }
  }
  b.name {
    position: absolute;
    top: 7px;
    right: 45px;
    z-index: 1;
    color: #999;
    pointer-events: none;
  }
  .copy-btn {
    position: absolute;
    top: 6px;
    right: 6px;
    z-index: 1;
    color: #ccc;
    background-color: #525252;
    border-radius: 6px;
    display: none;
    font-size: 14px;
    width: 32px;
    height: 24px;
    outline: none;
  }
}
</style>
