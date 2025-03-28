<template>

  <div class="app-container home" style="z-index: 10">

    <el-page-header @back="onBack">
      <template #breadcrumb>
        <div class="el-row">
          <div class="el-col-20">
            <el-breadcrumb separator="/" v-if="pathType == 1" style="margin-bottom: 0px;">
              <el-breadcrumb-item @click="handleSelPath(0)">
                回收站
              </el-breadcrumb-item>
              <el-breadcrumb-item v-for="(item, index) in fileDirList" :key="index" @click="handleSelPath(item)">
                {{item.name}}
              </el-breadcrumb-item>

            </el-breadcrumb>
          </div>
          <div class="el-col-4">
            <div class="flex items-center">
              <el-input
                  v-model="fileSearchWord"
                  style="width:100%;float:right;"
                  placeholder="请输入需要搜索的内容"
                  class="input-with-select"
              >
                <template #append>
                  <el-button :icon="Search" @click="getSearchList"/>
                </template>
              </el-input>
            </div>
          </div>

        </div>


      </template>
      <template #content>
        <div class="flex items-center">
          <span class="text-large font-600 mr-3"></span>
        </div>
      </template>
      <template #extra>
        <div class="flex items-center">
          <el-button :icon="Delete" class="ml-2" type="danger" @click="handleCleanClick">清空回收站</el-button>
        </div>
      </template>
    </el-page-header>

<!--    <el-row style="margin-top: 10px;margin-bottom: 10px;">-->
<!--      <el-col :span="12">-->
<!--        <div>-->
<!--          <el-button type="primary" :icon="Upload">-->
<!--          </el-button>-->
<!--          <el-button type="primary" :icon="Edit" />-->
<!--          <el-button type="primary" :icon="Share" ></el-button>-->
<!--          <el-button type="primary" :icon="Delete"></el-button>-->

