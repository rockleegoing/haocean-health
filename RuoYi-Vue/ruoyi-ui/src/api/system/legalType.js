import request from '@/utils/request'

// 查询法律类型列表
export function listLegalType(query) {
  return request({
    url: '/system/legal/type/list',
    method: 'get',
    params: query
  })
}

// 获取所有法律类型
export function allLegalType() {
  return request({
    url: '/system/legal/type/all',
    method: 'get'
  })
}

// 获取法律类型详情
export function getLegalType(typeId) {
  return request({
    url: '/system/legal/type/' + typeId,
    method: 'get'
  })
}

// 新增法律类型
export function addLegalType(data) {
  return request({
    url: '/system/legal/type',
    method: 'post',
    data: data
  })
}

// 修改法律类型
export function editLegalType(data) {
  return request({
    url: '/system/legal/type',
    method: 'put',
    data: data
  })
}

// 删除法律类型
export function delLegalType(typeIds) {
  return request({
    url: '/system/legal/type/' + typeIds,
    method: 'delete'
  })
}

// 导出法律类型
export function exportLegalType(query) {
  return request({
    url: '/system/legal/type/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
