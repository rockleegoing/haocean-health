import request from '@/utils/request'

// 获取规范用语分类列表（所有分类，用于联动下拉）
export function listCategory() {
  return request({
    url: '/system/normativecategory/list',
    method: 'get',
    params: { pageNum: 1, pageSize: 9999 }
  })
}

// 查询规范用语列表
export function listLanguage(query) {
  return request({
    url: '/system/language/list',
    method: 'get',
    params: query
  })
}

// 查询规范用语详细
export function getLanguage(id) {
  return request({
    url: '/system/language/' + id,
    method: 'get'
  })
}

// 新增规范用语
export function addLanguage(data) {
  return request({
    url: '/system/language',
    method: 'post',
    data: data
  })
}

// 修改规范用语
export function updateLanguage(data) {
  return request({
    url: '/system/language',
    method: 'put',
    data: data
  })
}

// 删除规范用语
export function delLanguage(id) {
  return request({
    url: '/system/language/' + id,
    method: 'delete'
  })
}
