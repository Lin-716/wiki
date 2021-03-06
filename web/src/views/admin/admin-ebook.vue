<template>
  <a-layout class="admin-ebook">
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
               :data-source="ebooks"
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
            <router-link :to="'/admin/doc?ebookId=' + record.id">
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
      title="ebook form"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <p><a-form :model="ebook" :label-col="{ span: 6 }">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类">
        <a-cascader v-model:value="ebook.categoryIds"
                    :field-names="{ label:'name', value:'id', children:'children' }"
                    :options="level1"
                     />
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" />
      </a-form-item>
    </a-form></p>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios'
import { message } from 'ant-design-vue'
import { Tool } from '@/util/tool'

export default defineComponent({
  name:'AdminEbook',
  setup() {
    const categoryIds = ref() //数组[一级分类，二级分类]
    const param= ref()
    param.value = {}
    const ebooks = ref()
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total:0
    })
    const loading = ref(false)

    const columns = [
      {
        title: '封面',
        dataIndex: 'cover',
        slots: { customRender: 'cover' }
      },
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '分类',
        key: 'categoryIds',
        slots: { customRender: 'category' }
      },
      {
        title: '文档数',
        dataIndex: 'docCount'
      },
      {
        title: '阅读数',
        dataIndex: 'viewCount'
      },
      {
        title: '点赞数',
        dataIndex: 'voteCount'
      },
      {
        title: 'Action',
        dataIndex: 'action',
        slots: { customRender: 'action' }
      },
    ]

    // 数据查询,只在方法内部调用不需要被return
    const handleQuery = (params:any) => {
      loading.value = true
      ebook.value = []
      axios.get("/ebook/list",{
        params: {
          page: params.page,
          size: params.size,
          name: param.value.name
        }
      }).then((response) => {
        loading.value = false
        const data = response.data //commomResp
        if(data.success){
          //测试demo content的list代表了数据
          ebooks.value = data.content.list

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
    const ebook = ref()
    const modalVisible = ref(false)
    const modalLoading = ref(false)

    const handleModalOk = () => {
      modalLoading.value = true
      ebook.value.category1Id = categoryIds.value[0]
      ebook.value.category2Id = categoryIds.value[1]
      axios.post("/ebook/save",ebook.value).then((response) => {
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
      ebook.value = Tool.copy(record)//复制值显示到表单中，修改表单值未确定时，不会修改到值
      categoryIds.value = [ebook.value.category1Id]
    }

    //新增
    const add = () => {
      modalVisible.value = true
      ebook.value = {}
    }

    //删除
    const handleDelete = (id: number) => {
      modalLoading.value = true
      axios.delete("/ebook/delete/" + id,ebook.value).then((response) => {
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
    const level1 = ref()
    let categorys: any
    const handleQueryCategory = () => {
      loading.value = true
      axios.get("/category/all").then((response) => {
        loading.value = false
        const data = response.data //commomResp
        if(data.success){
          categorys= data.content
          level1.value = []
          level1.value = Tool.array2Tree(categorys,0)//一级分类parent为0

          //加载完分类后在加载电子书，保证数据加载完后再加载电子书
          handleQuery({
            // page: 1,
            // size: pagination.value.pageSize
          })
        }else{
          message.error(data.message)
        }
      })
    }

    const getCategoryName = (cid:number) => {
      let result = ""
      categorys.forEach((item:any) => {
        if(item.id == cid){
          result = item.name
        }
      })
      return result
    }

    //后端获得分页参数
    onMounted(() => {
      handleQueryCategory()

    })

    return {
      ebooks,
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

      ebook,
      handleQueryCategory,
      level1,
      getCategoryName
    };
  },
});
</script>

