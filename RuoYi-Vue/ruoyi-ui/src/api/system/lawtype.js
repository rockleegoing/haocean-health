import request from '@/utils/request'

// 查询法律法规类型列表
export function listLawtype(query) {
  return request({
    url: '/system/lawtype/list',
    method: 'get',
    params: query
  })
}

// 查询法律法规类型详细
export function getLawtype(id) {
  return request({
    url: '/system/lawtype/' + id,
    method: 'get'
  })
}

// 新增法律法规类型
export function addLawtype(data) {
  return request({
    url: '/system/lawtype',
    method: 'post',
    data: data
  })
}

// 修改法律法规类型
export function updateLawtype(data) {
  return request({
    url: '/system/lawtype',
    method: 'put',
    data: data
  })
}

// 删除法律法规类型
export function delLawtype(id) {
  return request({
    url: '/system/lawtype/' + id,
    method: 'delete'
  })
}

// 获取法律类型树
export function treeList(query) {
  return request({
    url: '/system/lawtype/treeList',
    method: 'get',
    params: query
  })
}
