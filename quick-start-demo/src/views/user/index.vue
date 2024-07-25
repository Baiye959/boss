<template>
  <div class="container">
    <header class="header">
      <div class="input-group">
        <label>用户名:</label>
        <input type="text" v-model="tempQuery.name" placeholder="输入用户名"/>
      </div>
      <div class="input-group">
        <label>身份证号:</label>
        <input type="text" v-model="tempQuery.citizenId" placeholder="输入身份证号"/>
      </div>
      <button @click="handleUserSearch" class="search-button">查询</button>
    </header>
    <main class="main-content">
      <div class="actions">
        <button @click="openAddDialog" class="add-button">添加</button>
        <button @click="deleteSelectedItems" class="delete-button">删除</button>
      </div>
      <ul class="list">
        <li class="list-header">
          <input type="checkbox" v-model="selectAll" @change="toggleSelectAll"/>
          <span class="list-item-index">序号</span>
          <span class="list-item-id">身份证号</span>
          <span class="list-item-name">姓名</span>
          <span class="list-item-birthday">生日</span>
          <span class="list-item-actions">操作</span>
        </li>
        <li v-for="(item, index) in paginatedItems" :key="item.id" class="list-item">
          <input type="checkbox" v-model="item.selected"/>
          <span class="list-item-index">{{ (currentPage - 1) * pageSize + index + 1 }}</span>
          <span class="list-item-id">{{ item.idNumber }}</span>
          <span class="list-item-name">{{ item.name }}</span>
          <span class="list-item-birthday">{{ item.birthday }}</span>
          <span class="list-item-actions">
            <button @click="openEditDialog(item)" class="edit-button">编辑</button>
            <button @click="confirmDeleteItem(item)" class="delete-button">删除</button>
          </span>
        </li>
      </ul>
    </main>
    <div class="pagination">
      <select v-model="pageSize" @change="updatePagination">
        <option v-for="size in pageSizeOptions" :key="size" :value="size">{{ size }}</option>
      </select>
      <button @click="prevPage" :disabled="currentPage === 1">&lt;</button>
      <button v-for="page in totalPages" :key="page" @click="goToPage(page)" :class="{ active: currentPage === page }">{{ page }}</button>
      <button @click="nextPage" :disabled="currentPage === totalPages">&gt;</button>
    </div>
    <footer class="footer">
      <p>© 2024 Baiye959</p>
    </footer>

    <UserDialog
        v-if="showAddDialog"
        :isEdit="false"
        @save="addUser"
        @close="closeAddDialog"
    />
    <UserDialog
        v-if="showEditDialog"
        :isEdit="true"
        :user="editItemData"
        @save="saveEdit"
        @close="closeEditDialog"
    />

    <div v-if="showDeleteDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>确定执行删除操作？</h3>
        <div class="button-group">
          <button @click="deleteItem" class="confirm-button">确定</button>
          <button @click="closeDeleteDialog" class="cancel-button">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { search, add, update, findById, remove, batchRemove } from '@/api/user';
import UserDialog from './UserDialog.vue';

