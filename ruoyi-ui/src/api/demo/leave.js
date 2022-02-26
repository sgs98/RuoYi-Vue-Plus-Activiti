import request from '@/utils/request'

// 查询请假业务列表
export function listLeave(query) {
  return request({
    url: '/demo/leave/list',
    method: 'get',
    params: query
  })
}

// 查询请假业务详细
export function getLeave(id) {
  return request({
    url: '/demo/leave/' + id,
    method: 'get'
  })
}

// 新增请假业务
export function addLeave(data) {
  return request({
    url: '/demo/leave',
    method: 'post',
    data: data
  })
}

// 修改请假业务
export function updateLeave(data) {
  return request({
    url: '/demo/leave',
    method: 'put',
    data: data
  })
}

// 删除请假业务
export function delLeave(id) {
  return request({
    url: '/demo/leave/' + id,
    method: 'delete'
  })
}
