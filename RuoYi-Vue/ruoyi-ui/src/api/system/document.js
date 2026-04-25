import request from '@/utils/request'

// ==================== 文书模板管理 API ====================

// 查询文书模板列表
export function listTemplate(query) {
  return request({
    url: '/api/admin/document/template/list',
    method: 'get',
    params: query
  })
}

// 查询文书模板详细
export function getTemplate(id) {
  return request({
    url: '/api/admin/document/template/' + id,
    method: 'get'
  })
}

// 新增文书模板
export function addTemplate(data) {
  return request({
    url: '/api/admin/document/template',
    method: 'post',
    data: data
  })
}

// 修改文书模板
export function updateTemplate(data) {
  return request({
    url: '/api/admin/document/template',
    method: 'put',
    data: data
  })
}

// 删除文书模板
export function delTemplate(id) {
  return request({
    url: '/api/admin/document/template/' + id,
    method: 'delete'
  })
}

// ==================== 文书模板变量管理 API ====================

// 新增文书模板变量
export function addVariable(data) {
  return request({
    url: '/api/admin/document/variable',
    method: 'post',
    data: data
  })
}

// 修改文书模板变量
export function updateVariable(data) {
  return request({
    url: '/api/admin/document/variable',
    method: 'put',
    data: data
  })
}

// 删除文书模板变量
export function delVariable(id) {
  return request({
    url: '/api/admin/document/variable/' + id,
    method: 'delete'
  })
}

// ==================== 文书套组管理 API ====================

// 查询文书套组列表
export function listGroup(query) {
  return request({
    url: '/api/admin/document/group/list',
    method: 'get',
    params: query
  })
}

// 兼容别名
export const listDocumentGroup = listGroup

// 查询文书套组详细
export function getGroup(id) {
  return request({
    url: '/api/admin/document/group/' + id,
    method: 'get'
  })
}

// 兼容别名
export const getDocumentGroup = getGroup

// 新增文书套组
export function addGroup(data) {
  return request({
    url: '/api/admin/document/group',
    method: 'post',
    data: data
  })
}

// 修改文书套组
export function updateGroup(data) {
  return request({
    url: '/api/admin/document/group',
    method: 'put',
    data: data
  })
}

// 删除文书套组
export function delGroup(id) {
  return request({
    url: '/api/admin/document/group/' + id,
    method: 'delete'
  })
}

// ==================== 文书记录管理 API ====================

// 查询文书记录列表
export function listRecord(query) {
  return request({
    url: '/api/admin/document/record/list',
    method: 'get',
    params: query
  })
}

// 查询文书记录详细
export function getRecord(id) {
  return request({
    url: '/api/admin/document/record/' + id,
    method: 'get'
  })
}

// 新增文书记录
export function addRecord(data) {
  return request({
    url: '/api/admin/document/record',
    method: 'post',
    data: data
  })
}

// 修改文书记录
export function updateRecord(data) {
  return request({
    url: '/api/admin/document/record',
    method: 'put',
    data: data
  })
}

// 删除文书记录
export function delRecord(id) {
  return request({
    url: '/api/admin/document/record/' + id,
    method: 'delete'
  })
}
