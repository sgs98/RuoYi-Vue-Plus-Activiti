import { listWaitingMessage } from "@/api/workflow/message";

const message = {
  state: {
    list: '',
    total: ''
  },
  mutations: {
    SET_MESSAGE: (state, data) => {
        state.list = data.rows
        state.total = data.total
    }
  },
  actions: {
    getMessage({ commit, state }) {
        return new Promise((resolve, reject) => {
          listWaitingMessage().then(response => {
            commit('SET_MESSAGE',response)
            resolve(response)
          }).catch(error => {
            reject(error)
          })
        })
    }
  }
}

export default message
