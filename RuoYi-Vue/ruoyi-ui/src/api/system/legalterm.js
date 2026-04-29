import request from '@/utils/request'

// 查询法律条款列表
export function listLegalterm(query) {
  return request({
    url: '/system/legalterm/list',
    method: 'get',
    params: query
  })
}

// 查询法律条款详细
export function getLegalterm(id) {
  return request({
    url: '/system/legalterm/' + id,
    method: 'get'
  })
}

// 新增法律条款
export function addLegalterm(data) {
  return request({
    url: '/system/legalterm',
    method: 'post',
    data: data
  })
}

// 修改法律条款
export function updateLegalterm(data) {
  return request({
    url: '/system/legalterm',
    method: 'put',
    data: data
  })
}

// 删除法律条款
export function delLegalterm(id) {
  return request({
    url: '/system/legalterm/' + id,
    method: 'delete'
  })
}
