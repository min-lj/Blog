import Vue from "vue";
import VueRouter from "vue-router";
import Layout from "../layout/index.vue";

Vue.use(VueRouter);

const routes = [
  {
    path: "/login",
    name: "登录",
    hidden: true,
    component: () => import("../views/login/Login.vue")
  },
  {
    path: "/",
    component: Layout,
    children: [
      {
        path: "",
        name: "首页",
        icon: "iconfont el-icon-myshouye",
        component: () => import("../views/home/Home.vue"),
        meta: {
          requireAuth: true
        }
      }
    ]
  },
  {
    path: "/articles/*",
    component: Layout,
    hidden: true,
    children: [
      {
        path: "",
        name: "修改文章",
        icon: "el-icon-edit",
        component: () => import("../views/article/Article.vue"),
        meta: {
          requireAuth: true
        }
      }
    ]
  },
  {
    path: "/article-submenu",
    name: "文章管理",
    icon: "iconfont el-icon-mywenzhang-copy",
    redirect: "/articles",
    component: Layout,
    children: [
      {
        path: "/articles",
        name: "添加文章",
        icon: "iconfont el-icon-myfabiaowenzhang",
        component: () => import("../views/article/Article.vue"),
        meta: {
          requireAuth: true
        }
      },
      {
        path: "/article-list",
        name: "文章列表",
        icon: "iconfont el-icon-mywenzhangliebiao",
        component: () => import("../views/article/ArticleList.vue"),
        meta: {
          requireAuth: true
        }
      },
      {
        path: "/categories",
        name: "分类列表",
        icon: "iconfont el-icon-myfenlei",
        component: () => import("../views/category/Category.vue"),
        meta: {
          requireAuth: true
        }
      },
      {
        path: "/tags",
        name: "标签列表",
        icon: "iconfont el-icon-myicontag",
        component: () => import("../views/tag/Tag.vue"),
        meta: {
          requireAuth: true
        }
      }
    ]
  },
  {
    path: "/message-submenu",
    name: "消息管理",
    icon: "iconfont el-icon-myxiaoxi",
    redirect: "/comments",
    component: Layout,
    children: [
      {
        path: "/comments",
        name: "评论列表",
        icon: "iconfont el-icon-mypinglunzu",
        component: () => import("../views/comment/Comment.vue"),
        meta: {
          requireAuth: true
        }
      },
      {
        path: "/messages",
        name: "留言列表",
        icon: "iconfont el-icon-myliuyan",
        component: () => import("../views/message/Message.vue"),
        meta: {
          requireAuth: true
        }
      }
    ]
  },
  {
    path: "/system-submenu",
    name: "系统管理",
    icon: "iconfont el-icon-myxitong",
    redirect: "/comments",
    component: Layout,
    children: [
      {
        path: "/links",
        name: "友链列表",
        icon: "iconfont el-icon-mydashujukeshihuaico-",
        component: () => import("../views/friendLink/FriendLink.vue"),
        meta: {
          requireAuth: true
        }
      },
      {
        path: "/about",
        name: "关于我",
        icon: "iconfont el-icon-myguanyuwo",
        component: () => import("../views/about/About.vue"),
        meta: {
          requireAuth: true
        }
      },
      {
        path: "/swagger",
        name: "接口文档",
        icon: "iconfont el-icon-mywendang",
        component: () => import("../views/swagger/Swagger.vue"),
        meta: {
          requireAuth: true
        }
      }
    ]
  },
  {
    path: "/user-submenu",
    name: "用户管理",
    icon: "iconfont el-icon-myuser",
    redirect: "/users",
    component: Layout,
    children: [
      {
        path: "/users",
        name: "用户列表",
        icon: "iconfont el-icon-myyonghuliebiao",
        component: () => import("../views/user/user.vue"),
        meta: {
          requireAuth: true
        }
      }
    ]
  },
  {
    path: "/setting",
    component: Layout,
    children: [
      {
        path: "",
        name: "个人中心",
        icon: "iconfont el-icon-myshezhi",
        component: () => import("../views/setting/Setting.vue"),
        meta: {
          requireAuth: true
        }
      }
    ]
  }
];

const router = new VueRouter({
  mode: "history",
  routes
});

export default router;
