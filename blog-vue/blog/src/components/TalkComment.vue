<template>
  <div>
    <div class="comment-title"><i class="iconfont iconpinglunzu" />评论</div>
    <!-- 评论框 -->
    <div class="comment-wrapper">
      <div style="display:flex;width:100%">
        <v-avatar size="36">
          <img
            v-if="this.$store.state.avatar"
            :src="this.$store.state.avatar"
          />
          <img
            v-else
            :src="this.$store.state.blogInfo.websiteConfig.touristAvatar"
          />
        </v-avatar>
        <div style="width:100%" class="ml-3">
          <div class="comment-input">
            <textarea
              class="comment-textarea"
              v-model="commentContent"
              placeholder="留下点什么吧..."
              auto-grow
              dense
            />
          </div>
          <!-- 操作按钮 -->
          <div class="emoji-container">
            <span
              :class="chooseEmoji ? 'emoji-btn-active' : 'emoji-btn'"
              @click="chooseEmoji = !chooseEmoji"
            >
              <i class="iconfont iconbiaoqing" />
            </span>
            <button
              @click="insertComment"
              class="upload-btn v-comment-btn"
              style="margin-left:auto"
            >
              提交
            </button>
          </div>
          <!-- 表情框 -->
          <emoji @addEmoji="addEmoji" :chooseEmoji="chooseEmoji" />
        </div>
      </div>
    </div>
    <!-- 评论详情 -->
    <div v-if="count > 0 && reFresh">
      <!-- 评论列表 -->
      <div
        class="comment-wrapper"
        v-for="(item, index) of commentList"
        :key="item.id"
      >
        <!-- 头像 -->
        <v-avatar size="40" class="comment-avatar">
          <img :src="item.avatar" />
        </v-avatar>
        <div class="comment-meta">
          <!-- 用户名 -->
          <div class="comment-user">
            <span v-if="!item.webSite">{{ item.nickname }}</span>
            <a v-else :href="item.webSite" target="_blank">
              {{ item.nickname }}
            </a>
            <v-icon size="20" color="#ffa51e" v-if="item.userId == 1">
              mdi-check-decagram
            </v-icon>
          </div>
          <!-- 信息 -->
          <div class="comment-info">
            <!-- 楼层 -->
            <span style="margin-right:10px">{{ count - index }}楼</span>
            <!-- 发表时间 -->
            <span style="margin-right:10px">{{ item.createTime | date }}</span>
            <!-- 点赞 -->
            <span
              :class="isLike(item.id) + ' iconfont icondianzan'"
              @click="like(item)"
            />
            <span v-show="item.likeCount > 0"> {{ item.likeCount }}</span>
            <!-- 回复 -->
            <span class="reply-btn" @click="replyComment(index, item)">
              回复
            </span>
          </div>
          <!-- 评论内容 -->
          <p v-html="item.commentContent" class="comment-content"></p>
          <!-- 回复人 -->
          <div
            style="display:flex"
            v-for="reply of item.replyDTOList"
            :key="reply.id"
          >
            <!-- 头像 -->
            <v-avatar size="36" class="comment-avatar">
              <img :src="reply.avatar" />
            </v-avatar>
            <div class="reply-meta">
              <!-- 用户名 -->
              <div class="comment-user">
                <span v-if="!reply.webSite">{{ reply.nickname }}</span>
                <a v-else :href="reply.webSite" target="_blank">
                  {{ reply.nickname }}
                </a>
                <v-icon size="20" color="#ffa51e" v-if="reply.userId == 1">
                  mdi-check-decagram
                </v-icon>
              </div>
              <!-- 信息 -->
              <div class="comment-info">
                <!-- 发表时间 -->
                <span style="margin-right:10px">
                  {{ reply.createTime | date }}
                </span>
                <!-- 点赞 -->
                <span
                  :class="isLike(reply.id) + ' iconfont icondianzan'"
                  @click="like(reply)"
                />
                <span v-show="reply.likeCount > 0"> {{ reply.likeCount }}</span>
                <!-- 回复 -->
                <span class="reply-btn" @click="replyComment(index, reply)">
                  回复
                </span>
              </div>
              <!-- 回复内容 -->
              <p class="comment-content">
                <!-- 回复用户名 -->
                <template v-if="reply.replyUserId != item.userId">
                  <span v-if="!reply.replyWebSite" class="ml-1">
                    @{{ reply.replyNickname }}
                  </span>
                  <a
                    v-else
                    :href="reply.replyWebSite"
                    target="_blank"
                    class="comment-nickname ml-1"
                  >
                    @{{ reply.replyNickname }}
                  </a>
                  ，
                </template>
                <span v-html="reply.commentContent" />
              </p>
            </div>
          </div>
          <!-- 回复数量 -->
          <div
            class="mb-3"
            style="font-size:0.75rem;color:#6d757a"
            v-show="item.replyCount > 3"
            ref="check"
          >
            共
            <b>{{ item.replyCount }}</b>
            条回复，
            <span
              style="color:#00a1d6;cursor:pointer"
              @click="checkReplies(index, item)"
            >
              点击查看
            </span>
          </div>
          <!-- 回复分页 -->
          <div
            class="mb-3"
            style="font-size:0.75rem;color:#222;display:none"
            ref="paging"
          >
            <span style="padding-right:10px">
              共{{ Math.ceil(item.replyCount / 5) }}页
            </span>
            <paging
              ref="page"
              :totalPage="Math.ceil(item.replyCount / 5)"
              :index="index"
              :commentId="item.id"
              @changeReplyCurrent="changeReplyCurrent"
            />
          </div>
          <!-- 回复框 -->
          <Reply :type="type" ref="reply" @reloadReply="reloadReply" />
        </div>
      </div>
      <!-- 加载按钮 -->
      <div class="load-wrapper">
        <v-btn outlined v-if="count > commentList.length" @click="listComments">
          加载更多...
        </v-btn>
      </div>
    </div>
    <!-- 没有评论提示 -->
    <div v-else style="padding:1.25rem;text-align:center">
      来发评论吧~
    </div>
  </div>
