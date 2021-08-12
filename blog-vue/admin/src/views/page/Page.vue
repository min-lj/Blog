<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ this.$route.name }}</div>
    <div class="operation-container">
      <el-button
        type="primary"
        size="small"
        icon="el-icon-plus"
        @click="openModel(null)"
      >
        新建页面
      </el-button>
    </div>
    <!-- 相册列表 -->
    <el-row class="page-container" :gutter="12" v-loading="loading">
      <!-- 空状态 -->
      <el-empty v-if="pageList.length == 0" description="暂无页面" />
      <el-col v-for="item of pageList" :key="item.id" :md="6">
        <div class="page-item">
          <!-- 相册操作 -->
          <div class="page-opreation">
            <el-dropdown @command="handleCommand">
              <i class="el-icon-more" style="color:#fff" />
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="'update' + JSON.stringify(item)">
                  <i class="el-icon-edit" />编辑
                </el-dropdown-item>
                <el-dropdown-item :command="'delete' + item.id">
                  <i class="el-icon-delete" />删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <el-image fit="cover" class="page-cover" :src="item.pageCover" />
          <div class="page-name">{{ item.pageName }}</div>
        </div>
      </el-col>
    </el-row>
    <!-- 新增模态框 -->
    <el-dialog :visible.sync="addOrEdit" width="35%" top="10vh">
      <div class="dialog-title-container" slot="title" ref="pageTitle" />
      <el-form label-width="80px" size="medium" :model="pageForum">
        <el-form-item label="页面名称">
          <el-input style="width:220px" v-model="pageForum.pageName" />
        </el-form-item>
        <el-form-item label="页面标签">
          <el-input style="width:220px" v-model="pageForum.pageLabel" />
        </el-form-item>
        <el-form-item label="页面封面">
          <el-upload
            class="upload-cover"
            drag
            :show-file-list="false"
            action="/api/admin/config/images"
            multiple
            :on-success="uploadCover"
          >
            <i class="el-icon-upload" v-if="pageForum.pageCover == ''" />
            <div class="el-upload__text" v-if="pageForum.pageCover == ''">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <img
              v-else
              :src="pageForum.pageCover"
              width="360px"
              height="180px"
            />
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditPage">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 删除对话框 -->
    <el-dialog :visible.sync="isdeletePage" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900" />提示
      </div>
      <div style="font-size:1rem">是否删除该页面？</div>
      <div slot="footer">
        <el-button @click="isdeletePage = false">取 消</el-button>
        <el-button type="primary" @click="deletePage">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.listPages();
  },
  data: function() {
    return {
      keywords: "",
      loading: true,
      current: 1,
      size: 8,
      count: 0,
      isdeletePage: false,
      addOrEdit: false,
      pageForum: {
        id: null,
        pageName: "",
        pageLabel: "",
        pageCover: ""
      },
      pageList: []
    };
  },
  methods: {
    openModel(item) {
      if (item) {
        console.log(item);
        this.pageForum = JSON.parse(item);
        this.$refs.pageTitle.innerHTML = "修改页面";
      } else {
        this.pageForum = {
          id: null,
          pageName: "",
          pageLabel: "",
          pageCover: ""
        };
        this.$refs.pageTitle.innerHTML = "新建页面";
      }
      this.addOrEdit = true;
    },
    listPages() {
      this.axios.get("/api/admin/pages").then(({ data }) => {
        this.pageList = data.data;
        this.loading = false;
      });
    },
    addOrEditPage() {
      if (this.pageForum.pageName.trim() == "") {
        this.$message.error("页面名称不能为空");
        return false;
      }
      if (this.pageForum.pageLabel.trim() == "") {
        this.$message.error("页面标签不能为空");
        return false;
      }
      if (this.pageForum.pageCover == null) {
        this.$message.error("页面封面不能为空");
        return false;
      }
      this.axios.post("/api/admin/pages", this.pageForum).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listPages();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
      this.addOrEdit = false;
    },
    uploadCover(response) {
      this.pageForum.pageCover = response.data;
    },
    handleCommand(command) {
      const type = command.substring(0, 6);
      const data = command.substring(6);
      if (type == "delete") {
        this.pageForum.id = data;
        this.isdeletePage = true;
      } else {
        console.log(data);
        this.openModel(data);
      }
    },
    deletePage() {
      this.axios
        .delete("/api/admin/pages/" + this.pageForum.id)
        .then(({ data }) => {
          if (data.flag) {
            this.$notify.success({
              title: "成功",
              message: data.message
            });
            this.listPages();
          } else {
            this.$notify.error({
              title: "失败",
              message: data.message
            });
          }
          this.isdeletePage = false;
        });
    }
  }
};
</script>

<style scoped>
.page-cover {
  position: relative;
  border-radius: 4px;
  width: 100%;
  height: 170px;
}
.page-name {
  text-align: center;
  margin-top: 0.5rem;
}
.page-item {
  position: relative;
  cursor: pointer;
  margin-bottom: 1rem;
}
.page-opreation {
  position: absolute;
  z-index: 1000;
  top: 0.5rem;
  right: 0.8rem;
}
</style>
