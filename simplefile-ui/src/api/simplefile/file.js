import request from '@/utils/request'

// 查询文件列表
export function fileList(data) {
  return request({
    url: '/simplefile/file/list',
    method: 'post',
    data: data
  })
}
// 回收站文件列表
export function fileRecoveryList(data) {
  return request({
    url: '/simplefile/file/recoverylist',
    method: 'post',
    data: data
  })
}
// 文件移动
export function filemv(data) {
  return request({
    url: '/simplefile/file/mv',
    method: 'post',
    data: data
  })
}
// 文件复制
export function filecp(data) {
  return request({
    url: '/simplefile/file/copy',
    method: 'post',
    data: data
  })
}
// 文件删除
export function filedel(data) {
  return request({
    url: '/simplefile/file/del',
    method: 'post',
    data: data
  })
}
// 回收站删除
export function fileDiskDel(data) {
  return request({
    url: '/simplefile/file/diskDel',
    method: 'post',
    data: data
  })
}
// 回收站全部删除
export function fileDiskDelAll() {
  return request({
    url: '/simplefile/file/diskDelAll',
    method: 'post'
  })
}
// 文件删除还原
export function fileDiskRestore(data) {
  return request({
    url: '/simplefile/file/diskRestore',
    method: 'post',
    data: data
  })
}
export function filePreview(fileId, type) {
  return import.meta.env.VITE_APP_BASE_API + '/simplefile/file/preview/' + fileId + '/' + type + '?_=' + Math.random();
}
export function filePreviewUrls(fileId, type, pages) {
  let urls = [];
  var i = 0;
  while(i < pages ){
    urls.push(import.meta.env.VITE_APP_BASE_API + '/simplefile/file/preview/' + fileId + '/' + type + '?_=' + Math.random() + '&current=' + (i+1));
    i++;
  }
  return urls;
}
// 文件重命名
export function filerename(data) {
  return request({
    url: '/simplefile/file/rename',
    method: 'post',
    data: data
  })
}
// 新建文件夹
export function fileCreateDir(data) {
  return request({
    url: '/simplefile/file/createDir',
    method: 'post',
    data: data
  })
}
// 文件总量统计
export function fileCount(data) {
  return request({
    url: '/simplefile/file/count',
    method: 'post',
    data: data
  })
}
// 文件详情
export function fileDetail(data) {
  return request({
    url: '/simplefile/file/get?fileId=' + data,
    method: 'get'
  })
}
// 压缩文件显示
export function fileZipDetail(data) {
  return request({
    url: '/simplefile/file/zipget?fileId=' + data,
    method: 'get'
  })
}
// 解压文件
export function fileUnzipZip(data) {
  return request({
    url: '/simplefile/file/unzip?fileId=' + data,
    method: 'get',
    timeout: 1000 * 60 * 5
  })
}
// 分享
export function fileShare(data) {
  return request({
    url: '/simplefile/file/share',
    method: 'post',
    data: data
  })
}
// 分享详情
export function fileShareDetail(data) {
  return request({
    url: '/simplefile/file/shareDetail?fileId=' + data,
    method: 'get'
  })
}
// 文本内容
export function fileTxtContent(fileId) {
  return request({
    url: '/simplefile/file/preview/'+fileId+'/m',
    method: 'get',
    timeout: 60000,
    responseType: 'text'
  })
}
// 文件下载
export function fileDownload(fileId) {
  window.open(import.meta.env.VITE_APP_BASE_API + '/simplefile/file/download/'+fileId, '_blank');
}

export function fileUploadUrl() {
  return import.meta.env.VITE_APP_BASE_API + '/simplefile/file/upload';
}

// 判断文件类型
export function getFileType() {
  return import.meta.env.VITE_APP_BASE_API + '/simplefile/file/upload';
}