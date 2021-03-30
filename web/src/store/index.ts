import { createStore } from 'vuex'

//定义全局变量,操作在mutations和actions，mutations同步，actions异步
const store = createStore({
  state: {
    user:{ }
  },
  mutations: {
    //state是全局的，右边的user是外部传进来的变量不是state里的
    setUser (state,user){
      state.user = user
    }
  },
  actions: {
  },
  modules: {
  }
})
export default store
