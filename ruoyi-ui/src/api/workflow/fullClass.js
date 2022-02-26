import request from '@/utils/request'

// 查询业务规则列表
export function listFullClass(query) {
  return request({
    url: '/workflow/fullClass/list',
    method: 'get',
    params: query
  })
}

// 查询业务规则详细
export function getFullClass(id) {
  return request({
    url: '/workflow/fullClass/' + id,
    method: 'get'
  })
}

// 新增业务规则
export function addFullClass(data) {
  return request({
    url: '/workflow/fullClass',
    method: 'post',
    data: data
  })
}

// 修改业务规则
export function updateFullClass(data) {
  return request({
    url: '/workflow/fullClass',
    method: 'put',
    data: data
  })
}

// 删除业务规则
export function delFullClass(id) {
  return request({
    url: '/workflow/fullClass/' + id,
    method: 'delete'
  })
}
