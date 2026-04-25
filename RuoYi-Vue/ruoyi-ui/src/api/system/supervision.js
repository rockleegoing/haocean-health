import request from '@/utils/request'

// ==================== 监管事项管理 API ====================

// 查询监管事项列表
export function listSupervision(query) {
  return request({
    url: '/api/admin/supervision/list',
    method: 'get',
    params: query
  })
}

// 查询监管事项详细
export function getSupervision(itemId) {
  return request({
    url: '/api/admin/supervision/detail/' + itemId,
    method: 'get'
  })
}

// 新增监管事项
export function addSupervision(data) {
  return request({
    url: '/api/admin/supervision',
    method: 'post',
    data: data
  })
}

// 修改监管事项
export function updateSupervision(data) {
  return request({
    url: '/api/admin/supervision',
    method: 'put',
    data: data
  })
}

// 删除监管事项
export function delSupervision(itemId) {
  return request({
    url: '/api/admin/supervision/' + itemId,
    method: 'delete'
  })
}

// ==================== 监管类型管理 API ====================

// 查询监管类型列表
export function listCategory(query) {
  return request({
    url: '/api/admin/supervision/category/list',
    method: 'get',
    params: query
  })
}

// 查询监管类型详细
export function getCategory(categoryId) {
  return request({
    url: '/api/admin/supervision/category/detail/' + categoryId,
    method: 'get'
  })
}

// 新增监管类型
export function addCategory(data) {
  return request({
    url: '/api/admin/supervision/category',
    method: 'post',
    data: data
  })
}

// 修改监管类型
export function updateCategory(data) {
  return request({
    url: '/api/admin/supervision/category',
    method: 'put',
    data: data
  })
}

// 删除监管类型
export function delCategory(categoryId) {
  return request({
    url: '/api/admin/supervision/category/' + categoryId,
    method: 'delete'
  })
}

// ==================== 关联管理 API ====================

// 添加规范用语关联
export function addLanguageLink(itemId, languageId) {
  return request({
    url: '/api/admin/supervision/language-link',
    method: 'post',
    params: { itemId, languageId }
  })
}

// 删除规范用语关联
export function delLanguageLink(linkId) {
  return request({
    url: '/api/admin/supervision/language-link/' + linkId,
    method: 'delete'
  })
}

// 添加法律法规关联
export function addRegulationLink(itemId, regulationId) {
  return request({
    url: '/api/admin/supervision/regulation-link',
    method: 'post',
    params: { itemId, regulationId }
  })
}

// 删除法律法规关联
export function delRegulationLink(linkId) {
  return request({
    url: '/api/admin/supervision/regulation-link/' + linkId,
    method: 'delete'
  })
}

// ==================== 移动端 API ====================

// 获取监管事项首页数据
export function getHomeData() {
  return request({
    url: '/api/supervision/home',
    method: 'get'
  })
}

// 获取监管类型列表
export function listAllCategory() {
  return request({
    url: '/api/supervision/category/all',
    method: 'get'
  })
}

// 获取监管子项列表
export function listItems(query) {
  return request({
    url: '/api/supervision/item-list',
    method: 'get',
    params: query
  })
}

// 根据父级ID获取子事项
export function listItemsByParentId(parentId) {
  return request({
    url: '/api/supervision/item-children/' + parentId,
    method: 'get'
  })
}

// 根据监管类型获取事项列表
export function listItemsByCategoryId(categoryId) {
  return request({
    url: '/api/supervision/item-list/category/' + categoryId,
    method: 'get'
  })
}

// 获取监管事项详情
export function getItemDetail(itemId) {
  return request({
    url: '/api/supervision/item-detail/' + itemId,
    method: 'get'
  })
}

// 搜索监管事项
export function searchItems(keyword, categoryId) {
  return request({
    url: '/api/supervision/search',
    method: 'get',
    params: { keyword, categoryId }
  })
}

// 获取监管事项关联的规范用语
export function getLanguageLinks(itemId) {
  return request({
    url: '/api/supervision/language-link/' + itemId,
    method: 'get'
  })
}

// 获取监管事项关联的法律法规
export function getRegulationLinks(itemId) {
  return request({
    url: '/api/supervision/regulation-link/' + itemId,
    method: 'get'
  })
}
