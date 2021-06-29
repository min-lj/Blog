import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    component: resolve => require(["../views/home/Home.vue"], resolve),
    meta: {
      title: "风丶宇的个人博客"
    }
  },
  {
    path: "/articles/:articleId",
    component: resolve => require(["../views/article/Article.vue"], resolve)
  },
  {
    path: "/archives",
    component: resolve => require(["../views/archive/Archive.vue"], resolve),
    meta: {
      title: "归档"
    }
  },
  {
    path: "/tags",
    component: resolve => require(["../views/tag/Tag.vue"], resolve),
    meta: {
      title: "标签"
    }
  },
  {
    path: "/categories",
    component: resolve => require(["../views/category/Category.vue"], resolve),
    meta: {
      title: "分类"
    }
  },
  {
    path: "/categories/*",
    component: resolve => require(["../components/ArticleList.vue"], resolve)
  },
  {
    path: "/tags/*",
    component: resolve => require(["../components/ArticleList.vue"], resolve)
  },
  {
    path: "/links",
    component: resolve => require(["../views/link/Link.vue"], resolve),
    meta: {
      title: "友链列表"
    }
  },
  {
    path: "/about",
    component: resolve => require(["../views/about/About.vue"], resolve),
    meta: {
      title: "关于我"
    }
  },
  {
    path: "/message",
    component: resolve => require(["../views/message/Messsage.vue"], resolve),
    meta: {
      title: "留言板"
    }
  },
  {
    path: "/user",
    component: resolve => require(["../views/user/User.vue"], resolve),
    meta: {
      title: "个人中心"
    }
  },
  {
    path: "/oauth/login/qq",
    component: resolve => require(["../components/OauthLogin.vue"], resolve)
  },
  {
    path: "/oauth/login/weibo",
    component: resolve => require(["../components/OauthLogin.vue"], resolve)
  }
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
});

export default router;
