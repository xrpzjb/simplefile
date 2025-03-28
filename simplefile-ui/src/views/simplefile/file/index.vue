<template>
  <!-- 页面主容器 -->
  <div class="app-container home" style="z-index: 10">
    <!-- 页面头部组件，包含面包屑导航、搜索框和上传按钮 -->
    <el-page-header @back="onBack">
      <!-- 面包屑导航和搜索框部分 -->
      <template #breadcrumb>
        <div class="el-row">
          <!-- 面包屑导航占 16 列 -->
          <div class="el-col-16">
            <!-- 当路径类型为 1 时显示面包屑导航 -->
            <el-breadcrumb separator="/" v-if="pathType == 1" style="margin-bottom: 0px;">
              <!-- 点击回到个人空间 -->
              <el-breadcrumb-item @click="handleSelPath(0)">
                个人空间
              </el-breadcrumb-item>
              <!-- 循环显示文件目录列表 -->
              <el-breadcrumb-item v-for="(item, index) in fileDirList" :key="index" @click="handleSelPath(item)">
                {{item.name}}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <!-- 搜索框占 8 列 -->
          <div class="el-col-8">
            <div class="flex items-center">
              <!-- 输入框用于搜索文件，绑定搜索关键词，输入变化时触发搜索 -->
              <el-input
                  v-model="fileSearchWord"
                  style="width:100%;float:right;"
                  placeholder="请输入需要搜索的内容"
                  class="input-with-select"
                  @change="getSearchList"
              >
                <!-- 搜索按钮 -->
                <template #append>
                  <el-button :icon="Search" @click="getSearchList"/>
                </template>
              </el-input>
            </div>
          </div>
        </div>
      </template>
      <!-- 页面头部内容部分，目前为空 -->
      <template #content>
        <div class="flex items-center">
        </div>
      </template>
      <!-- 页面头部额外部分，包含上传文件按钮 -->
      <template #extra>
        <div class="flex items-center">
          <el-button type="primary" class="ml-2" @click="openUpload">上传文件</el-button>
        </div>
      </template>
    </el-page-header>

    <!-- 文件列表表格 -->
    <el-row @click="handleSumRowClick" @contextmenu.native.prevent="showMenu($event)" >
      <el-table id="fileTable" ref="fileTableRef" :data="sortedData" :row-class-name="addRowClassName" :default-sort="currentSort"  style="width: 100%;" :height="tableHeight" v-loading="loading" @row-dblclick="handleRowSelFilelick"  @sort-change="handleSortChange" @row-click="handleRowClick">
        <!-- 文件名称列 -->
        <el-table-column fixed prop="name" label="文件名称" width="" sortable="custom" :sort-orders="['ascending', 'descending']" >
          <template #default="scope">
            <div style="display: flex; align-items: center" class="fileListDiv">
              <!-- 根据文件类型显示不同的图标 -->
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
              <!-- 显示文件名称 -->
              {{scope.row.name}}
            </div>
          </template>
        </el-table-column>
        <!-- 文件类型列 -->
        <el-table-column prop="type" label="类型" width="150" sortable="custom" :sort-orders="['ascending', 'descending']">
          <template #default="scope">
            <!-- 根据数据字典显示文件类型 -->
            <dict-tag :options="service_file_type" :value="scope.row.type" />
          </template>
        </el-table-column>
        <!-- 文件大小列 -->
        <el-table-column prop="fileSize" label="大小" width="150" sortable="custom" :sort-orders="['ascending', 'descending']">
          <template #default="scope">
            <!-- 显示文件大小名称 -->
            {{scope.row.fileSizeName}}
          </template>
        </el-table-column>
        <!-- 文件更新时间列 -->
        <el-table-column prop="lastModifiedTime" label="更新时间" width="200" sortable="custom" :sort-orders="['ascending', 'descending']"/>
      </el-table>
    </el-row>
    <!-- 显示文件数量和当前磁盘使用情况 -->
    <el-row style="margin-top: 10px">
      <el-col :span="12">
        <el-text class="mx-1">{{fileDataList.length}}个项目 {{nowDisk.fileSizeName}}</el-text>
      </el-col>
    </el-row>

    <!-- 右键菜单组件 -->
    <RightClickMenu :menus="menus" ref="rightClickMenu" />

    <!-- 文件属性抽屉 -->
    <el-drawer
        v-model="drawerFilePro"
        :size="drawerFileProSize"
        :title="drawerFileInfoNew.name"
        :before-close="handleClose"
    >
      <!-- 抽屉顶部操作按钮 -->
      <div class="flex gap-2" style="position: absolute;right:25px;padding-top: 5px;z-index: 999">
        <el-button key="primary" type="button" class="wintag" size="small" @click="fileQuickDownload()"><el-icon><Download /></el-icon></el-button>
        <el-button key="primary" type="button" class="wintag" size="small" @click="changeTagWinSize(30)">30%</el-button>
        <el-button key="primary" type="button" class="wintag" size="small" @click="changeTagWinSize(50)">50%</el-button>
        <el-button key="primary" type="button" class="wintag" size="small" @click="changeTagWinSize(80)">80%</el-button>
        <el-button key="primary" type="button" class="wintag" size="small" @click="changeTagWinSize(100)">100%</el-button>
      </div>
      <!-- 抽屉内的标签页 -->
      <el-tabs v-model="drawerTabName" class="demo-tabs" @tab-click="handleClick" style="clear: both;">
        <!-- 预览标签页 -->
        <el-tab-pane label="预览" name="first" v-if="drawerFileInfoNew.type != '0'">
          <!-- 图片预览 -->
          <el-image v-for="imageurl in drawerFileInfoNew.urls" style="max-width: 100%;max-height: 100%;padding-left: 5px;padding-right: 5px;" :src="imageurl" fit="cover"
                    v-if="drawerFileInfoNew.handleFileType == 'pic'" />
          <!-- 文本文件预览 -->
          <div v-else-if="drawerFileInfoNew.handleFileType == 'txt'">
            <pre class="language-javascript line-numbers" :style="{ height: preHeight }">
              <code >{{ drawerFileInfoNew.txtContent }}</code>
            </pre>
          </div>
          <!-- PDF 文件预览 -->
          <div v-else-if="drawerFileInfoNew.handleFileType == 'pdf'">
            <el-text v-if="!drawerFileInfoNew.load">正在加载...</el-text>
            <vue-office-pdf
                :src="drawerFileInfoNew.url"
                @rendered="vueOffceRendered"
                :style="{ height: officeHeight }"
            />
          </div>
          <!-- Word 文件预览 -->
          <div v-else-if="drawerFileInfoNew.handleFileType == 'doc'">
            <el-text v-if="!drawerFileInfoNew.load">正在加载...</el-text>
            <vue-office-docx
                :src="drawerFileInfoNew.url"
                @rendered="vueOffceRendered"
                :style="{ height: officeHeight }"
            />
          </div>
          <!-- Excel 文件预览 -->
          <div v-else-if="drawerFileInfoNew.handleFileType == 'excel'">
            <el-text v-if="!drawerFileInfoNew.load">正在加载...</el-text>
            <vue-office-excel id="vue-office-excel"
                              :src="drawerFileInfoNew.url"
                              @rendered="vueOffceRendered"
                              :style="{ height: officeHeight }"
            />
          </div>
          <!-- 压缩文件预览 -->
          <div v-else-if="drawerFileInfoNew.handleFileType == 'zip'">
            <el-tree
                style="max-width: 600px"
                :data="drawerFileInfoNew.zipdata"
                :props="zipDefaultProps"
            />
          </div>
          <!-- 音频文件预览 -->
          <div style="width:100%;height: 160px;margin-top: -5px;padding:20px;padding-top: 50px;" v-else-if="drawerFileInfoNew.handleFileType == 'audio'">
            <VueAudioPlayer
                ref="audioPlayer"
                :audio-list="audioList"
                :show-play-loading="true"
            >
            </VueAudioPlayer>
          </div>
          <!-- 不支持预览的提示 -->
          <div v-else style="background: #fff !important;">
            暂不支持预览
            <el-button key="primary" type="button" class="wintag" size="small" @click="openFileTryTxt()">尝试以文本形式打开</el-button>
          </div>
        </el-tab-pane>
        <!-- 属性标签页 -->
        <el-tab-pane label="属性" name="second">
          <!-- 显示文件类型 -->
          <el-descriptions title="">
            <el-descriptions-item label="类型">
              {{getServiceFileType(drawerFileInfoNew.type)}}
            </el-descriptions-item>
          </el-descriptions>
          <!-- 显示文件指向位置 -->
          <el-descriptions title="">
            <el-descriptions-item label="指向位置">{{drawerFileInfoNew.pointPath}}</el-descriptions-item>
          </el-descriptions>
          <!-- 显示文件位置 -->
          <el-descriptions title="">
            <el-descriptions-item label="文件位置">{{drawerFileInfoNew.path}}</el-descriptions-item>
          </el-descriptions>
          <!-- 显示文件大小 -->
          <el-descriptions title="">
            <el-descriptions-item label="大小">{{drawerFileInfoNew.fileSizeName}}</el-descriptions-item>
          </el-descriptions>
          <!-- 显示文件更新时间 -->
          <el-descriptions title="">
            <el-descriptions-item label="更新时间">{{drawerFileInfoNew.lastModifiedTime}}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </el-drawer>

    <!-- 文件重命名对话框 -->
    <el-dialog
        v-model="renameDialogFormVisible"
        title="文件重命名"
        width="500" align-center="true">
      <el-form >
        <el-form-item label="新文件名" label-width="140px">
          <el-input v-model="renameNewFileName" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="renameDialogFormVisible = false">关闭</el-button>
          <el-button type="primary" @click="filerRename">
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分享设置对话框 -->
    <el-dialog
        v-model="shareDialog.open"
        title="分享"
        width="500"
        align-center>
      <el-form >
        <el-form-item label="分享链接" v-if="shareDialog.data.link">
          <el-input v-model="shareDialog.data.link" autocomplete="off" readonly/>
        </el-form-item>
        <el-form-item label="分享密码" >
          <el-input v-model="shareDialog.data.shareEncryCode" autocomplete="off" />
        </el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker
              v-model="shareDialog.data.shareTime"
              type="datetime"
              placeholder="请选择过期时间"
              clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="shareDialog.open = false">关闭</el-button>
          <el-button type="primary" @click="shareSubmit">
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增文件夹对话框 -->
    <el-dialog v-model="createNewDirDialogFormVisible" title="新增文件夹" width="500" align-center>
      <el-form >
        <el-form-item label="文件夹名称" label-width="140px">
          <el-input v-model="createNewDirName" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="renameDialogFormVisible = false">关闭</el-button>
          <el-button type="primary" @click="fileNewDir">
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 文件上传对话框 -->
    <el-dialog
        v-model="uploadDialogVisible"
        title="上传文件"
        width="500"
        :before-close="handleClose"
        align-center
    >
      <el-upload
          v-model:file-list="uploadFileList"
          class="upload-demo"
          :action="fileDiskUploadUrl()"
          multiple
          drag
          auto-upload="true"
          :on-success="uploadSuccess"
          :on-error="uploadError"
          :limit="3"
      >
        <el-button type="primary">选择文件</el-button>
        <template #tip>
          <div class="el-upload__tip">
          </div>
        </template>
      </el-upload>
    </el-dialog>

    <!-- 解压提示对话框 -->
    <el-dialog
        v-model="unzipDialogVisible"
        title="Tips"
        width="500"
    >
      <span>正在解压中 <br>
        已耗时：{{ formattedTimeElapsed }}
      </span>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="unzipDialogVisible = false">
            后台等待
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Index">
/************************** 引入文件 ****************************/
// 引入 Prism 代码高亮库及其样式和插件
import Prism from 'prismjs';
import 'prismjs/themes/prism-okaidia.css';
import 'prismjs/plugins/line-numbers/prism-line-numbers.js';
import 'prismjs/plugins/line-numbers/prism-line-numbers.css';

