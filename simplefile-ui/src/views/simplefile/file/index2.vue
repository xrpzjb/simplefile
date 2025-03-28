<template>
  <div aria-label="A complete example of page header" style="padding: 20px;">
    <el-page-header @back="onBack">
      <template #breadcrumb>
        <el-breadcrumb separator="/" v-if="pathType == 1">
          <el-breadcrumb-item :to="{ path: './page-header.html' }">
            个人空间
          </el-breadcrumb-item>
          <el-breadcrumb-item v-for="(item, index) in fileDirList" :key="index" @click="handleSelPath(item)">{{item.name}}</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
      <template #content>
        <div class="flex items-center">

        </div>
      </template>
      <template #extra>
        <div class="flex items-center">
          <el-input
              v-model="input3"
              style="max-width: 300px;float:right;"
              placeholder="请输入需要搜索的内容"
              class="input-with-select"
          >

            <template #append>
              <el-button :icon="Search" />
            </template>
          </el-input>
        </div>
      </template>
    </el-page-header>

      <el-row>
        <el-table :data="sortedData" :default-sort="currentSort"  style="width: 100%;" :height="tableHeight" v-loading="loading" @row-dblclick="handleRowSelFilelick" @row-contextmenu="handleRowRightSelFilelick" @sort-change="handleSortChange">
          <el-table-column fixed prop="name" label="文件名称" width="" sortable="custom" :sort-orders="['ascending', 'descending']">
            <template #default="scope">
              <div style="display: flex; align-items: center">
                <el-image style="width: 25px; height: 25px" :src="fileType0" fit="contain" v-if="scope.row.type == 0"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType1" fit="contain" v-else-if="scope.row.type == 1"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType2" fit="contain" v-else-if="scope.row.type == 2"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType3" fit="contain" v-else-if="scope.row.type == 3"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType4" fit="contain" v-else-if="scope.row.type == 4"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType5" fit="contain" v-else-if="scope.row.type == 5"></el-image>
                <el-image style="width: 23px; height: 25px" :src="fileType6" fit="contain" v-else-if="scope.row.type == 6"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType7" fit="contain" v-else-if="scope.row.type == 7"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType8" fit="contain" v-else-if="scope.row.type == 8"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType9" fit="contain" v-else-if="scope.row.type == 9"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType11" fit="contain" v-else-if="scope.row.type == 11"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType12" fit="contain" v-else-if="scope.row.type == 12"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType13" fit="contain" v-else-if="scope.row.type == 13"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType14" fit="contain" v-else-if="scope.row.type == 14"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType15" fit="contain" v-else-if="scope.row.type == 15"></el-image>
                <el-image style="width: 25px; height: 25px" :src="fileType999" fit="contain" v-else-if="scope.row.type == 999"></el-image>

                <el-image style="width: 25px; height: 25px" :src="fileType999" fit="contain" v-else></el-image>

                {{scope.row.name}}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="150" sortable="custom" :sort-orders="['ascending', 'descending']">
            <template #default="scope">
              <dict-tag :options="service_file_type" :value="scope.row.type" />
            </template>
          </el-table-column>
          <el-table-column prop="fileSize" label="大小" width="150" sortable="custom" :sort-orders="['ascending', 'descending']">
            <template #default="scope">
              {{scope.row.fileSizeName}}
            </template>
          </el-table-column>
          <el-table-column prop="lastModifiedTime" label="修改时间" width="200" sortable="custom" :sort-orders="['ascending', 'descending']"/>
        </el-table>
        <el-text class="mx-1" style="margin-top: 5px;">{{fileDataList.length}}个项目</el-text>
      </el-row>



  </div>
</template>

<script setup name="Index">
import fileType0 from '@/assets/images/fileType/0.png'
import fileType1 from '@/assets/images/fileType/1.png'
import fileType2 from '@/assets/images/fileType/2.png'
import fileType3 from '@/assets/images/fileType/3.png'
import fileType4 from '@/assets/images/fileType/4.png'
import fileType5 from '@/assets/images/fileType/5.png'
import fileType6 from '@/assets/images/fileType/6.png'
import fileType7 from '@/assets/images/fileType/7.png'
import fileType8 from '@/assets/images/fileType/8.png'
import fileType9 from '@/assets/images/fileType/9.png'
import fileType11 from '@/assets/images/fileType/11.png'
import fileType12 from '@/assets/images/fileType/12.png'
import fileType13 from '@/assets/images/fileType/13.png'
import fileType14 from '@/assets/images/fileType/14.png'
import fileType15 from '@/assets/images/fileType/15.png'
import fileType999 from '@/assets/images/fileType/999.png'
import { fileList } from "@/api/simplefile/file.js";
import { ElNotification as notify } from 'element-plus'

const { proxy } = getCurrentInstance();
const { service_file_type } = proxy.useDict("service_file_type");


import {
  Search,
  ArrowLeft,
  ArrowRight,
  Delete,
  Upload,
  Folder,
  Edit,
  Share,
} from '@element-plus/icons-vue'
import {listDept} from "@/api/system/dept.js";
import {listRole} from "@/api/system/role.js";
import {listNotice} from "@/api/system/notice.js";

const version = ref('3.8.8')
const fileDataList = ref([]);
const fileDirList = ref([]);
const nowDisk = ref({});
const loading = ref(true);
const queryParams = ref({});
const pathType = ref(1);
const filePath = ref();
const currentSort = ref({ prop: '', order: '' });
const tableHeight = ref('300px');

const onBack = () => {
  notify('Back')
}

// 双击文件
const handleRowSelFilelick = (row, column, event) => {
  console.log('双击行:', row);
  console.log('双击列:', column);
  console.log('事件:', event);
  queryParams.value.filePath = null;
  // 在这里处理双击行的逻辑
  queryParams.value.dirId = row.fileId;
  getList();
};


function handleSortChange({ prop, order }) {
  currentSort.value = { prop, order };
}

const sortedData = computed(() => {
  const { prop, order } = currentSort.value;
  console.log(prop +'--' + order);
  return [...fileDataList.value].sort((a, b) => {
    if (a[prop] < b[prop]) {
      return order === 'ascending' ? -1 : 1;
    }
    if (a[prop] > b[prop]) {
      return order === 'ascending' ? 1 : -1;
    }
    return 0;
  });
});

// 选择目录
function handleSelPath(item){
  queryParams.value.dirId = null;
  queryParams.value.filePath = item.filePath;
  getList();
}

function handleInputPathSel(){
  console.log(1111);
  console.log(nowDisk);
  pathType.value = 2;
  filePath.value = nowDisk.value.path;
  console.log(filePath.value);
}

function handleInputPathBlur(){
  pathType.value = 1;
  filePath.value = null;
}

function initHeight(){

}

onMounted(() => {
  // 获取窗口高度
  tableHeight.value = (window.innerHeight - 190) + 'px';

  // 监听窗口大小变化，更新窗口高度
  window.addEventListener('resize', () => {
    tableHeight.value = (window.innerHeight - 190) + 'px';
  });
});

function getList() {
  loading.value = true;
  fileList(queryParams.value).then(response => {
    fileDataList.value = response.data.diskFileList;
    fileDirList.value = response.data.diskFileDirList;
    nowDisk.value = response.data.nowDisk;
    loading.value = false;
  });
}

getList();

function goTarget(url) {
  window.open(url, '__blank')
}
</script>

<style scoped lang="scss">
.el-table tr{
  cursor:pointer;
}
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>

