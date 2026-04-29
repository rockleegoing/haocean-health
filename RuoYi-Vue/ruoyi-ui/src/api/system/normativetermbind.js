import request from '@/utils/request'

// 查询规范用语法律条款关联列表
export function listNormativetermbind(query) {
  return request({
    url: '/system/normativetermbind/list',
    method: 'get',
    params: query
  })
}

// 查询规范用语法律条款关联详细
export function getNormativetermbind(legalTermId) {
  return request({
    url: '/system/normativetermbind/' + legalTermId,
    method: 'get'
  })
}

// 新增规范用语法律条款关联
export function addNormativetermbind(data) {
  return request({
    url: '/system/normativetermbind',
    method: 'post',
    data: data
  })
}

// 修改规范用语法律条款关联
export function updateNormativetermbind(data) {
  return request({
    url: '/system/normativetermbind',
    method: 'put',
    data: data
  })
}

// 删除规范用语法律条款关联（批量）
export function delNormativetermbind(legalTermIds) {
  return request({
    url: '/system/normativetermbind/' + legalTermIds,
    method: 'delete'
  })
}

// 解绑（根据法律条款ID和规范用语ID）
export function unbindNormativetermbind(legalTermId, normativeLanguageId) {
  return request({
    url: '/system/normativetermbind/unbind',
    method: 'delete',
    params: { legalTermId, normativeLanguageId }
  })
}

// 导出规范用语法律条款关联
export function exportNormativetermbind(query) {
  return request({
    url: '/system/normativetermbind/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}
