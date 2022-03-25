import request from '@/utils/request'

// 查询工作流接口用户
export function getWorkflowUserListByPage(data) {
    return request({
      url: '/workflow/user/getWorkflowUserListByPage',
      method: 'post',
      data: data
    })
}

