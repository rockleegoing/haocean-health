import request from '@/utils/request'

// 查询监管事项列表
export function listMatter(query) {
  return request({
    url: '/system/matter/list',
    method: 'get',
    params: query
  })
}

// 查询监管事项详细
export function getMatter(id) {
  return request({
    url: '/system/matter/' + id,
    method: 'get'
  })
}

// 新增监管事项
export function addMatter(data) {
  return request({
    url: '/system/matter',
    method: 'post',
    data: data
  })
}

// 修改监管事项
export function updateMatter(data) {
  return request({
    url: '/system/matter',
    method: 'put',
    data: data
  })
}

// 删除监管事项
export function delMatter(id) {
  return request({
    url: '/system/matter/' + id,
    method: 'delete'
  })
}
