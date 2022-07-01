import request from '@/utils/request'

export default {

  // 查询当前用户的待办任务
  getTaskWaitByPage(query) {
    return request({
      url: '/workflow/task/getTaskWaitByPage',
      method: 'get',
      params: query
    })
  },

  // 查询当前用户的已办任务
  getTaskFinishByPage(query) {
    return request({
      url: '/workflow/task/getTaskFinishByPage',
      method: 'get',
      params: query
    })
  },

    // 查询所有用户的待办任务
    getAllTaskWaitByPage(query) {
      return request({
        url: '/workflow/task/getAllTaskWaitByPage',
        method: 'get',
        params: query
      })
    },

    // 查询所有用户的已办任务
    getAllTaskFinishByPage(query) {
      return request({
        url: '/workflow/task/getAllTaskFinishByPage',
        method: 'get',
        params: query
      })
    },

  // 完成任务
  completeTask(data) {
    return request({
      url: '/workflow/task/completeTask',
      method: 'post',
      data: data
    })
  },

  // 获取目标节点（下一个节点）
  getNextNodeInfo(data) {
    return request({
      url: '/workflow/task/getNextNodeInfo',
      method: 'post',
      data: data
    })
  },

  // 获取历史任务节点，用于驳回功能
  getBackNodes(processInstId) {
    return request({
      url: '/workflow/task/getBackNodes/'+processInstId,
      method: 'get'
    })
  },

  // 驳回审批
  backProcess(data) {
    return request({
      url: '/workflow/task/backProcess',
      method: 'post',
      data: data
    })
  },
  // 删除执行后的节点
  deleteByNodeIds(data) {
    return request({
      url: '/workflow/task/deleteByNodeIds',
      method: 'post',
      data: data
    })
  },
  // 签收任务
  claim(taskId) {
    return request({
      url: '/workflow/task/claim/'+taskId,
      method: 'post'
    })
  },
  // 归还任务
  returnTask(taskId) {
    return request({
      url: '/workflow/task/returnTask/'+taskId,
      method: 'post'
    })
  },
  // 委托任务
  delegateTask(data) {
    return request({
      url: '/workflow/task/delegateTask',
      method: 'post',
      data: data
    })
  },
  // 转办任务
  transmitTask(data) {
    return request({
      url: '/workflow/task/transmitTask',
      method: 'post',
      data: data
    })
  },
  // 会签任务加签
  addMultiInstanceExecution(data) {
    return request({
      url: '/workflow/task/addMultiInstanceExecution',
      method: 'post',
      data: data
    })
  },
  // 会签任务减签
  deleteMultiInstanceExecution(data) {
    return request({
      url: '/workflow/task/deleteMultiInstanceExecution',
      method: 'post',
      data: data
    })
  }
}