// 引入文件操作相关的 API
import {
  fileList,
  filecp,
  filemv,
  filedel,
  filerename,
  fileCreateDir,
  fileCount,
  filePreview,
  filePreviewUrls,
  fileDetail,
  fileTxtContent,
  fileDownload,
  fileUploadUrl,
  fileShare,
  fileShareDetail,
  fileZipDetail,
  fileUnzipZip
} from "@/api/simplefile/file.js";

// 引入 ElementPlus 图标
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

// 引入 ElementPlus 消息、弹窗、加载组件
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'

// 引入右键菜单组件
import RightClickMenu from "@/components/RightClickMenu"

// 引入 Vue 响应式相关函数
import { markRaw, ref, nextTick} from 'vue';

// 引入文件预览相关组件
import VueOfficeDocx from '@vue-office/docx'
import VueOfficePdf from '@vue-office/pdf'
import VueOfficeExcel from '@vue-office/excel'

// 引入文件预览组件样式
import '@vue-office/excel/lib/index.css'
import '@vue-office/docx/lib/index.css'

/************************** 定义变量 ****************************/
// 获取当前组件实例
const { proxy } = getCurrentInstance();

// 搜索关键词
const fileSearchWord = ref('');

// 右键菜单选项
const menus = ref([]);

// 剪贴板上的文件
const selFile = ref({});