<!--        </div>-->
<!--      </el-col>-->
<!--      <el-col :span="12">-->
<!--        <el-input-->
<!--            v-model="input3"-->
<!--            style="max-width: 300px;float:right;"-->
<!--            placeholder="请输入需要搜索的内容"-->
<!--            class="input-with-select"-->
<!--        >-->
<!--          &lt;!&ndash;          <template #prepend>&ndash;&gt;-->
<!--          &lt;!&ndash;            搜索&ndash;&gt;-->
<!--          &lt;!&ndash;          </template>&ndash;&gt;-->
<!--          <template #append>-->
<!--            <el-button :icon="Search" />-->
<!--          </template>-->
<!--        </el-input>-->
<!--      </el-col>-->
<!--    </el-row>-->

    <el-row @click="handleSumRowClick" @contextmenu.native.prevent="showMenu($event)">
      <el-table :data="sortedData" :row-class-name="addRowClassName" :default-sort="currentSort"  style="width: 100%;" :height="tableHeight" v-loading="loading" @row-dblclick="handleRowSelFilelick"  @sort-change="handleSortChange" @row-click="handleRowClick">
        <el-table-column fixed prop="name" label="文件名称" width="" sortable="custom" :sort-orders="['ascending', 'descending']" >
          <template #default="scope">
            <div style="display: flex; align-items: center" class="fileListDiv">
              <font-awesome-icon icon="fa-solid fa-folder" v-if="scope.row.type == '0'" style="color:#409eff"/>
              <font-awesome-icon icon="fa-solid fa-file-lines" v-else-if="scope.row.type == '1'" />
              <font-awesome-icon icon="fa-solid fa-file-image" v-else-if="scope.row.type == '2'" style="color: #2563eb;"/>
              <font-awesome-icon icon="fa-solid fa-file-word" v-else-if="scope.row.type == '3'" style="color: #2563eb;"/>
              <font-awesome-icon icon="fa-solid fa-file-excel" v-else-if="scope.row.type == '4'"/>
              <font-awesome-icon icon="fa-solid fa-file-pdf" v-else-if="scope.row.type == '5'" style="color: crimson;"/>
              <font-awesome-icon icon="fa-solid fa-file-zipper" v-else-if="scope.row.type == '6'" style="color: green;"/>
              <font-awesome-icon icon="fa-solid fa-file" v-else-if="scope.row.type == '7'" style="color: #e6a23c;"/>
              <font-awesome-icon icon="fa-solid fa-file-powerpoint" v-else-if="scope.row.type == '8'"/>
              <font-awesome-icon icon="fa-solid fa-file-hotjar" v-else-if="scope.row.type == '9'"/>
              <font-awesome-icon icon="fa-solid fa-gears"   v-else-if="scope.row.type == '10'"/>
              <font-awesome-icon icon="fa-solid fa-file-code" v-else-if="scope.row.type == '11'" style="color: crimson;"/>
              <font-awesome-icon icon="fa-solid fa-database" v-else-if="scope.row.type == '12'" style="color: #67c23a;"/>
              <font-awesome-icon icon="fa-solid fa-wrench" v-else-if="scope.row.type == '13'"/>
              <font-awesome-icon icon="fa-solid fa-film" v-else-if="scope.row.type == '14'"/>
              <font-awesome-icon icon="fa-solid fa-music" v-else-if="scope.row.type == '15'"/>
              <font-awesome-icon icon="fa-solid fa-file" v-else-if="scope.row.type == '999'"/>
              <font-awesome-icon icon="fa-solid fa-file" v-else />

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
        <el-table-column prop="lastModifiedTime" label="更新时间" width="200" sortable="custom" :sort-orders="['ascending', 'descending']"/>
      </el-table>
    </el-row>
    <el-row style="margin-top: 10px">
      <el-col :span="12">
        <el-text class="mx-1">{{fileDataList.length}}个项目 {{nowDisk.fileSizeName}}</el-text>
      </el-col>
    </el-row>

    <RightClickMenu :menus="menus" ref="rightClickMenu" />

    <el-drawer
        v-model="drawerFilePro"
        :title="drawerFileInfoNew.name"
        :before-close="handleClose"
    >
      <el-tabs v-model="drawerTabName" class="demo-tabs" @tab-click="handleClick">
        <el-tab-pane label="预览" name="first" v-if="drawerFileInfoNew.type != '0'" style="background: #f2f2f2;padding-top: 5px;">
          <el-image v-for="imageurl in drawerFileInfoNew.url" style="max-width: 100%;max-height: 100%;padding-left: 5px;padding-right: 5px;" :src="imageurl" fit="cover" v-if="drawerFileInfoNew.type == '2' || drawerFileInfoNew.type == '3'"/>
          <el-input
              v-if="drawerFileInfoNew.type == '1' || drawerFileInfoNew.type == '12' || drawerFileInfoNew.type == '10' || drawerFileInfoNew.type == '11' || drawerFileInfoNew.type == '13'"
              v-model="drawerFileInfoNew.txtContent"
              :rows="30"
              style="width: 100%;font-size: 10px;margin-top: -5px;"
              type="textarea"
          />

          <div style="width:100%;height: 175px;margin-top: -5px;padding:20px;padding-top: 50px;" v-if="drawerFileInfoNew.type == '15'">
            <VueAudioPlayer
                ref="audioPlayer"
                :audio-list="audioList"
                :show-play-loading="true"
            >
          </VueAudioPlayer>
          </div>

        </el-tab-pane>
        <el-tab-pane label="属性" name="second">
          <el-descriptions title="">
            <el-descriptions-item label="类型">
              {{getServiceFileType(drawerFileInfoNew.type)}}
            </el-descriptions-item>
          </el-descriptions>
          <el-descriptions title="">
            <el-descriptions-item label="指向位置">{{drawerFileInfoNew.pointPath}}</el-descriptions-item>
          </el-descriptions>
          <el-descriptions title="">
            <el-descriptions-item label="文件位置">{{drawerFileInfoNew.path}}</el-descriptions-item>
          </el-descriptions>
          <el-descriptions title="">
            <el-descriptions-item label="大小">{{drawerFileInfoNew.fileSizeName}}</el-descriptions-item>
          </el-descriptions>
          <el-descriptions title="">
            <el-descriptions-item label="更新时间">{{drawerFileInfoNew.lastModifiedTime}}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </el-drawer>




  </div>
</template>

<script setup name="Index">
/************************** 引入文件 ****************************/
import {
  fileCount,
  filePreview,
  filePreviewUrls,
  fileDetail,
  fileTxtContent,
  fileDownload,
  fileRecoveryList,
  fileDiskDel, fileDiskRestore, fileDiskDelAll
} from "@/api/simplefile/file.js";
import {
  Search,
  ArrowLeft,
  ArrowRight,
  Delete,
  Upload,
  Folder,
  Edit,
  Share,
  Switch,
  DocumentRemove,
  DocumentCopy,
  FolderAdd,
  Download,
  Message
} from '@element-plus/icons-vue'
import { ElMessage,ElMessageBox,ElLoading } from 'element-plus'
import {listDept} from "@/api/system/dept.js";
import {listRole} from "@/api/system/role.js";
import {listNotice} from "@/api/system/notice.js";
import RightClickMenu from "@/components/RightClickMenu"
import {markRaw, ref} from 'vue';

