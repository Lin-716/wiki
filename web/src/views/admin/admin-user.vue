<template>
  <a-layout class="admin-user">
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="name">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit" @click="handleQuery({page:1,size:pagination.pageSize})">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type=primary @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table :columns="columns"
               :row-key="record => record.id"
               :data-source="users"
               :pagination="pagination"
               :loading="Loading"
               @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:category="{ text, record }">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
        </template>
<!--        record代表一整行的数据-->
        <template v-slot:action="{ text, record }">
<!--          空格组件-->
          <a-space size="small">
            <router-link :to="'/admin/doc?userId=' + record.id">
              <a-button type="primary">
                文档管理
              </a-button>
            </router-link>

            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="确认删除吗?"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>

          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

<!--  表单-->
  <a-modal
      title="user form"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="登陆名">
        <a-input v-model:value="user.loginName" :disabled="user.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name" />
      </a-form-item>
      <a-form-item label="密码">
        <a-input v-model:value="user.password" type="password"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios'
import { message } from 'ant-design-vue'
import { Tool } from '@/util/tool'

export default defineComponent({
  name:'AdminUser',
  setup() {
    const categoryIds = ref() //数组[一级分类，二级分类]
    const param= ref()
    param.value = {}
    const users = ref()
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total:0
    })
    const loading = ref(false)

    const columns = [
      {
        title: '登陆名',
        dataIndex: 'loginName'
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '密码',
        dataIndex: 'password'
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }
    ]

    // 数据查询,只在方法内部调用不需要被return
    const handleQuery = (params:any) => {
      loading.value = true
      user.value = []
      axios.get("/user/list",{
        params: {
          page: params.page,
          size: params.size,
          loginName: param.value.loginName
        }
      }).then((response) => {
        loading.value = false
        const data = response.data //commomResp
        if(data.success){
          //测试demo content的list代表了数据
          users.value = data.content.list

          //reset pagination button
          pagination.value.current = params.page
          pagination.value.total = data.content.page
        }else{
          message.error(data.message)
        }
      })
    }

    //点击页码
    const handleTableChange = (pagination: any) => {
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      })
    }

    //表单
    const user = ref()
    const modalVisible = ref(false)
    const modalLoading = ref(false)

    const handleModalOk = () => {
      modalLoading.value = true

      axios.post("/user/save",user.value).then((response) => {
        modalLoading.value = false
        const data = response.data
        if (data.success){
          modalVisible.value = false
          //重新加载
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          })
        }else{
          message.error(data.message)
        }
      })
    }

    //编辑
    const edit = (record: any) => {
      modalVisible.value = true
      user.value = Tool.copy(record)//复制值显示到表单中，修改表单值未确定时，不会修改到值
      categoryIds.value = [user.value.category1Id]
    }

    //新增
    const add = () => {
      modalVisible.value = true
      user.value = {}
    }

    //删除
    const handleDelete = (id: number) => {
      modalLoading.value = true
      axios.delete("/user/delete/" + id).then((response) => {
        const data = response.data // commonResp
        if (data.success){
          //重新加载
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          })
        }
      })
    }

    //查询分类
    // const level1 = ref()
    // let categorys: any
    // const handleQueryCategory = () => {
    //   loading.value = true
    //   axios.get("/category/all").then((response) => {
    //     loading.value = false
    //     const data = response.data //commomResp
    //     if(data.success){
    //       categorys= data.content
    //       level1.value = []
    //       level1.value = Tool.array2Tree(categorys,0)//一级分类parent为0
    //
    //       //加载完分类后在加载电子书，保证数据加载完后再加载电子书
    //       handleQuery({
    //         // page: 1,
    //         // size: pagination.value.pageSize
    //       })
    //     }else{
    //       message.error(data.message)
    //     }
    //   })
    // }
    //
    // const getCategoryName = (cid:number) => {
    //   let result = ""
    //   categorys.forEach((item:any) => {
    //     if(item.id == cid){
    //       result = item.name
    //     }
    //   })
    //   return result
    // }

    //后端获得分页参数
    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    })

    return {
      users,
      pagination,
      columns,
      loading,
      handleTableChange,

      edit,
      add,
      param,
      handleDelete,
      handleQuery,

      modalVisible,
      modalLoading,
      handleModalOk,

      user,
    };
  },
});
</script>

