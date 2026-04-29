import request from '@/utils/request'

// 绑定法律类型（可批量）
export function bindLawType(lawId, typeIds) {
  return request({
    url: '/app/lawtype/bind/' + lawId,
    method: 'post',
    data: typeIds
  })
}

// 获取某法律的类型绑定列表
export function getLawTypeBind(lawId) {
  return request({
    url: '/app/lawtype/bind/' + lawId,
    method: 'get'
  })
}

// 删除某法律的全部绑定
export function deleteLawTypeBind(lawId) {
  return request({
    url: '/app/lawtype/bind/' + lawId,
    method: 'delete'
  })
}
