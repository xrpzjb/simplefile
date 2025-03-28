import request from '@/utils/request'

// 分享详情
export function shareDetail(data) {
  return request({
    url: '/simplefile/share/detail/' + data,
    method: 'post',
    data: data
  })
}

// 文件列表
export function shareList(token, data) {
  return request({
    url: '/simplefile/share/list/' + token,
    method: 'post',
    data: data
  })
}

// 分享验证
export function shareVerify(shareCode, data) {
  return request({
    url: '/simplefile/share/verify/' + shareCode,
    method: 'post',
    data: data
  })
}









