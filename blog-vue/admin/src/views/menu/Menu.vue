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
        新增菜单
      </el-button>
      <!-- 数据筛选 -->
      <div style="margin-left:auto">
        <el-input
          v-model="keywords"
          prefix-icon="el-icon-search"
          size="small"
          placeholder="请输入菜单名"
          style="width:200px"
          @keyup.enter.native="listResources"
        />
        <el-button
          type="primary"
          size="small"
          icon="el-icon-search"
          style="margin-left:1rem"
          @click="listResources"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 权限列表 -->
    <el-table
      v-loading="loading"
      :data="menuList"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="name" label="菜单名称" width="120" />
      <el-table-column prop="icon" align="center" label="图标" width="100">
        <template slot-scope="scope">
          <i :class="'iconfont ' + scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column
        prop="orderNum"
        align="center"
        label="排序"
        width="100"
      />
      <el-table-column prop="path" label="访问路径" />
      <el-table-column prop="component" label="组件路径" />
      <el-table-column prop="isDisable" label="禁用" align="center" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.isDisable"
            active-color="#13ce66"
            inactive-color="#F4F4F5"
            :active-value="1"
            :inactive-value="0"
            @change="changeDisable(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="isHidden" label="隐藏" align="center" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.isHidden"
            active-color="#13ce66"
            inactive-color="#F4F4F5"
            :active-value="1"
            :inactive-value="0"
            @change="changeDisable(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        label="创建时间"
        align="center"
        width="150"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px" />
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="mini"
            @click="openModel(scope.row)"
            v-if="scope.row.children"
          >
            <i class="el-icon-plus" /> 新增
          </el-button>
          <el-button type="text" size="mini" @click="openModel(scope.row)">
            <i class="el-icon-edit" /> 修改
          </el-button>
          <el-popconfirm
            title="确定删除吗？"
            style="margin-left:10px"
            @onConfirm="deleteLink(scope.row.id)"
          >
            <el-button size="mini" type="text" slot="reference">
              <i class="el-icon-delete" /> 删除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新增模态框 -->
    <el-dialog :visible.sync="addMenu" width="30%">
      <el-form label-width="80px" size="medium" :model="categoryForm">
        <el-form-item label="菜单类型">
          <el-radio-group v-model="isCatalog">
            <el-radio :label="true">目录</el-radio>
            <el-radio :label="false">一级菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="上级菜单" v-if="!isCatalog">
          <el-select v-model="value" placeholder="请选择">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="菜单名">
          <el-input v-model="menuForm.name" style="width:220px" />
        </el-form-item>
        <el-form-item label="菜单图标">
          <el-input v-model="menuForm.icon" style="width:220px" />
        </el-form-item>
        <el-form-item label="组件路径">
          <el-input v-model="menuForm.component" style="width:220px" />
        </el-form-item>
        <el-form-item label="路由地址">
          <el-input v-model="menuForm.path" style="width:220px" />
        </el-form-item>
        <el-form-item label="显示排序">
          <el-input v-model="menuForm.orderNum" style="width:220px" />
        </el-form-item>
        <el-form-item label="显示状态">
          <el-radio-group v-model="menuForm.isHidden">
            <el-radio :label="true">隐藏</el-radio>
            <el-radio :label="false">显示</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditCategory">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.listResources();
  },
  data() {
    return {
      loading: true,
      addMenu: false,
      isCatalog: null,
      menuList: [],
      menuForm: {}
    };
  },
  methods: {
    listResources() {
      this.axios.get("/api/admin/menus").then(({ data }) => {
        this.menuList = data.data;
        this.loading = false;
      });
    },
    openModel(menu) {
      console.log(menu);
      this.addMenu = true;
    }
  }
};
</script>
