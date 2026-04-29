import request from '@/utils/request'

// 查询规范用语类别列表
export function listNormativecategory(query) {
  return request({
    url: '/system/normativecategory/list',
    method: 'get',
    params: query
  })
}

// 查询规范用语类别详细
export function getNormativecategory(code) {
  return request({
    url: '/system/normativecategory/' + code,
    method: 'get'
  })
}

// 新增规范用语类别
export function addNormativecategory(data) {
  return request({
    url: '/system/normativecategory',
    method: 'post',
    data: data
  })
}

// 修改规范用语类别
export function updateNormativecategory(data) {
  return request({
    url: '/system/normativecategory',
    method: 'put',
    data: data
  })
}

// 删除规范用语类别
export function delNormativecategory(code) {
  return request({
    url: '/system/normativecategory/' + code,
    method: 'delete'
  })
}
