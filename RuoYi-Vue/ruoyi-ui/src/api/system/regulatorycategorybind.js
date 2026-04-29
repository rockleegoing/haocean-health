import request from '@/utils/request'

// 查询监管事项执法分类绑定关系列表
export function listRegulatorycategorybind(query) {
  return request({
    url: '/system/regulatorycategorybind/list',
    method: 'get',
    params: query
  })
}

// 查询监管事项执法分类绑定关系详细
export function getRegulatorycategorybind(params) {
  return request({
    url: '/system/regulatorycategorybind/',
    method: 'get',
    params: params
  })
}

// 新增监管事项执法分类绑定关系
export function addRegulatorycategorybind(data) {
  return request({
    url: '/system/regulatorycategorybind',
    method: 'post',
    data: data
  })
}

// 删除监管事项执法分类绑定关系
export function delRegulatorycategorybind(params) {
  return request({
    url: '/system/regulatorycategorybind',
    method: 'delete',
    params: params
  })
}

// 查询所有已绑定的事项列表（用于按事项视图，不受分页限制）
export function listBoundMatter() {
  return request({
    url: '/system/regulatorycategorybind/boundMatterList',
    method: 'get'
  })
}

// 查询行业分类列表
export function listCategory(query) {
  return request({
    url: '/system/category/list',
    method: 'get',
    params: query
  })
}