/************************** 定义变量 ****************************/
// 当前页面实例
const {proxy} = getCurrentInstance();
const fileSearchWord = ref('');
// 菜单
const menus = ref([]);
// 剪贴板上的文件
const selFile = ref({});
// 文件属性
const drawerFilePro = ref(false);
const drawerTabName = ref('first');
const drawerFileInfoNew = ref({});

// 数据字典
const {service_file_type} = proxy.useDict("service_file_type");

const version = ref('3.8.8')
// 文件列表
const fileDataList = ref([]);
// 当前目录层级
const fileDirList = ref([]);
// 当前目录
const nowDisk = ref({});
// 加载条
const loading = ref(true);
// 文件列表查询条件
const queryParams = ref({});
// 选中当前行
const currentRow = ref({});
// table
const tableRef = ref({});
// 路径类型
const pathType = ref(1);
// 路径
const filePath = ref();
// 当前排序
const currentSort = ref({prop: '', order: ''});
// 表格初始高度
const tableHeight = ref('300px');
// 当前操作 0默认操作1选择2剪切3复制4重命名
const fileOperType = ref(0);

/************************* 事件处理 ********************************/
const menu_delete = {
  icon: markRaw(DocumentRemove),
  name: "立即删除(D)",
  click: () => {
    fileDelete(currentRow.value.fileId);
  }
};
const menu_back = {
  icon: markRaw(Switch),
  name: "还原(Y)",
  click: () => {
    fileRestore(currentRow.value.fileId);
  }
};
const menu_download = {
  icon: markRaw(Download),
  name: "下载(D)",
  click: () => {
    fileBaseDownoad(currentRow.value.fileId);
  }
};
const menu_property = {
  icon: markRaw(Delete),
  name: "属性(R)",
  click: () => {
    audioList.value = [];
    drawerFilePro.value = true;
    drawerFileInfoNew.value = currentRow.value;
    fileDetail(drawerFileInfoNew.value.fileId).then(response => {
      let pages = response.data.pages;
      drawerFileInfoNew.value.url = filePreviewUrls(drawerFileInfoNew.value.fileId, 'm', pages);
    });
    fileTxtContent(drawerFileInfoNew.value.fileId).then(response => {
      drawerFileInfoNew.value.txtContent = response;
    });
    if (drawerFileInfoNew.value.type != 0) {
      drawerTabName.value = 'second';
    }
    if (drawerFileInfoNew.value.type == 15) {
      let audioNowList = [{
        "src": filePreview(drawerFileInfoNew.value.fileId, 'm'),
        "title": drawerFileInfoNew.value.name
      }];
      audioList.value = audioNowList;
    }
  }
};
const showMenu = (event, data) => {
  console.log(currentRow.value != null && currentRow.value.fileId != null);
  menus.value = [menu_download, menu_delete, menu_back, menu_property];
  proxy.$refs["rightClickMenu"].open(event);
}

const handleSumRowClick = () => {
  proxy.$refs["rightClickMenu"].close();
}

const addRowClassName = (row) => {
  var selrow = toRaw(row)
  if (currentRow.value != null
      && selrow.row.fileId == currentRow.value.fileId
      && currentRow.value.fileId != undefined) {
    if (fileOperType.value == 2) {
      return "current-jt-row";
    }
    if (fileOperType.value == 3) {
      return "current-row";
    }
    return "current-row";
  }
}

// 双击文件
const handleRowSelFilelick = (row, column, event) => {
  console.log('双击行:', row);
  console.log('双击列:', column);
  console.log('事件:', event);
  if (currentRow.value.type != 0) {
    drawerFilePro.value = true;
    drawerFileInfoNew.value = currentRow.value;
    fileDetail(drawerFileInfoNew.value.fileId).then(response => {
      let pages = response.data.pages;
      drawerFileInfoNew.value.url = filePreviewUrls(drawerFileInfoNew.value.fileId, 'm', pages);
    });
    fileTxtContent(drawerFileInfoNew.value.fileId).then(response => {
      drawerFileInfoNew.value.txtContent = response;
    });
    if (drawerFileInfoNew.value.type == 15) {
      let audioNowList = [{
        "src": filePreview(drawerFileInfoNew.value.fileId, 'm'),
        "title": drawerFileInfoNew.value.name
      }];
      audioList.value = audioNowList;
    }
    return;
  }
  queryParams.value.filePath = null;
  // 在这里处理双击行的逻辑
  queryParams.value.dirId = row.fileId;
  getList();
};

const handleRowClick = (row, column, event) => {
  proxy.$refs["rightClickMenu"].close();
  // selFile.value = null;
  currentRow.value = row;
  // fileOperType.value = 0;
  console.log("选中当前行：" + currentRow.value.fileId);
};

