import request from '@/utils/request'

// 查询规范用语与监管事项绑定关系列表
export function listNormativematterbind(query) {
  return request({
    url: '/system/normativematterbind/list',
    method: 'get',
    params: query
  })
}

// 查询规范用语与监管事项绑定关系详细
export function getNormativematterbind(params) {
  return request({
    url: '/system/normativematterbind/',
    method: 'get',
    params: params
  })
}

// 新增规范用语与监管事项绑定关系
export function addNormativematterbind(data) {
  return request({
    url: '/system/normativematterbind',
    method: 'post',
    data: data
  })
}

// 删除规范用语与监管事项绑定关系
export function delNormativematterbind(params) {
  return request({
    url: '/system/normativematterbind',
    method: 'delete',
    params: params
  })
}

// 查询所有已绑定的事项列表（用于按事项视图，不受分页限制）
export function listBoundMatter() {
  return request({
    url: '/system/normativematterbind/boundMatterList',
    method: 'get'
  })
}

// 查询所有已绑定的规范用语列表（用于按规范用语视图，不受分页限制）
export function listBoundNormative() {
  return request({
    url: '/system/normativematterbind/boundNormativeList',
    method: 'get'
  })
}

// 查询监管事项列表
export function listMatter(query) {
  return request({
    url: '/system/matter/list',
    method: 'get',
    params: query
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
