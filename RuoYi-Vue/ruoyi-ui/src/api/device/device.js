import request from '@/utils/request'

// 查询设备列表
export function listDevice(query) {
  return request({
    url: '/device/device/list',
    method: 'post',
    data: query
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
export function unbindDevice(deviceId) {
  return request({
    url: '/device/device/unbind',
    method: 'put',
    data: { deviceId: deviceId }
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
    url: '/device/device/' + ids,
    method: 'delete'
  })
}

// 远程清除设备数据
export function remoteWipeDevice(deviceUuid) {
  return request({
    url: '/device/device/remoteWipe',
    method: 'post',
    data: { deviceUuid: deviceUuid }
  })
}
