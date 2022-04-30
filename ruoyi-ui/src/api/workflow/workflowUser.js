import request from '@/utils/request'

// 查询工作流接口用户
export function getWorkflowUserListByPage(data) {
    return request({
      url: '/workflow/user/getWorkflowUserListByPage',
      method: 'post',
      data: data
    })
}
// 分页查询工作流选择加签人员
export function getWorkflowAddMultiListByPage(data) {
  return request({
    url: '/workflow/user/getWorkflowAddMultiListByPage',
    method: 'post',
    data: data
  })
}
