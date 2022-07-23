export default function markdownToHtml(content) {
  const MarkdownIt = require("markdown-it");
  const hljs = require("highlight.js");
  const md = new MarkdownIt({
    html: true,
    linkify: true,
    typographer: true,
    breaks: true,
    highlight: function(str, lang) {
      // 当前时间加随机数生成唯一的id标识
      var d = new Date().getTime();
      if (window.performance && typeof window.performance.now === "function") {
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
      if (lang == null) {
        lang = "java";
      }
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
  })
    .use(require("markdown-it-sub"))
    .use(require("markdown-it-sup"))
    .use(require("markdown-it-mark"))
    .use(require("markdown-it-abbr"))
    .use(require("markdown-it-container"))
    .use(require("markdown-it-deflist"))
    .use(require("markdown-it-emoji"))
    .use(require("markdown-it-footnote"))
    .use(require("markdown-it-ins"))
    .use(require("markdown-it-katex-external"))
    .use(require("markdown-it-task-lists"));
  // 将markdown替换为html标签
  return md.render(content);
}
