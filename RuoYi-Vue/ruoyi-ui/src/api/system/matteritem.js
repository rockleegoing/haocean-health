import request from '@/utils/request'

// 查询监管事项详情列表
export function listMatteritem(query) {
  return request({
    url: '/system/matteritem/list',
    method: 'get',
    params: query
  })
}

// 查询监管事项详情详细
export function getMatteritem(id) {
  return request({
    url: '/system/matteritem/' + id,
    method: 'get'
  })
}

// 新增监管事项详情
export function addMatteritem(data) {
  return request({
    url: '/system/matteritem',
    method: 'post',
    data: data
  })
}

// 修改监管事项详情
export function updateMatteritem(data) {
  return request({
    url: '/system/matteritem',
    method: 'put',
    data: data
  })
}

// 删除监管事项详情
export function delMatteritem(id) {
  return request({
    url: '/system/matteritem/' + id,
    method: 'delete'
  })
}
