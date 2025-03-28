<template>
  <div class="app-container">

    <el-tabs v-model="activeName" class="demo-tabs" @tab-click="tabHandleClick" @tab-change="tabChangeClick">
      <el-tab-pane label="存储位置" name="first">

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
                type="primary"
                plain
                icon="Plus"
                @click="pathHandleAdd"
            >新增</el-button>
          </el-col>
        </el-row>

        <el-table :data="pathTableList" stripe style="width: 100%" v-loading="loading">
          <el-table-column prop="systemPath" label="物理路径" />
          <el-table-column prop="pointPath" label="映射路径" />
          <el-table-column prop="scannStatus" label="扫描状态">
            <template #default="scope">
              <dict-tag :options="service_scann_status" :value="scope.row.scannStatus" />
            </template>
          </el-table-column>
          <el-table-column prop="scannTime" label="最后扫描时间" />
          <el-table-column label="操作" align="center" width="250" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" >
                <el-button link type="primary" icon="Edit" @click="pathHandleUpdate(scope.row)" ></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" >
                <el-button link type="primary" icon="Delete" @click="pathHandleDelete(scope.row)" ></el-button>
              </el-tooltip>
              <el-tooltip content="全部重新扫描" placement="top" >
                <el-button link type="primary" icon="DocumentChecked" @click="pathHandleScannAll(scope.row)" >全量扫描</el-button>
              </el-tooltip>
              <el-tooltip content="更新扫描" placement="top" >
                <el-button link type="primary" icon="DocumentChecked" @click="pathHandleScannUpdate(scope.row)" >增量扫描</el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>

        <!-- 添加或修改文件映射对话框 -->
        <el-dialog :title="pathTitle" v-model="pathOpen" width="500px" append-to-body>
          <el-form ref="pointRef" :model="pathForm"  label-width="80px">
            <el-form-item label="系统路径" prop="systemPath">
              <el-input v-model="pathForm.systemPath" type="text" placeholder="请输入系统路径" />
            </el-form-item>
            <el-form-item label="映射路径" prop="pointPath">
              <el-input v-model="pathForm.pointPath" type="text" placeholder="请输入映射路径" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="dialog-footer">
              <el-button type="primary" @click="pathSubmitForm">确 定</el-button>
              <el-button @click="pathCancel">取 消</el-button>
            </div>
          </template>
        </el-dialog>

      </el-tab-pane>
      <el-tab-pane label="任务" name="second">定时任务</el-tab-pane>
      <el-tab-pane label="其他" name="third" style="" >
        <div style="width: 400px;">
          <el-form ref="configRef" :model="configForm"  label-width="120px" style="" class="configForm">
            <el-form-item label="回收站保留天数" prop="recycleDay">
              <el-input v-model="configForm.recycleDay" type="number" placeholder="请输入天数" />
            </el-form-item>
          </el-form>
          <el-form ref="configRef" :model="configForm"  label-width="120px" style="" class="configForm">
            <el-form-item label="网站地址" prop="siteAddress">
              <el-input v-model="configForm.siteAddress" type="text" placeholder="请输入网站地址" />
            </el-form-item>
          </el-form>
          <el-affix :offset="20" position="top"  style="text-align: right;" target=".configForm">
            <el-button type="primary" @click="fileSetOtherConfig">保存设置</el-button>
          </el-affix>
        </div>

      </el-tab-pane>
    </el-tabs>



  </div>
</template>

<script setup name="setting">
import { ref } from 'vue'
import {
  listPoint,
  getPoint,
  delPoint,
  addPoint,
  updatePoint,
  pointScannAll,
  pointScannUpdate
} from "@/api/simplefile/point.js";
import {getOtherConfig,setOtherConfig} from "@/api/simplefile/otherConfig.js";
import {get} from "@vueuse/core";

// 定义变量
const { proxy } = getCurrentInstance();
const loading = ref(true);

const activeName = ref('first')

// 数据字典
const { service_scann_status } = proxy.useDict("service_scann_status");

// <=================== 总体start ======================>
const tabHandleClick = (tab, event) => {
  console.log(tab, event)
}
const tabChangeClick = (tab) => {
  console.log(tab)
  if(tab == 'third'){
    fileGetOtherConfig();
  }
  if(tab == 'first'){
    getPathList();
  }
}
// <=================== 总体end =======================>

// <=================== 其他设置start ================>
const configForm = ref({});
function fileGetOtherConfig() {
  getOtherConfig().then(response => {
    configForm.value = response.data;
  });
}
function fileSetOtherConfig(){
  setOtherConfig(configForm.value).then(response => {
    if(response.code == '200'){
      proxy.$modal.msgSuccess("设置成功");
    }
  });
}
// <=================== 其他设置end ==================>

// <=================== 存储位置映射路径start ==========>
const pathOpen = ref(false);
const pathTitle = ref("");
const pathForm = ref({});

const pathTableList = ref([]);
function pathHandleAdd() {
  pathReset();
  pathOpen.value = true;
  pathTitle.value = "新增文件映射";
}
function getPathList() {
  loading.value = true;
  listPoint().then(response => {
    pathTableList.value = response.rows;
    loading.value = false;
  });
}
function pathReset() {
  pathForm.value = {
    pointId: null,
    systemPath: null,
    pointPath: null,
    scannTime: null,
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null
  };
  proxy.resetForm("pointRef");
}
/** 提交按钮 */
function pathSubmitForm() {
  if (pathForm.value.pointId != null) {
    updatePoint(pathForm.value).then(response => {
      proxy.$modal.msgSuccess("修改成功");
      pathOpen.value = false;
      getPathList();
    });
  } else {
    addPoint(pathForm.value).then(response => {
      proxy.$modal.msgSuccess("新增成功");
      pathOpen.value = false;
      getPathList();
    });
  }
}
/** 取消按钮 */
function pathCancel() {
  pathOpen.value = false;
  pathReset();
};
function pathHandleUpdate(row) {
  pathReset();
  const _pointId = row.pointId || ids.value
  getPoint(_pointId).then(response => {
    pathForm.value = response.data;
    pathOpen.value = true;
    pathTitle.value = "修改文件映射";
  });
}
function pathHandleDelete(row) {
  const _pointIds = row.pointId || ids.value;
  proxy.$modal.confirm('是否确认删除文件映射"' + row.systemPath + '"的数据项？[!!相应的文件管理数据会删除!!!]').then(function() {
    return delPoint(_pointIds);
  }).then(() => {
    getPathList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}
function pathHandleScannAll(row){
  proxy.$modal.confirm('是否确认进行全量文件扫描路径"' + row.systemPath + '"的数据项？[全部重新进行扫描]').then(function() {
    pointScannAll(row.pointId).then(response => {
      proxy.$modal.msgSuccess("操作成功，等待后台扫描完成");
    });
  }).then(() => {
  }).catch(() => {});
}
function pathHandleScannUpdate(row){
  proxy.$modal.confirm('是否确认进行增量文件扫描路径"' + row.systemPath + '"的数据项？[扫描新增或者修改的文件]').then(function() {
    pointScannUpdate(row.pointId).then(response => {
      proxy.$modal.msgSuccess("操作成功，等待后台扫描完成");
    });
  }).then(() => {
  }).catch(() => {});
}

getPathList();
// <==============存储位置映射路径end ======================>

</script>

<style scoped lang="scss">

.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}

</style>

