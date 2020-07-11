import Vue from "vue";
import VueRouter from "vue-router";
import Home from "../views/home/Home.vue";
import Article from "../views/article/Article.vue";
import Archive from "../views/archive/Archive.vue";
import Tag from "../views/tag/Tag.vue";
import Category from "../views/category/Category.vue";
import Link from "../views/link/Link.vue";
import About from "../views/about/About.vue";
import Message from "../views/message/Messsage.vue";
import ArticleList from "../components/ArticleList.vue";
import User from "../views/user/User.vue";
import OauthLogin from "../components/OauthLogin.vue";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    component: Home
  },
  {
    path: "/articles/*",
    component: Article
  },
  {
    path: "/archives",
    component: Archive
  },
  {
    path: "/tags",
    component: Tag
  },
  {
    path: "/categories",
    component: Category
  },
  {
    path: "/categories/*",
    component: ArticleList
  },
  {
    path: "/links",
    component: Link
  },
  {
    path: "/about",
    component: About
  },
  {
    path: "/message",
    component: Message
  },
  {
    path: "/tags/*",
    component: ArticleList
  },
  {
    path: "/user",
    component: User
  },
  {
    path: "/oauth/login/qq",
    component: OauthLogin
  },
  {
    path: "/oauth/login/weibo",
    component: OauthLogin
  }
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
});

export default router;