// 文件属性抽屉显示状态
const drawerFilePro = ref(false);

// 文件属性抽屉大小
const drawerFileProSize = ref('30%');

// 文件属性抽屉当前激活的标签页
const drawerTabName = ref('first');

// 文件属性信息
const drawerFileInfoNew = ref({});

// 文件重命名对话框显示状态
const renameDialogFormVisible = ref(false)

// 新文件名
const renameNewFileName = ref(null)

// 新增文件夹对话框显示状态
const createNewDirDialogFormVisible = ref(false)

// 新文件夹名称
const createNewDirName = ref(null)

// 动态高度
const dynamicHeight = ref(100)

// 获取数据字典
const {service_file_type} = proxy.useDict("service_file_type");

// 版本号
const version = ref('3.8.8');

// 文件表格引用
const fileTableRef = ref({});

// 文件列表
const fileDataList = ref([]);

// 当前目录层级
const fileDirList = ref([]);

// 当前目录信息
const nowDisk = ref({});

// 加载状态
const loading = ref(true);

// 文件列表查询条件
const queryParams = ref({});

// 选中的当前行
const currentRow = ref({});

// 路径类型
const pathType = ref(1);

// 路径
const filePath = ref();

// 当前排序规则
const currentSort = ref({ prop: 'type', order: 'ascending' });

