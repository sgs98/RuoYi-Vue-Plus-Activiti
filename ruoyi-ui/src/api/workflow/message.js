import request from '@/utils/request'

// 查询消息通知列表
export function listMessage(query) {
  return request({
    url: '/workflow/message/list',
    method: 'get',
    params: query
  })
}

// 查询消息通知详细
export function getMessage(id) {
  return request({
    url: '/workflow/message/' + id,
    method: 'get'
  })
}

// 新增消息通知
export function addMessage(data) {
  return request({
    url: '/workflow/message',
    method: 'post',
    data: data
  })
}

// 修改消息通知
export function updateMessage(data) {
  return request({
    url: '/workflow/message',
    method: 'put',
    data: data
  })
}

// 删除消息通知
export function delMessage(id) {
  return request({
    url: '/workflow/message/' + id,
    method: 'delete'
  })
}

// 阅读消息
export function readMessage(id) {
  return request({
    url: '/workflow/message/readMessage/' + id,
    method: 'get'
  })
}

// 批量阅读消息
export function batchReadMessage(id) {
  return request({
    url: '/workflow/message/batchReadMessage',
    method: 'get'
  })
}
