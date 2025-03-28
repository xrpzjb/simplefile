import request from '@/utils/request'

// 查询文件映射列表
export function listPoint(query) {
  return request({
    url: '/set/point/list',
    method: 'get',
    params: query
  })
}

// 查询文件映射详细
export function getPoint(pointId) {
  return request({
    url: '/set/point/' + pointId,
    method: 'get'
  })
}

// 文件全量扫描
export function pointScannAll(pointId) {
  return request({
    url: '/set/point/scannAll/' + pointId,
    method: 'get'
  })
}

// 文件增量扫描
export function pointScannUpdate(pointId) {
  return request({
    url: '/set/point/scannUpdate/' + pointId,
    method: 'get'
  })
}

// 新增文件映射
export function addPoint(data) {
  return request({
    url: '/set/point',
    method: 'post',
    data: data
  })
}

// 修改文件映射
export function updatePoint(data) {
  return request({
    url: '/set/point',
    method: 'put',
    data: data
  })
}

// 删除文件映射
export function delPoint(pointId) {
  return request({
    url: '/set/point/' + pointId,
    method: 'delete'
  })
}
