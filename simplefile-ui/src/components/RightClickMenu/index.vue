<template>
  <!-- 右键菜单 -->
  <div class="rightMenu" v-show="showMenu">
    <ul>
      <li v-for="(menu, index) in menus" @click="menu.click" :key="index">
        <el-icon>
          <component :is="menu.icon" />
        </el-icon>
        <span style="margin-left: 10px;">
              {{ menu.name }}
        </span>
      </li>
    </ul>
  </div>
</template>

<script setup name="RightClickMenu">
import { defineExpose } from "vue";
// 接收菜单信息
const props = defineProps({
  menus: {
    type: Object,
    topHeight:0,
    default: [],
  }
})
const showMenu = ref(false);
// 关闭菜单
function close() {
  showMenu.value = false
}
// 打开菜单和显示位置
function open(event,topHeight) {
  // 阻止系统默认行为
  event.preventDefault();
  // 先关闭
  showMenu.value = false;
  // 显示位置
  let menu = document.querySelector('.rightMenu');
  var docHeight = document.documentElement.clientHeight;
  menu.style.display = 'block';
  var height = menu.offsetHeight;
  menu.style.display = 'none';
  menu.style.left = event.clientX + 'px';
  let nowHeight = (event.clientY);
  if(docHeight - nowHeight > height){
    menu.style.top = (nowHeight) + 'px';
  }else{
    menu.style.top = (nowHeight-height) + 'px';
  }
  // 显示
  showMenu.value = true;
  // 注册点击侦听事件
  document.addEventListener('click', close);
}
// 暴露方法
defineExpose({ open, close });
</script>

<style scoped>

.rightMenu {
  position: fixed;
  z-index: 99999999;
  cursor: pointer;
  border: 1px solid #fff;
  box-shadow: 0 0.5em 1em 1px rgba(0, 0, 0, 0.1);
  border-radius: 6px;
  color: #606266;
  font-size: 14px;
  background: #fff;
  padding:5px;
}

.rightMenu ul {
  list-style: none;
  margin: 0;
  padding: 0;
  border-radius: 6px;
}

.rightMenu ul li {
  padding: 6px 10px;
  border-bottom: 1px solid #ccc8c852;
  box-sizing: border-box;
  display:block;
  align-items: center;
  justify-content: space-around;
}

.rightMenu ul li:last-child {
  border: none;
}

.rightMenu ul li:hover {
  background: #409eff;
  color: #fff;
}


.rightMenu ul li:first-child {
  border-radius: 6px 6px 0 0;
}
.rightMenu ul li:last-child {
  border-radius: 0 0 6px 6px;
}
</style>
