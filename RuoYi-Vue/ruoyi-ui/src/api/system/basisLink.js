import request from '@/utils/request'

// 查询依据关联列表
export function listBasisLink(query) {
  return request({
    url: '/system/basisLink/list',
    method: 'get',
    params: query
  })
}

// 获取依据关联详情
export function getBasisLink(linkId) {
  return request({
    url: '/system/basisLink/' + linkId,
    method: 'get'
  })
}

// 根据章节ID获取关联列表
export function getBasisLinkByChapterId(chapterId) {
  return request({
    url: '/system/basisLink/chapter/' + chapterId,
    method: 'get'
  })
}

// 根据章节ID和依据类型获取关联列表
export function getBasisLinkByChapterIdAndType(chapterId, basisType) {
  return request({
    url: '/system/basisLink/chapter/' + chapterId + '/' + basisType,
    method: 'get'
  })
}

// 新增依据关联
export function addBasisLink(data) {
  return request({
    url: '/system/basisLink',
    method: 'post',
    data: data
  })
}

// 修改依据关联
export function updateBasisLink(data) {
  return request({
    url: '/system/basisLink',
    method: 'put',
    data: data
  })
}

// 删除依据关联
export function delBasisLink(linkIds) {
  return request({
    url: '/system/basisLink/' + linkIds,
    method: 'delete'
  })
}
