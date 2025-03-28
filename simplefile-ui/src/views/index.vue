<template>
  <div class="app-container home">

    <el-row>
      <el-col :span="24">
        <el-input
            v-model="input1"
            style="max-width: 600px;border-bottom-right-radius: 0px;border-right: 0px;"
            placeholder="存储位置"
        >
          <template #prepend>
            <el-button-group>
              <el-button type="primary">
                <el-icon class="el-icon--left"><ArrowLeft /></el-icon>
              </el-button>
              <el-button type="primary">
                <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
              <el-button type="primary">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </el-button-group>
          </template>
        </el-input>
        <el-input
            v-model="input3"
            style="max-width: 300px;float:right;"
            placeholder="请输入需要搜索的内容"
            class="input-with-select"
        >
<!--          <template #prepend>-->
<!--            搜索-->
<!--          </template>-->
          <template #append>
            <el-button :icon="Search" />
          </template>
        </el-input>
      </el-col>



    </el-row>

    <el-row style="margin-top: 10px;margin-bottom: 10px;">
      <el-button-group class="ml-4">
        <el-button type="primary" :icon="Upload">上传文件</el-button>
<!--        <el-button type="primary" :icon="Share" />-->
<!--        <el-button type="primary" :icon="Delete" />-->
      </el-button-group>
    </el-row>

    <el-row>
      <el-table :data="tableData" style="width: 100%" height="250">
        <el-table-column fixed prop="date" label="文件名称" width="" />
        <el-table-column prop="name" label="类型" width="150" />
        <el-table-column prop="state" label="大小" width="150" />
        <el-table-column prop="city" label="修改时间" width="200" />
        <el-table-column prop="address" label="创建时间" width="200" />
      </el-table>
    </el-row>

  </div>
</template>

<script setup name="Index">
import {
  Search,
  ArrowLeft,
  ArrowRight,
  Delete,
  Upload,
  Edit,
  Share,
} from '@element-plus/icons-vue'

const version = ref('3.8.8')

const tableData = [
  {
    date: '2016-05-03',
    name: 'Tom',
    state: 'California',
    city: 'Los Angeles',
    address: 'No. 189, Grove St, Los Angeles',
    zip: 'CA 90036',
  },
  {
    date: '2016-05-02',
    name: 'Tom',
    state: 'California',
    city: 'Los Angeles',
    address: 'No. 189, Grove St, Los Angeles',
    zip: 'CA 90036',
  }
]

function goTarget(url) {
  window.open(url, '__blank')
}
</script>

<style scoped lang="scss">
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