// 表格初始高度
const tableHeight = ref('300px');

// 文本预览高度
const preHeight = ref('500px');

// 办公文件预览高度
const officeHeight = ref('300px');

// 当前操作类型
const fileOperType = ref(0);

// 加载实例
let loadingInstance;

// 开始加载动画
function startLoading(targetDom, msg) {
  loadingInstance = ElLoading.service({
    target: targetDom, // 指定需要覆盖的 DOM 节点
    text: msg, // 设置加载提示文字
    background: 'rgba(0, 0, 0, 0.7)', // 可选：设置背景颜色和透明度
    // 你可以根据需求添加更多的配置项，如 spinner、customClass 等
  });
}

// 结束加载动画
function endLoading() {
  if (loadingInstance) {
    loadingInstance.close();
  }
}

/************************ 压缩文件 **************************/
// 解压对话框显示状态
const unzipDialogVisible = ref(false);

// 解压开始时间
const startTime = ref(null);

// 时间间隔引用
const intervalRef = ref(null);

// 已耗时格式化显示
const formattedTimeElapsed = ref('0 分 0 秒');

// 压缩文件树属性配置
const zipDefaultProps = {
  children: 'children',
  label: 'label',
}

// 显示解压对话框
const showUnzipDialog = () => {
  unzipDialogVisible.value = true;
  startTime.value = Date.now();
  intervalRef.value = setInterval(updateTimeElapsed, 1000);
};