export default {
  name: "UserManage",
  components: {
    UserDialog
  },
  data() {
    return {
      userQuery: { name: '', citizenId: '' },
      tempQuery: { name: '', citizenId: '' }, // 临时查询条件
      items: [],
      filteredItems: [],
      paginatedItems: [],
      selectAll: false,
      showAddDialog: false,
      showEditDialog: false,
      showDeleteDialog: false,
      editItemData: {},
      itemToDelete: null,
      currentPage: 1,
      pageSize: 5,
      pageSizeOptions: [5, 10, 15, 20],
      pagination: {
        pageSize: 5,
        pageIndex: 1,
        total: 0
      }
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.filteredItems.length / this.pageSize);
    }
  },
  methods: {
    filterItems() {
      this.filteredItems = this.items.filter(item =>
          item.name.includes(this.userQuery.name) && item.idNumber.includes(this.userQuery.citizenId)
      );
      this.paginateItems();
    },
    paginateItems() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      this.paginatedItems = this.filteredItems.slice(start, end);
    },
    async fetchItems() {
      try {
        console.log('Fetching items with query:', this.userQuery); // 调试输出
        const response = await search(this.userQuery);
        console.log('Fetch response:', response); // 调试输出
        if (response && response.body && response.body.data) {
          this.items = response.body.data;
          this.pagination.pageSize = response.body.pageSize;
          this.pagination.pageIndex = response.body.pageIndex;
          this.pagination.total = response.body.total;
          this.filterItems(); // 在获取新数据后进行过滤和分页
        }
      } catch (error) {
        console.error('获取用户数据失败:', error);
      }
    },
    handleUserSearch() {
      this.userQuery = { ...this.tempQuery }; // 将临时查询条件赋值给实际查询对象
      this.currentPage = 1;
      this.fetchItems();
    },
    openAddDialog() {
      this.editItemData = {}; // 清空编辑项数据
      this.showAddDialog = true;
    },
    closeAddDialog() {
      this.showAddDialog = false;
    },
    async addUser(newUser) {
      try {
        const response = await add(newUser);
        console.log(response.message);
        this.fetchItems();
        this.showAddDialog = false;
      } catch (error) {
        console.error('添加用户失败:', error);
      }
    },
    async openEditDialog(item) {
      try {
        const response = await findById(item.id);
        console.log('User data:', response); // 调试输出用户数据
        if (response.head.code === 200) {
          this.editItemData = response.body; // 确保 editItemData 被正确赋值
          this.showEditDialog = true;
        } else {
          console.error('获取用户信息失败:', response.head.message);
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
      }
    },
    async saveEdit(editedUser) {
      try {
        const response = await update(editedUser);
        console.log(response.message);
        this.fetchItems();
        this.showEditDialog = false;
      } catch (error) {
        console.error('更新用户失败:', error);
      }
    },
    closeEditDialog() {
      this.showEditDialog = false;
    },
    confirmDeleteItem(item) {
      this.itemToDelete = item;
      this.showDeleteDialog = true;
    },
    async deleteItem() {
      try {
        const response = await remove(this.itemToDelete.id, this.itemToDelete.version);
        console.log(response.message);
        this.fetchItems();
        this.showDeleteDialog = false;
      } catch (error) {
        console.error('删除用户失败:', error);
      }
    },
    closeDeleteDialog() {
      this.showDeleteDialog = false;
    },
    async deleteSelectedItems() {
      try {
        const selectedItems = this.items.filter(item => item.selected);
        const idVersions = selectedItems.map(item => ({ id: item.id, version: item.version }));
        const response = await batchRemove(idVersions);
        console.log(response.message);
        this.fetchItems();
      } catch (error) {
        console.error('批量删除用户失败:', error);
      }
    },
    toggleSelectAll() {
      this.filteredItems.forEach(item => item.selected = this.selectAll);
    },
    updatePagination() {
      if (this.currentPage > this.totalPages) {
        this.currentPage = this.totalPages;
      }
      this.paginateItems(); // 更新分页项目
    },
    goToPage(page) {
      this.currentPage = page;
      this.paginateItems(); // 更新分页项目
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.paginateItems(); // 更新分页项目
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.paginateItems(); // 更新分页项目
      }
    }
  },
  watch: {
    pageSize() {
      this.updatePagination();
    }
  },
  async created() {
    await this.fetchItems();
  }
};
</script>

<style>
.container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  background-color: #fff;
}
.header {
  background-color: #f8f8f8;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-bottom: 1px solid #ddd;
}
.input-group {
  display: flex;
  align-items: center;
  margin-right: 20px;
}
.input-group:last-child {
  margin-right: 0;
}
.input-group label {
  margin-right: 5px;
  font-weight: bold;
  white-space: nowrap;
}
.header input {
  width: 150px;
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.header .search-button {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  margin-left: 10px;
}
.header .search-button:hover {
  background-color: #0056b3;
}
.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #fafafa;
}
.actions {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}
.actions .add-button {
  padding: 10px 20px;
  margin-right: 10px;
  border: none;
  border-radius: 4px;
  background-color: #28a745;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}
.actions .add-button:hover {
  background-color: #218838;
}
.actions .delete-button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  background-color: #dc3545;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}
.actions .delete-button:hover {
  background-color: #c82333;
}
.list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}
.list-header, .list-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #ddd;
  background-color: #fff;
  transition: background-color 0.3s;
}
.list-header {
  background-color: #f1f1f1;
  font-weight: bold;
}
.list-item:hover {
  background-color: #f9f9f9;
}
.list-item input[type="checkbox"] {
  margin-right: 10px;
}
.list-item-index {
  width: 50px;
  text-align: center;
}
.list-item-id {
  width: 200px;
}
.list-item-name {
  flex: 1;
}
.list-item-birthday {
  width: 180px;
  text-align: center;
}
.list-item-actions {
  width: 120px;
  text-align: center;
}
.list-item-actions button {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
  transition: background-color 0.3s;
}
.list-item-actions .edit-button {
  background-color: #ffc107;
  color: white;
}
.list-item-actions .edit-button:hover {
  background-color: #e0a800;
}
.list-item-actions .delete-button {
  background-color: #dc3545;
  color: white;
}
.list-item-actions .delete-button:hover {
  background-color: #c82333;
}
.footer {
  background-color: #f8f8f8;
  padding: 10px;
  text-align: center;
  border-top: 1px solid #ddd;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  margin-bottom: 20px; /* 添加底部间距 */
}
.pagination select {
  padding: 5px;
  margin-right: 10px;
}
.pagination button {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  margin-right: 5px;
  transition: background-color 0.3s;
}
.pagination button:hover {
  background-color: #0056b3;
}
.pagination button[disabled] {
  background-color: #ccc;
  cursor: not-allowed;
}
.pagination button.active {
  background-color: #0056b3;
  font-weight: bold;
}
</style>
