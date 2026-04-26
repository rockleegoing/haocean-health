import request from '@/utils/request'

// 查询法律法规列表
export function listRegulation(query) {
  return request({
    url: '/system/regulation/list',
    method: 'get',
    params: query
  })
}

// 查询法律法规详情
export function getRegulation(regulationId) {
  return request({
    url: '/system/regulation/' + regulationId,
    method: 'get'
  })
}

// 新增法律法规
export function addRegulation(data) {
  return request({
    url: '/system/regulation',
    method: 'post',
    data: data
  })
}

// 修改法律法规
export function updateRegulation(data) {
  return request({
    url: '/system/regulation',
    method: 'put',
    data: data
  })
}

// 删除法律法规
export function delRegulation(regulationIds) {
  return request({
    url: '/system/regulation/' + regulationIds,
    method: 'delete'
  })
}

// 获取章节列表
export function getChapterList(regulationId, query) {
  return request({
    url: '/system/regulation/chapters/' + regulationId,
    method: 'get',
    params: query
  })
}

// 查询章节详情
export function getChapter(chapterId) {
  return request({
    url: '/system/regulation/chapter/' + chapterId,
    method: 'get'
  })
}

// 获取条款列表
export function getArticleList(regulationId, query) {
  return request({
    url: '/system/regulation/articles/' + regulationId,
    method: 'get',
    params: query
  })
}

// 查询条款详情
export function getArticle(articleId) {
  return request({
    url: '/system/regulation/article/' + articleId,
    method: 'get'
  })
}

// 新增章节
export function addChapter(data) {
  return request({
    url: '/system/regulation/chapter',
    method: 'post',
    data: data
  })
}

// 修改章节
export function updateChapter(data) {
  return request({
    url: '/system/regulation/chapter',
    method: 'put',
    data: data
  })
}

// 删除章节
export function delChapter(chapterIds) {
  return request({
    url: '/system/regulation/chapter/' + chapterIds,
    method: 'delete'
  })
}

// 新增条款
export function addArticle(data) {
  return request({
    url: '/system/regulation/article',
    method: 'post',
    data: data
  })
}

// 修改条款
export function updateArticle(data) {
  return request({
    url: '/system/regulation/article',
    method: 'put',
    data: data
  })
}

// 删除条款
export function delArticle(articleIds) {
  return request({
    url: '/system/regulation/article/' + articleIds,
    method: 'delete'
  })
}

// 查询定性依据列表
export function listLegalBasis(query) {
  return request({
    url: '/system/legalBasis/list',
    method: 'get',
    params: query
  })
}

// 查询定性依据详情
export function getLegalBasis(basisId) {
  return request({
    url: '/system/legalBasis/' + basisId,
    method: 'get'
  })
}

// 新增定性依据
export function addLegalBasis(data) {
  return request({
    url: '/system/legalBasis',
    method: 'post',
    data: data
  })
}

// 修改定性依据
export function updateLegalBasis(data) {
  return request({
    url: '/system/legalBasis',
    method: 'put',
    data: data
  })
}

// 删除定性依据
export function delLegalBasis(basisIds) {
  return request({
    url: '/system/legalBasis/' + basisIds,
    method: 'delete'
  })
}

// 获取某法规关联的定性依据
export function getLegalBasisByRegulation(regulationId) {
  return request({
    url: '/system/legalBasis/regulation/' + regulationId,
    method: 'get'
  })
}
