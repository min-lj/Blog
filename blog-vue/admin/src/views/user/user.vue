<template>
  <el-card class="main-card">
    <!-- 表格操作 -->
    <div class="operation-container">
      <!-- 条件筛选 -->
      <div style="margin-left:auto">
        <el-input
          v-model="keywords"
          prefix-icon="el-icon-search"
          size="small"
          placeholder="请输入昵称"
          style="width:200px"
          @keyup.enter.native="listUsers"
        />
        <el-button
          type="primary"
          size="small"
          icon="el-icon-search"
          style="margin-left:1rem"
          @click="listUsers"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table border :data="userList" @selection-change="selectionChange">
      <!-- 表格列 -->
      <el-table-column
        prop="linkAvatar"
        label="头像"
        align="center"
        width="100"
      >
        <template slot-scope="scope">
          <img :src="scope.row.avatar" width="40" height="40" />
        </template>
      </el-table-column>
      <el-table-column
        prop="nickname"
        label="昵称"
        align="center"
        width="140"
      />
      <el-table-column
        prop="loginType"
        label="登录方式"
        align="center"
        width="120"
      >
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.loginType == 0">邮箱</el-tag>
          <el-tag v-if="scope.row.loginType == 1">QQ</el-tag>
          <el-tag type="danger" v-if="scope.row.loginType == 2">微博</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="userRole"
        label="用户角色"
        align="center"
        width="120"
      >
        <template slot-scope="scope">
          <el-tag>{{ scope.row.userRole }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isSilence" label="禁言" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.isSilence"
            active-color="#13ce66"
            inactive-color="#F4F4F5"
            :active-value="1"
            :inactive-value="0"
            @change="changeComment(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        prop="ipAddr"
        label="登录ip"
        align="center"
        width="140"
      />
      <el-table-column
        prop="ipSource"
        label="登录地址"
        align="center"
        width="140"
      />
      <el-table-column
        prop="createTime"
        label="创建时间"
        width="130"
        align="center"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px" />
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <el-table-column
        prop="lastLoginTime"
        label="上次登录时间"
        width="130"
        align="center"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px" />
          {{ scope.row.lastLoginTime | date }}
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column label="操作" align="center" width="100">
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="mini"
            @click="openEditModel(scope.row)"
          >
            编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination
      class="pagination-container"
      background
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page="current"
      :page-size="size"
      :total="count"
      :page-sizes="[10, 20]"
      layout="total, sizes, prev, pager, next, jumper"
    />
    <!-- 添加对话框 -->
    <el-dialog :visible.sync="isEdit" width="30%">
      <div class="dialog-title-container" slot="title">
        修改用户
      </div>
      <el-form label-width="60px" size="medium" :model="userForm">
        <el-form-item label="昵称">
          <el-input v-model="userForm.nickname" style="width:220px" />
        </el-form-item>
        <el-form-item label="权限">
          <el-radio v-model="userForm.userRole" label="user">user</el-radio>
          <el-radio v-model="userForm.userRole" label="admin">admin</el-radio>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="isEdit = false">取 消</el-button>
        <el-button type="primary" @click="editUserRole">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.listUsers();
  },
  data: function() {
    return {
      isEdit: false,
      userForm: {
        userInfoId: null,
        nickname: "",
        userRole: ""
      },
      userIdList: [],
      userList: [],
      keywords: null,
      current: 1,
      size: 10,
      count: 0
    };
  },
  methods: {
    selectionChange(userList) {
      this.userIdList = [];
      userList.forEach(item => {
        this.userIdList.push(item.userInfoId);
      });
    },
    sizeChange(size) {
      this.size = size;
      this.listUsers();
    },
    currentChange(current) {
      this.current = current;
      this.listUsers();
    },
    changeComment(user) {
      let param = new URLSearchParams();
      param.append("isSilence", user.isSilence);
      this.axios.put("/api/admin/users/comment/" + user.userInfoId, param);
    },
    updateUserStatus(id) {
      let param = new URLSearchParams();
      if (id != null) {
        param.append("idList", [id]);
      } else {
        param.append("idList", this.userIdList);
      }
      param.append("status", this.status == 0 ? 1 : 0);
      this.axios.put("/api/admin/users", param).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listUsers();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.updateIsDelete = false;
      });
    },
    openEditModel(user) {
      this.userForm = JSON.parse(JSON.stringify(user));
      this.isEdit = true;
    },
    editUserRole() {
      this.axios
        .put("/api/admin/users/role", this.userForm)
        .then(({ data }) => {
          if (data.flag) {
            this.$notify.success({
              title: "成功",
              message: data.message
            });
            this.listUsers();
          } else {
            this.$notify.error({
              title: "失败",
              message: data.message
            });
          }
          this.isEdit = false;
        });
    },
    listUsers() {
      this.axios
        .get("/api/admin/users", {
          params: {
            current: this.current,
            size: this.size,
            keywords: this.keywords
          }
        })
        .then(({ data }) => {
          this.userList = data.data.recordList;
          this.count = data.data.count;
        });
    }
  }
};
</script>
