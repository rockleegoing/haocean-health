import request from '@/utils/request'

// 查询法律目录列表
export function listLaw(query) {
  return request({
    url: '/system/law/list',
    method: 'get',
    params: query
  })
}

// 查询法律目录详细
export function getLaw(id) {
  return request({
    url: '/system/law/' + id,
    method: 'get'
  })
}

// 新增法律目录
export function addLaw(data) {
  return request({
    url: '/system/law',
    method: 'post',
    data: data
  })
}

// 修改法律目录
export function updateLaw(data) {
  return request({
    url: '/system/law',
    method: 'put',
    data: data
  })
}

// 删除法律目录
export function delLaw(id) {
  return request({
    url: '/system/law/' + id,
    method: 'delete'
  })
}
