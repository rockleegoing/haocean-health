import request from '@/utils/request'

// 查询关联列表
export function listBasisLink(query) {
  return request({
    url: '/system/basisLink/list',
    method: 'get',
    params: query
  })
}

// 新增关联
export function addBasisLink(data) {
  return request({
    url: '/system/basisLink',
    method: 'post',
    data: data
  })
}

// 删除关联
export function delBasisLink(linkIds) {
  return request({
    url: '/system/basisLink/' + linkIds,
    method: 'delete'
  })
}

// 根据条款ID获取关联的定性依据
export function getLegalBasisByArticle(articleId) {
  return request({
    url: '/system/basisLink/legalBasis/' + articleId,
    method: 'get'
  })
}

// 根据条款ID获取关联的处理依据
export function getProcessingBasisByArticle(articleId) {
  return request({
    url: '/system/basisLink/processingBasis/' + articleId,
    method: 'get'
  })
}

// 获取某法规的章节下条目关联依据统计
export function getArticleBasisCount(regulationId) {
  return request({
    url: '/system/basisLink/articleBasisCount/' + regulationId,
    method: 'get'
  })
}