</template>

<script>
import Reply from "./Reply";
import Paging from "./Paging";
import Emoji from "./Emoji";
import EmojiList from "../assets/js/emoji";
export default {
  components: {
    Reply,
    Emoji,
    Paging
  },
  props: {
    type: {
      type: Number
    }
  },
  created() {
    this.listComments();
  },
  data: function() {
    return {
      reFresh: true,
      commentContent: "",
      chooseEmoji: false,
      current: 1,
      commentList: [],
      count: 0
    };
  },
  methods: {
    replyComment(index, item) {
      this.$refs.reply.forEach(item => {
        item.$el.style.display = "none";
      });
      //传值给回复框
      this.$refs.reply[index].commentContent = "";
      this.$refs.reply[index].nickname = item.nickname;
      this.$refs.reply[index].replyUserId = item.userId;
      this.$refs.reply[index].parentId = this.commentList[index].id;
      this.$refs.reply[index].chooseEmoji = false;
      this.$refs.reply[index].index = index;
      this.$refs.reply[index].$el.style.display = "block";
    },
    addEmoji(key) {
      this.commentContent += key;
    },
    checkReplies(index, item) {
      this.axios
        .get("/api/comments/" + item.id + "/replies", {
          params: { current: 1, size: 5 }
        })
        .then(({ data }) => {
          this.$refs.check[index].style.display = "none";
          item.replyDTOList = data.data;
          //超过1页才显示分页
          if (Math.ceil(item.replyCount / 5) > 1) {
            this.$refs.paging[index].style.display = "flex";
          }
        });
    },
    changeReplyCurrent(current, index, commentId) {
      //查看下一页回复
      this.axios
        .get("/api/comments/" + commentId + "/replies", {
          params: { current: current, size: 5 }
        })
        .then(({ data }) => {
          this.commentList[index].replyDTOList = data.data;
        });
    },
    listComments() {
      //查看评论
      const path = this.$route.path;
      const arr = path.split("/");
      var param = {
        current: this.current,
        type: this.type
      };
      switch (this.type) {
        case 1:
        case 3:
          param.topicId = arr[2];
          break;
        default:
          break;
      }
      this.axios
        .get("/api/comments", {
          params: param
        })
        .then(({ data }) => {
          if (this.current == 1) {
            this.commentList = data.data.recordList;
          } else {
            this.commentList.push(...data.data.recordList);
          }
          this.current++;
          this.count = data.data.count;
          this.$emit("getCommentCount", this.count);
        });
    },
    insertComment() {
      //判断登录
      if (!this.$store.state.userId) {
        this.$store.state.loginFlag = true;
        return false;
      }
      //判空
      if (this.commentContent.trim() == "") {
        this.$toast({ type: "error", message: "评论不能为空" });
        return false;
      }
      //解析表情
      var reg = /\[.+?\]/g;
      this.commentContent = this.commentContent.replace(reg, function(str) {
        return (
          "<img src= '" +
          EmojiList[str] +
          "' width='22'height='20' style='padding: 0 1px'/>"
        );
      });
      //发送请求
      const path = this.$route.path;
      const arr = path.split("/");
      var comment = {
        commentContent: this.commentContent,
        type: this.type
      };
      switch (this.type) {
        case 1:
        case 3:
          comment.topicId = arr[2];
          break;
        default:
          break;
      }
      this.commentContent = "";
      this.axios.post("/api/comments", comment).then(({ data }) => {
        if (data.flag) {
          // 查询最新评论
          this.current = 1;
          this.listComments();
          const isReview = this.$store.state.blogInfo.websiteConfig
            .isCommentReview;
          if (isReview) {
            this.$toast({ type: "warnning", message: "评论成功，正在审核中" });
          } else {
            this.$toast({ type: "success", message: "评论成功" });
          }
        } else {
          this.$toast({ type: "error", message: data.message });
        }
      });
    },
    like(comment) {
      // 判断登录
      if (!this.$store.state.userId) {
        this.$store.state.loginFlag = true;
        return false;
      }
      // 发送请求
      this.axios
        .post("/api/comments/" + comment.id + "/like")
        .then(({ data }) => {
          if (data.flag) {
            // 判断是否点赞
            if (this.$store.state.commentLikeSet.indexOf(comment.id) != -1) {
              this.$set(comment, "likeCount", comment.likeCount - 1);
            } else {
              this.$set(comment, "likeCount", comment.likeCount + 1);
            }
            this.$store.commit("commentLike", comment.id);
          }
        });
    },
    reloadReply(index) {
      this.axios
        .get("/api/comments/" + this.commentList[index].id + "/replies", {
          params: {
            current: this.$refs.page[index].current
          }
        })
        .then(({ data }) => {
          this.commentList[index].replyCount++;
          //回复大于5条展示分页
          if (this.commentList[index].replyCount > 5) {
            this.$refs.paging[index].style.display = "flex";
          }
          this.$refs.check[index].style.display = "none";
          this.$refs.reply[index].$el.style.display = "none";
          this.commentList[index].replyDTOList = data.data;
        });
    }
  },
  computed: {
    isLike() {
      return function(commentId) {
        var commentLikeSet = this.$store.state.commentLikeSet;
        return commentLikeSet.indexOf(commentId) != -1 ? "like-active" : "like";
      };
    }
  },
  watch: {
    commentList() {
      this.reFresh = false;
      this.$nextTick(() => {
        this.reFresh = true;
      });
    }
  }
};
</script>