function handleSortChange({prop, order}) {
  currentSort.value = {prop, order};
}

const sortedData = computed(() => {
  const {prop, order} = currentSort.value;
  console.log(prop + '--' + order);
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
function handleSelPath(item) {
  if (item == 0) {
    queryParams.value.dirId = null;
  } else {
    queryParams.value.dirId = item.id;
    queryParams.value.filePath = item.filePath;
  }
  getList();
}

function handleInputPathSel() {
  pathType.value = 2;
  filePath.value = nowDisk.value.path;
}

function handleInputPathBlur() {
  pathType.value = 1;
  filePath.value = null;
}


// 获取文件类型
function getServiceFileType(type) {
  return proxy.selectDictLabel(this.service_file_type, type);
}


onMounted(() => {
  // 获取窗口高度
  tableHeight.value = (window.innerHeight - 210) + 'px';

  // 监听窗口大小变化，更新窗口高度
  window.addEventListener('resize', () => {
    tableHeight.value = (window.innerHeight - 210) + 'px';
  });
});

/**************************** 清空回收站 ******************************/

// 清空回收站事件
function handleCleanClick(){
  ElMessageBox.confirm(
      '确定清空回收站吗?',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
  .then(() => {
    apifileDiskDelAll();
  })
}

function apifileDiskDelAll() {
  loading.value = true;
  fileDiskDelAll().then(response => {
    if(response.code == '200'){
      proxy.$modal.msgSuccess("回收完成");
      getList();
    }
  });
}

/**************************  audio  *********************************/
const audioList = ref([]);
const handleBeforePlay = (next) => {
  // this.currentAudioName =
  //     this.audioList[this.$refs.audioPlayer.currentPlayIndex].title
  next() // Start play
}

/************************* 请求 后台交互 ******************************/

function getList() {
  loading.value = true;
  fileRecoveryList(queryParams.value).then(response => {
    fileDataList.value = response.data.diskFileList;
    fileDirList.value = response.data.diskFileDirList;
    nowDisk.value = response.data.nowDisk;
    loading.value = false;
    currentRow.value = ref({});
  });
}

function getSearchList() {
  queryParams.value.fileName = fileSearchWord.value;
  getList();
}

// 文件删除
function fileDelete(fileId) {
  var datajson = {"fileId": fileId};
  loading.value = true;
  fileDiskDel(datajson).then(response => {
    // 移除改行
    for (let i = 0; i < fileDataList.value.length; i++) {
      if (fileDataList.value[i].fileId == fileId) {
        fileDataList.value.splice(i, 1);
      }
    }
    loading.value = false;
    ElMessage({
      message: '文件删除成功',
      type: 'success',
    })
  })
}

// 文件删除
function fileRestore(fileId) {
  var datajson = {"fileId": fileId};
  loading.value = true;
  fileDiskRestore(datajson).then(response => {
    // 移除改行
    for (let i = 0; i < fileDataList.value.length; i++) {
      if (fileDataList.value[i].fileId == fileId) {
        fileDataList.value.splice(i, 1);
      }
    }
    loading.value = false;
    ElMessage({
      message: '还原成功',
      type: 'success',
    })
  })
}

// 文件下载
function fileBaseDownoad(fileId) {
  fileDownload(fileId);
}

getList();

function goTarget(url) {
  window.open(url, '__blank')
}
</script>

<style scoped lang="scss">
//:deep .el-page-header__breadcrumb{
//  margin-bottom: 0px !important;
//}
//:deep .el-page-header__header{
//  display: none;
//}
:deep(.svg-inline--fa){
  font-size:20px;
  margin-right: 5px;
}
:deep(.el-table__body tr:active) {
  background-color: #409eff !important; /* current-row的背景色 */
  color: #fff; /* current-row的字体颜色 */
}
:deep(.el-drawer__body){
  border-top: 1px solid #f4f4f5;
}
:deep(.el-table__body tr.current-row>td.el-table__cell){
  background-color: #409eff;
  color: #fff;
}
:deep(.el-table__body tr.current-jt-row>td.el-table__cell){
  background-color: #eee;
  color: #999;
}
:deep(.el-table--enable-row-transition .el-table__body td.el-table__cell){
  transition: background-color 0s ease;
}
:deep(.el-breadcrumb__inner a, .el-breadcrumb__inner.is-link){
  font-weight: 600 !important;
}
:deep(.el-breadcrumb){
  font-size: 16px;
  height: 35px;
  line-height: 35px;
}
:deep(.el-page-header__header){
  margin-bottom: 16px;
}

.el-table tr{
  cursor:pointer;
}
.pointBtn{
  background: #fff !important;
  border: 1px solid #d1d5db !important;
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

