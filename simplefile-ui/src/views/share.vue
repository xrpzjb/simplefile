<template>
  <div style="background: #f2f2f2;width: 100%;min-height: 100%;">


    <div class="app-container" >

      <el-menu
          :default-active="activeIndex2"
          class="el-menu-demo"
          mode="horizontal"
          background-color="#409EFF"
          text-color="#fff"
          active-text-color="#fff"
          @select="handleSelect"
      >
        <el-menu-item index="1">simpleFile</el-menu-item>
      </el-menu>

      <el-row style="width: 80%;margin: 0 auto;">
        <el-col :span="18" >
          <el-card>
            <template #header>
              <div class="card-header">
                <span>分享内容</span>
              </div>
            </template>

            <el-row >
              <el-table id="fileTable" ref="fileTableRef" :data="sortedData" :row-class-name="addRowClassName" :default-sort="currentSort"  style="width: 100%;" :height="tableHeight" v-loading="loading" @row-dblclick="handleRowSelFilelick"  @sort-change="handleSortChange" @row-click="handleRowClick">
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

          </el-card>
        </el-col>
<!--        <el-col :span="6" style="padding-left: 10px;">-->
<!--          <el-card>-->
<!--            <template #header>-->
<!--              <div class="card-header">-->
<!--                <span>属性</span>-->
<!--              </div>-->
<!--            </template>-->
<!--            <div>-->
<!--              <el-descriptions title="">-->
<!--                <el-descriptions-item label="类型">图片</el-descriptions-item>-->
<!--              </el-descriptions>-->
<!--              <el-descriptions title="">-->
<!--                <el-descriptions-item label="大小">35M</el-descriptions-item>-->
<!--              </el-descriptions>-->
<!--              <el-descriptions title="">-->
<!--                <el-descriptions-item label="时间">2025-01-20 14:29</el-descriptions-item>-->
<!--              </el-descriptions>-->
<!--            </div>-->
<!--          </el-card>-->
<!--        </el-col>-->
      </el-row>

    </div>
  </div>


</template>

<script setup name="setting">
import { ref } from 'vue'
import {shareDetail,shareList,shareVerify} from "@/api/simplefile/share.js";
import {
  Search,
  ArrowLeft,
  ArrowRight,
  Delete,
  Upload,
  Folder,
  Edit,
  Share,
  DocumentRemove,
  DocumentCopy,
  FolderAdd,
  Download,
  Message
} from '@element-plus/icons-vue'
import { ElMessage,ElMessageBox,ElLoading } from 'element-plus'
//引入VueOfficeDocx组件
import VueOfficeDocx from '@vue-office/docx'
import VueOfficePdf from '@vue-office/pdf'
// 引入VueOfficeExcel组件
import VueOfficeExcel from '@vue-office/excel'
// 引入相关样式
import '@vue-office/excel/lib/index.css'
// 引入相关样式
import '@vue-office/docx/lib/index.css'

/*************************** 定义变量 *****************************/

// 当前页面实例
const { proxy } = getCurrentInstance();
// 当前排序
const currentSort = ref({ prop: '', order: '' });
// 表格初始高度
const tableHeight = ref('300px');
// 加载条
const loading = ref(true);
// 数据字典
const { service_file_type } = proxy.useDict("service_file_type");
// 文件列表
const fileDataList = ref([]);
// 当前目录层级
const fileDirList = ref([]);
// 当前目录
const nowDisk = ref({});
/// 文件列表查询条件
const queryParams = ref({});
// 选中当前行
const currentRow = ref({});
// 业务主要标识
const shareData = ref({
  shareCode:null,
  shareToken:null,
  havPwd:null,
  sharePwd:null,
  fileName:null
});
/*************************** 事件 *****************************/

const addRowClassName = (row) => {
  var selrow = toRaw(row)
  if(currentRow.value != null
      && selrow.row.fileId == currentRow.value.fileId
      && currentRow.value.fileId != undefined){
    if(fileOperType.value == 2){
      return "current-jt-row";
    }
    if(fileOperType.value == 3){
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
  if(currentRow.value.type != 0){
    fileDetailHandle();
    return;
  }
  queryParams.value.filePath = null;
  // 在这里处理双击行的逻辑
  queryParams.value.dirId = row.fileId;
  getList();
};

const handleRowClick =  (row, column, event) => {
  proxy.$refs["rightClickMenu"].close();
  currentRow.value = row;
  console.log("选中当前行：" + currentRow.value.fileId);
};

function handleSortChange({ prop, order }) {
  currentSort.value = { prop, order };
}

const sortedData = computed(() => {
  const { prop, order } = currentSort.value;
  return [...fileDataList.value].sort((a, b) => {
    var astr = a[prop];
    var bstr = b[prop];
    if(prop == 'fileSize'){
      astr = parseInt(a[prop]);
      bstr = parseInt(b[prop]);
    }
    if (order === 'ascending') {
      return astr > bstr ? 1 : -1;
    } else if (order === 'descending') {
      return astr < bstr ? 1 : -1;
    }
    return 0;
  });
});

/*************************** api服务数据请求 *****************************/

apiShareDetail();

// 分享详情
// 读取该资源是否需要密码，当前是否存在密码，以及文件名称
function apiShareDetail() {
  shareDetail(shareData.value.shareCode).then(response => {
    // shareData.value.shareCode = response.data.shareCode;
    shareData.value.shareName = response.data.shareName;
    shareData.value.havPwd = response.data.havPwd;
    if(shareData.value.havPwd && !shareData.value.sharePwd){
      // 弹出密码框
    }
    if(!shareData.value.havPwd){
      apiShareVerify();
    }
  });
}

// 分享列表
// 读取资源列表
function apiShareList() {
  shareList(shareData.value.shareToken, null).then(response => {
    fileDataList.value = response.data.diskFileList;
    fileDirList.value = response.data.diskFileDirList;
    nowDisk.value = response.data.nowDisk;
    loading.value = false;
    currentRow.value = ref({});
  });
}

// 分享验证
// 验证分享密码，验证成功获取临时token
function apiShareVerify() {
  shareVerify(shareData.value.shareCode, shareData.value.sharePwd).then(response => {
    shareData.value.shareToken = response.data;
    if(shareData.value.shareToken){
      // 有值就是验证成功，进行获取文件数据
      apiShareList();
    }
  });
}


</script>

<style scoped lang="scss">
  :deep(.el-card.is-always-shadow){
    box-shadow: none;
  }
  .el-menu-demo{
    width: 80%;
    margin: 0 auto;
    margin-bottom: 10px;
  }
  :deep(.app-container){
    padding-top:0px;
  }
</style>

