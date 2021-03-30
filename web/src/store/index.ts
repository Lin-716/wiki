import { createStore } from 'vuex'

//集成工具类
declare let SessionStorage:any

const USER = "USER"

//定义全局变量,操作在mutations和actions，mutations同步，actions异步
const store = createStore({
  state: {
    user: SessionStorage.get(USER) || {}
    //user:{}
  },
  mutations: {
    //state是全局的，右边的user是外部传进来的变量不是state里的
    setUser (state,user){
      state.user = user
      SessionStorage.set(USER, user)
    }
  },
  actions: {
  },
  modules: {
  }
})
export default store