<style scoped>
.comment-wrapper {
  margin-top: 20px;
  display: flex;
  padding: 16px 20px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 3px 8px 6px rgb(7 17 27 / 6%);
  transition: all 0.3s ease 0s;
}
.comment-wrapper:hover {
  box-shadow: 0 5px 10px 8px rgb(7 17 27 / 16%);
  transform: translateY(-3px);
}
.comment-title {
  display: flex;
  align-items: center;
  font-size: 1.25rem;
  font-weight: bold;
  line-height: 40px;
  margin-top: 20px;
}
.comment-title i {
  font-size: 1.5rem;
  margin-right: 5px;
}
.count {
  padding: 5px;
  line-height: 1.75;
  font-size: 1.25rem;
  font-weight: bold;
}
.comment-meta {
  margin-left: 0.8rem;
  width: 100%;
  border-bottom: 1px dashed #f5f5f5;
}
.reply-meta {
  margin-left: 0.8rem;
  width: 100%;
}
.comment-user {
  font-size: 15px;
  line-height: 1.75;
}
.comment-user a {
  color: #1abc9c !important;
  font-weight: 500;
  transition: 0.3s all;
}
.comment-nickname {
  text-decoration: none;
  color: #1abc9c !important;
  font-weight: 500;
}
.comment-info {
  line-height: 1.75;
  font-size: 0.75rem;
  color: #b3b3b3;
}
.reply-btn {
  cursor: pointer;
  float: right;
  color: #ef2f11;
}
.comment-content {
  font-size: 0.875rem;
  line-height: 1.75;
  padding-top: 0.625rem;
  white-space: pre-line;
  word-wrap: break-word;
  word-break: break-all;
}
.comment-avatar {
  transition: all 0.5s;
}
.comment-avatar:hover {
  transform: rotate(360deg);
}
.like {
  cursor: pointer;
  font-size: 0.875rem;
}
.like:hover {
  color: #eb5055;
}
.like-active {
  cursor: pointer;
  font-size: 0.875rem;
  color: #eb5055;
}
.load-wrapper {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.load-wrapper button {
  background-color: #49b1f5;
  color: #fff;
}
</style>
