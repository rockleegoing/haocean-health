import request from '@/utils/request'

// 查询激活码列表
export function listActivationCode(query) {
  return request({
    url: '/device/activationCode/list',
    method: 'post',
    params: query
  })
}

// 查询激活码详细
export function getActivationCode(id) {
  return request({
    url: '/device/activationCode/' + id,
    method: 'get'
  })
}

// 批量生成激活码
export function generateActivationCode(data) {
  return request({
    url: '/device/activationCode/batchGenerate',
    method: 'post',
    data: data
  })
}

// 删除激活码
export function delActivationCode(id) {
  return request({
    url: '/device/activationCode/' + id,
    method: 'delete'
  })
}

// 批量删除激活码
export function batchDelActivationCode(ids) {
  return request({
    url: '/device/activationCode/' + ids,
    method: 'delete',
    data: ids
  })
}
