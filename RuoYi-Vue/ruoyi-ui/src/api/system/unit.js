import request from '@/utils/request'

// 查询执法单位列表
export function listUnit(query) {
  return request({
    url: '/system/unit/list',
    method: 'get',
    params: query
  })
}

// 查询执法单位详细
export function getUnit(unitId) {
  return request({
    url: '/system/unit/' + unitId,
    method: 'get'
  })
}

// 新增执法单位
export function addUnit(data) {
  return request({
    url: '/system/unit',
    method: 'post',
    data: data
  })
}

// 修改执法单位
export function updateUnit(data) {
  return request({
    url: '/system/unit',
    method: 'put',
    data: data
  })
}

// 删除执法单位
export function delUnit(unitId) {
  return request({
    url: '/system/unit/' + unitId,
    method: 'delete'
  })
}
