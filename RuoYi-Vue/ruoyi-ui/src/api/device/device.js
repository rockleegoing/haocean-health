import request from '@/utils/request'

// 查询设备列表
export function listDevice(query) {
  return request({
    url: '/device/device/list',
    method: 'get',
    params: query
  })
}

// 查询设备详细
export function getDevice(id) {
  return request({
    url: '/device/device/' + id,
    method: 'get'
  })
}

// 解绑设备
export function unbindDevice(id) {
  return request({
    url: '/device/device/unbind/' + id,
    method: 'put'
  })
}

// 删除设备
export function delDevice(id) {
  return request({
    url: '/device/device/' + id,
    method: 'delete'
  })
}

// 批量删除设备
export function batchDelDevice(ids) {
  return request({
    url: '/device/device/batch',
    method: 'delete',
    data: ids
  })
}
