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
export function getRegulatorycategorybind(matterId) {
  return request({
    url: '/system/regulatorycategorybind/' + matterId,
    method: 'get'
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

// 修改监管事项执法分类绑定关系
export function updateRegulatorycategorybind(data) {
  return request({
    url: '/system/regulatorycategorybind',
    method: 'put',
    data: data
  })
}

// 删除监管事项执法分类绑定关系
export function delRegulatorycategorybind(matterId) {
  return request({
    url: '/system/regulatorycategorybind/' + matterId,
    method: 'delete'
  })
}
