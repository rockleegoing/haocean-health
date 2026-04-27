import request from '@/utils/request'

// 查询监管类型列表
export function listSupervisionType(query) {
  return request({
    url: '/system/supervision/type/list',
    method: 'get',
    params: query
  })
}

// 获取所有监管类型
export function allSupervisionType() {
  return request({
    url: '/system/supervision/type/all',
    method: 'get'
  })
}

// 获取监管类型详情
export function getSupervisionType(typeId) {
  return request({
    url: '/system/supervision/type/' + typeId,
    method: 'get'
  })
}

// 新增监管类型
export function addSupervisionType(data) {
  return request({
    url: '/system/supervision/type',
    method: 'post',
    data: data
  })
}

// 修改监管类型
export function editSupervisionType(data) {
  return request({
    url: '/system/supervision/type',
    method: 'put',
    data: data
  })
}

// 删除监管类型
export function delSupervisionType(typeIds) {
  return request({
    url: '/system/supervision/type/' + typeIds,
    method: 'delete'
  })
}

// 导出监管类型
export function exportSupervisionType(query) {
  return request({
    url: '/system/supervision/type/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
