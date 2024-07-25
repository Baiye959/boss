<template>
  <div class="dialog-overlay">
    <div class="dialog">
      <h3>{{ isEdit ? '编辑用户' : '添加用户' }}</h3>
      <div>
        <label>用户名:</label>
        <input v-model="localUser.name" type="text"/>
      </div>
      <div>
        <label>身份证号:</label>
        <input v-model="localUser.idNumber" type="text"/>
      </div>
      <div>
        <label>生日:</label>
        <input v-model="localUser.birthday" type="date"/>
      </div>
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <div class="button-group">
        <button @click="saveUser">{{ isEdit ? '保存' : '添加' }}</button>
        <button @click="$emit('close')">取消</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "UserDialog",
  props: {
    isEdit: {
      type: Boolean,
      default: false
    },
    user: {
      type: Object,
      default: () => ({
        name: '',
        idNumber: '',
        birthday: ''
      })
    }
  },
  data() {
    return {
      localUser: { ...this.user },
      errorMessage: ''
    };
  },
  watch: {
    user: {
      handler(newValue) {
        this.localUser = { ...newValue };
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    saveUser() {
      // 清空错误信息
      this.errorMessage = '';

      // 验证用户输入
      if (!this.localUser.name || !this.localUser.idNumber || !this.localUser.birthday) {
        this.errorMessage = '所有字段都是必填项。';
        return;
      }

      // 如果所有字段都填写了，提交用户数据
      this.$emit('save', this.localUser);
    }
  }
};
</script>

<style>
.error-message {
  color: red;
  margin-top: 10px;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.dialog {
  background-color: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 500px;
  max-width: 90%;
  position: relative;
  z-index: 1001;
}
.dialog h3 {
  margin-top: 0;
  font-size: 1.5em;
  text-align: center;
}
.input-group {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
.input-group label {
  width: 80px; /* 固定宽度，防止换行 */
  margin-right: 10px;
  font-weight: bold;
}
.input-group input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}
.button-group {
  display: flex;
  justify-content: space-between;
}
.button-group button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}
.button-group button:hover {
  background-color: #0056b3;
}
.button-group .cancel-button {
  background-color: #6c757d;
}
.button-group .cancel-button:hover {
  background-color: #5a6268;
}
</style>
