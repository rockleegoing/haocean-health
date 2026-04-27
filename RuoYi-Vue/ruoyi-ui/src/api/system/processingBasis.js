import request from '@/utils/request'

// 查询处理依据列表
export function listProcessingBasis(query) {
  return request({
    url: '/system/processingBasis/list',
    method: 'get',
    params: query
  })
}

// 查询处理依据详情
export function getProcessingBasis(basisId) {
  return request({
    url: '/system/processingBasis/' + basisId,
    method: 'get'
  })
}

// 新增处理依据
export function addProcessingBasis(data) {
  return request({
    url: '/system/processingBasis',
    method: 'post',
    data: data
  })
}

// 修改处理依据
export function updateProcessingBasis(data) {
  return request({
    url: '/system/processingBasis',
    method: 'put',
    data: data
  })
}

// 删除处理依据
export function delProcessingBasis(basisIds) {
  return request({
    url: '/system/processingBasis/' + basisIds,
    method: 'delete'
  })
}

// 获取某法规关联的处理依据
export function getProcessingBasisByRegulation(regulationId) {
  return request({
    url: '/system/processingBasis/regulation/' + regulationId,
    method: 'get'
  })
}
