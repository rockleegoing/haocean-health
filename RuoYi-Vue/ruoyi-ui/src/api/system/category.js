import request from '@/utils/request'

// 查询行业分类列表
export function listCategory(query) {
  return request({
    url: '/system/category/list',
    method: 'get',
    params: query
  })
}

// 查询行业分类详细
export function getCategory(categoryId) {
  return request({
    url: '/system/category/' + categoryId,
    method: 'get'
  })
}

// 新增行业分类
export function addCategory(data) {
  return request({
    url: '/system/category',
    method: 'post',
    data: data
  })
}

// 修改行业分类
export function updateCategory(data) {
  return request({
    url: '/system/category',
    method: 'put',
    data: data
  })
}

// 删除行业分类
export function delCategory(categoryId) {
  return request({
    url: '/system/category/' + categoryId,
    method: 'delete'
  })
}
