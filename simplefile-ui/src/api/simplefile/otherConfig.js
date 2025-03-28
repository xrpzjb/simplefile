import request from '@/utils/request'

// 获取其他配置
export function getOtherConfig() {
  return request({
    url: '/simplefile/config/otherConfig/',
    method: 'get'
  })
}

// 更新其他配置
export function setOtherConfig(data) {
  return request({
    url: '/simplefile/config/otherConfig',
    method: 'post',
    data: data
  })
}