// 更新已耗时显示
const updateTimeElapsed = () => {
  if (startTime.value) {
    const currentTime = Date.now();
    const timeElapsed = currentTime - startTime.value;
    const totalSeconds = Math.floor(timeElapsed / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    formattedTimeElapsed.value = `${minutes} 分 ${seconds} 秒`;
  }
};

// 关闭解压对话框
const closeUnzipDialog = () => {
  clearInterval(intervalRef.value);
  unzipDialogVisible.value = false;
};

// 获取压缩文件详情
function ziPreviewGet() {
  fileZipDetail(currentRow.value.fileId).then(response => {
    drawerFileInfoNew.value.zipdata = response.data;
  });
}

// 解压文件
function unzipFile() {
  showUnzipDialog();
  fileUnzipZip(currentRow.value.fileId).then(response => {
    closeUnzipDialog();
    if (response.code == '200') {
      getList();
      ElMessage({
        message: '解压成功',
        type: 'success',
        plain: true,
      })
    } else {
      ElMessage({
        message: '解压失败',
        type: 'error',
        plain: true,
      })
    }
  }).catch(error => {
    closeUnzipDialog();
    if (error.code === 'ECONNABORTED') {
      // 处理超时错误
      console.error('请求超时，请检查网络连接或稍后重试。');
    } else {
      // 处理其他类型的错误
      console.error('请求出错，错误信息:', error);
    }
  });;
}

// 解压菜单选项
const menu_unzip = {
  icon: markRaw(DocumentRemove),
  name: "解压(D)",
  click: () => {
    unzipFile();
  }
};

/************************* 分享 ********************************/
// 分享对话框状态和数据
const shareDialog = ref({
  open: false,
  data: {
    shareTime: null,
    fileId: null,
    shareId: null,
    shareEncryCode: null
  }
});

// 分享菜单选项
const menu_share = {
  icon: markRaw(DocumentRemove),
  name: "分享(S)",
  click: () => {
    fileOperType.value = 5;
    shareDialog.value.data = {};
    shareDialog.value.open = true;
    shareDialog.value.data.fileId = currentRow.value.fileId;
    fileShareDetailApi();
  }
};

// 获取分享详情
function fileShareDetailApi() {
  fileShareDetail(currentRow.value.fileId).then(response => {
    shareDialog.value.data = response.data;
  });
}

// 提交分享设置
function shareSubmit() {
  fileShare(shareDialog.value.data).then(response => {
    if (response.code == '200') {
      // 分享成功，显示link
      ElMessageBox.alert(response.data, '分享链接', { confirmButtonText: '好的' });
      shareDialog.value.open = false;
    } else {
      ElMessage.error(response.msg);
    }
  });
}

// 尝试以文本形式打开文件
function openFileTryTxt() {
  drawerFileInfoNew.value.handleFileType = 'txt';
}

/************************* 事件处理 ********************************/
// 剪切菜单选项
const menu_jiantie = {
  icon: markRaw(DocumentRemove),
  name: "剪切(T)",
  click: () => {
    fileOperType.value = 2;
    selFile.value = currentRow.value;
  }
};

// 复制菜单选项
const menu_copy = {
  icon: markRaw(DocumentCopy),
  name: "复制(C)",
  click: () => {
    fileOperType.value = 3;
    selFile.value = currentRow.value;
  }
};

// 粘贴菜单选项
const menu_niantie = {
  icon: markRaw(DocumentRemove),
  name: "粘贴(P)",
  click: () => {
    if (fileOperType.value == 2) {
      fileMove(selFile.value.fileId, nowDisk.value.fileId);
    } else if (fileOperType.value == 3) {
      fileCopy(selFile.value.fileId, nowDisk.value.fileId);
    }
  },
};

// 新建文件夹菜单选项
const menu_newfloder = {
  icon: markRaw(DocumentRemove),
  name: "新建文件夹(T)",
  click: () => {
    createNewDirDialogFormVisible.value = true;
    createNewDirName.value = null;
  }
};

// 删除菜单选项
const menu_delete = {
  icon: markRaw(Delete),
  name: "删除(D)",
  click: () => {
    fileDelete(currentRow.value.fileId);
  }
};

// 重命名菜单选项
const menu_rename = {
  icon: markRaw(Delete),
  name: "重命名(M)",
  click: () => {
    renameNewFileName.value = currentRow.value.name;
    renameDialogFormVisible.value = true;
  }
};

// 下载菜单选项
const menu_download = {
  icon: markRaw(Download),
  name: "下载(D)",
  click: () => {
    fileBaseDownoad(currentRow.value.fileId);
  }
};

// 属性菜单选项
const menu_property = {
  icon: markRaw(Delete),
  name: "属性(R)",
  click: () => {
    fileDetailHandle();
  }
};

// 显示右键菜单
const showMenu = (event, data) => {
  console.log(currentRow.value != null && currentRow.value.fileId != null);
  if (selFile.value != null && selFile.value.fileId != null) {
    // 剪贴板有文件
    if (currentRow.value != null && currentRow.value.fileId != null) {
      // 当前选择了文件
      // 分享、剪切、复制、粘贴、新建文件夹、删除、重命名
      menus.value = [menu_download, menu_share, menu_jiantie, menu_copy, menu_niantie, menu_delete, menu_rename, menu_newfloder, menu_property];
      if (currentRow.value.handleFileType == 'zip') {
        menus.value = [menu_download, menu_share, menu_unzip, menu_jiantie, menu_copy, menu_niantie, menu_delete, menu_rename, menu_newfloder, menu_property];
      }
    } else {
      // 当前没有选择文件
      if (fileOperType.value == 2) {
        // 粘贴操作
        menus.value = [menu_niantie, menu_newfloder];
      } else if (fileOperType.value == 3) {
        // 复制操作
        menus.value = [menu_niantie, menu_newfloder,];
      }
    }
  } else {
    // 剪贴板没有文件
    if (currentRow.value != null && currentRow.value.fileId != null) {
      // 当前选择了文件
      // 分享、剪切、复制、粘贴、新建文件夹、删除、重命名
      menus.value = [menu_download, menu_share, menu_jiantie, menu_copy, menu_niantie, menu_delete, menu_rename, menu_newfloder, menu_property];
      if (currentRow.value.handleFileType == 'zip') {
        menus.value = [menu_download, menu_share, menu_unzip, menu_jiantie, menu_copy, menu_niantie, menu_delete, menu_rename, menu_newfloder, menu_property];
      }
    } else {
      // 当前没有选择文件
      menus.value = [menu_newfloder];
    }
  }
  proxy.$refs["rightClickMenu"].open(event);
}

// 点击表格行外部关闭右键菜单
const handleSumRowClick = () => {
  proxy.$refs["rightClickMenu"].close();
}

// 添加行样式类
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

// 双击文件行处理
const handleRowSelFilelick = (row, column, event) => {
  console.log('双击行:', row);
  console.log('双击列:', column);
  console.log('事件:', event);
  if (currentRow.value.type != 0) {
    fileDetailHandle();
    return;
  }
  queryParams.value.filePath = null;
  // 在这里处理双击行的逻辑
  queryParams.value.dirId = row.fileId;
  getList();
};

// 点击文件行处理
const handleRowClick = (row, column, event) => {
  proxy.$refs["rightClickMenu"].close();
  // selFile.value = null;
  currentRow.value = row;
  // fileOperType.value = 0;
  console.log("选中当前行：" + currentRow.value.fileId);
};

// 表格排序变化处理
function handleSortChange({ prop, order }) {
  currentSort.value = { prop, order };
}

// 计算排序后的数据
const sortedData = computed(() => {
  const { prop, order } = currentSort.value;
  return [...fileDataList.value].sort((a, b) => {
    var astr = a[prop];
    var bstr = b[prop];
    if (prop == 'fileSize') {
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

// 选择目录处理
function handleSelPath(item) {
  if (item == 0) {
    queryParams.value.dirId = null;
  } else {
    queryParams.value.dirId = item.id;
    queryParams.value.filePath = item.filePath;
  }
  getList();
}

// 输入路径选择处理
function handleInputPathSel() {
  pathType.value = 2;
  filePath.value = nowDisk.value.path;
}

// 输入路径失去焦点处理
function handleInputPathBlur() {
  pathType.value = 1;
  filePath.value = null;
}

// 获取文件类型名称
function getServiceFileType(type) {
  return proxy.selectDictLabel(this.service_file_type, type);
}

// 快速下载文件
function fileQuickDownload() {
  fileBaseDownoad(drawerFileInfoNew.value.fileId)
}

// 获取文件详情
function fileDetailHandle() {
  // drawerFileProSize.value = '70%';
  audioList.value = [];
  drawerFileInfoNew.value.load = false;
  drawerFileInfoNew.value = currentRow.value;
  drawerFileInfoNew.value.txtContent = '';
  drawerFileInfoNew.value.zipdata = [];
  drawerFilePro.value = true;
  fileDetail(drawerFileInfoNew.value.fileId).then(response => {
    let pages = response.data.pages;
    drawerFileInfoNew.value.urls = filePreviewUrls(drawerFileInfoNew.value.fileId, 'm', pages);
    drawerFileInfoNew.value.url = filePreview(drawerFileInfoNew.value.fileId, 'm');
    if (drawerFileInfoNew.value.handleFileType == 'doc'
        || drawerFileInfoNew.value.handleFileType == 'pdf'
        || drawerFileInfoNew.value.handleFileType == 'excel') {
      // drawerFileProSize.value = '70%';
    } else {
      // drawerFileProSize.value = '35%';
    }
    drawerFilePro.value = true;
  });
  if (drawerFileInfoNew.value.handleFileType == 'txt') {
    fileTxtContent(drawerFileInfoNew.value.fileId).then(response => {
      drawerFileInfoNew.value.txtContent = response;
      nextTick(() => {
        Prism.highlightAll();
      });
    });
  }
  if(drawerFileInfoNew.value.handleFileType == 'audio'){
    let audioNowList = [{"src":filePreview(drawerFileInfoNew.value.fileId,'m'),"title":drawerFileInfoNew.value.name}];
    audioList.value = audioNowList;
  }
  if(drawerFileInfoNew.value.handleFileType == 'zip'){
    ziPreviewGet(drawerFileInfoNew.value.fileId);
  }
}

// vue加载
function vueOffceRendered(){
  drawerFileInfoNew.value.load = true;
}

// 改变文件属性抽屉大小，并触发窗口 resize 事件
function changeTagWinSize(s) {
  drawerFileProSize.value = s + '%';
  setTimeout(() => {
    const resizeEvent = new Event('resize');
    window.dispatchEvent(resizeEvent);
  }, 500);
}

// 当组件挂载完成后执行以下操作
onMounted(() => {
  // 获取当前窗口的高度，并计算各元素的高度
  // 表格高度，从窗口高度减去 210 像素
  tableHeight.value = (window.innerHeight - 210) + 'px';
  // 预览区域高度，从窗口高度减去 180 像素
  preHeight.value = (window.innerHeight - 180) + 'px';
  // 办公区域高度，从窗口高度减去 180 像素
  officeHeight.value = (window.innerHeight - 180) + 'px';
  // 动态区域高度，从窗口高度减去 210 像素（无单位）
  dynamicHeight.value = (window.innerHeight - 210);

  // 监听窗口大小变化事件，当窗口大小改变时更新各元素高度
  window.addEventListener('resize', () => {
    tableHeight.value = (window.innerHeight - 220) + 'px';
    dynamicHeight.value = (window.innerHeight - 210);
    preHeight.value = (window.innerHeight - 180) + 'px';
    officeHeight.value = (window.innerHeight - 180) + 'px';
  });
});

/***************************** 上传文件 ********************************/
// 控制文件上传对话框是否显示的响应式变量
const uploadDialogVisible = ref(false);

// 存储上传文件列表的响应式变量
const uploadFileList = ref([]);

// 获取文件上传的 URL，拼接当前磁盘目录的 ID
function fileDiskUploadUrl() {
  return fileUploadUrl() + '?dirId=' + nowDisk.value.fileId;
}

// 文件上传成功后的处理函数
const uploadSuccess = (file, uploadFiles) => {
  // 若文件上传返回的状态码不是 200，表示上传失败
  if (file.code !== '200') {
    // 显示错误消息
    ElMessage({
      message: file.msg,
      type: 'error',
      plain: true,
    });
    // 将上传文件状态标记为失败
    uploadFiles.status = 'fail';
    console.log(uploadFiles);
    return;
  }
  // 上传成功，重新获取文件列表
  getList();
};

// 文件上传失败后的处理函数
const uploadError = (file, uploadFiles) => {
  console.log(file, uploadFiles);
};

// 打开文件上传对话框
function openUpload() {
  uploadFileList.value = [];
  uploadDialogVisible.value = true;
}

/**************************  audio  *********************************/
// 存储音频列表的响应式变量
const audioList = ref([]);

// 音频播放前的处理函数
const handleBeforePlay = (next) => {
  // 可以在此处记录当前播放音频的名称
  // this.currentAudioName =
  //     this.audioList[this.$refs.audioPlayer.currentPlayIndex].title
  // 调用 next 函数开始播放音频
  next();
};

/************************* 请求 后台交互 ******************************/

// 获取文件列表的函数
function getList() {
  // 显示加载状态
  loading.value = true;
  // 调用 fileList 函数请求文件列表数据
  fileList(queryParams.value).then(response => {
    // 将响应中的文件数据存储到 fileDataList 中
    fileDataList.value = response.data.diskFileList;
    // 将响应中的文件目录数据存储到 fileDirList 中
    fileDirList.value = response.data.diskFileDirList;
    // 将响应中的当前磁盘信息存储到 nowDisk 中
    nowDisk.value = response.data.nowDisk;
    // 隐藏加载状态
    loading.value = false;
    // 清空当前选中行
    currentRow.value = ref({});
  });
}

// 根据搜索关键词获取文件列表的函数
function getSearchList() {
  // 将搜索关键词赋值给查询参数中的文件名
  queryParams.value.fileName = fileSearchWord.value;
  // 调用 getList 函数重新获取文件列表
  getList();
}

// 文件复制函数
function fileCopy(fileId, dirId) {
  // 构造文件复制所需的 JSON 数据
  var datajson = {"fileId":fileId,"dirId":dirId};
  // 显示加载状态
  loading.value = true;
  // 调用 filecp 函数进行文件复制操作
  filecp(datajson).then(response => {
    // 复制成功后，重新获取文件列表
    getList();
    // 清空选中文件
    selFile.value = null;
    // 重置文件操作类型
    fileOperType.value = 0;
  });
  // 隐藏加载状态
  loading.value = false;
}

// 文件移动函数
function fileMove(fileId, dirId) {
  // 构造文件移动所需的 JSON 数据
  var datajson = {"fileId":fileId,"dirId":dirId};
  // 显示加载状态
  loading.value = true;
  // 调用 filemv 函数进行文件移动操作
  filemv(datajson).then(response => {
    // 移动成功后，重新获取文件列表
    getList();
    // 重置文件操作类型
    fileOperType.value = 0;
    // 清空选中文件
    selFile.value = null;
  });
  // 隐藏加载状态
  loading.value = false;
}

// 文件重命名函数
function filerRename() {
  // 关闭重命名对话框
  renameDialogFormVisible.value = false;
  // 若新文件名为空，则不进行重命名操作
  if (!renameNewFileName) {
    return;
  }
  // 构造文件重命名所需的 JSON 数据
  var datajson = {"fileId":currentRow.value.fileId,"newName":renameNewFileName.value};
  // 显示加载状态
  loading.value = true;
  // 调用 filerename 函数进行文件重命名操作
  filerename(datajson).then(response => {
    // 重命名成功后，重新获取文件列表
    getList();
  });
  // 清空新文件名
  renameNewFileName.value = null;
  // 隐藏加载状态
  loading.value = false;
}

// 新建文件夹函数
function fileNewDir() {
  // 关闭新建文件夹对话框
  createNewDirDialogFormVisible.value = false;
  // 若新文件夹名称为空，则不进行新建操作
  if (!createNewDirName) {
    return;
  }
  // 构造新建文件夹所需的 JSON 数据
  var datajson = {"fileId":nowDisk.value.fileId,"newName":createNewDirName.value};
  // 显示加载状态
  loading.value = true;
  // 调用 fileCreateDir 函数进行新建文件夹操作
  fileCreateDir(datajson).then(response => {
    // 新建成功后，重新获取文件列表
    getList();
  });
  // 清空新文件夹名称
  createNewDirName.value = null;
  // 隐藏加载状态
  loading.value = false;
}

// 文件删除函数
function fileDelete(fileId) {
  // 构造文件删除所需的 JSON 数据
  var datajson = {"fileId":fileId};
  // 显示加载状态
  loading.value = true;
  // 调用 filedel 函数进行文件删除操作
  filedel(datajson).then(response => {
    // 从文件数据列表中移除被删除的文件
    for (let i = 0; i < fileDataList.value.length; i++) {
      if (fileDataList.value[i].fileId === fileId) {
        fileDataList.value.splice(i, 1);
      }
    }
    // 隐藏加载状态
    loading.value = false;
    // 显示文件删除成功的消息
    ElMessage({
      message: '文件删除成功',
      type: 'success',
    });
  });
}

// 文件下载函数
function fileBaseDownoad(fileId) {
  // 调用 fileDownload 函数进行文件下载
  fileDownload(fileId);
}

// 页面加载时获取文件列表
getList();

// 打开新窗口访问指定 URL
function goTarget(url) {
  window.open(url, '__blank')
}
</script>

<style scoped lang="scss">
:deep(.el-page-header__left){
  display: none;
}
.app-container{
  padding-bottom: 10px !important;
}
//:deep(pre){
//  background: #f6f6f6;
//}
:deep(.svg-inline--fa){
  font-size:20px;
  margin-right: 5px;
}
//:deep(pre code.hljs){
//  overflow: initial;
//}
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
//:deep(.line-numbers .line-numbers-rows){
//  left:-11.8em;
//}
:deep(.line-numbers){
  white-space: pre-line;
}
:deep(.wintag){
  margin:0px 2px;
}
</style>

