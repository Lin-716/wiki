<template>
  <a-layout class="admin-ebook">
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-button type=primary @click="add()" size="large">
          新增
        </a-button>
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
<!--        record代表一整行的数据-->
        <template v-slot:action="{ text, record }">
<!--          空格组件-->
          <a-space size="small">
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
      <a-form-item label="分类一">
        <a-input v-model:value="ebook.category1Id" />
      </a-form-item>
      <a-form-item label="分类二">
        <a-input v-model:value="ebook.category2Id" />
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
//import { message } from 'ant-design-vue'

export default defineComponent({
  name:'AdminEbook',
  setup() {
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
        title: '分类一',
        key: 'category1Id',
        dataIndex: 'category1Id'
      },
      {
        title: '分类二',
        key: 'category2Id',
        dataIndex: 'category2Id'
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
      axios.get("/ebook/list",{
        params: {
          page: params.page,
          size: params.size
        }
      }).then((response) => {
        loading.value = false
        const data = response.data //commomResp
        ebooks.value = data.content.list
        //测试demo content的list代表了数据

        //reset pagination button
        pagination.value.current = params.page;
        pagination.value.total = data.content.page;
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
      axios.post("/ebook/save",ebook.value).then((response) => {
        const data = response.data
        if (data.success){
          modalLoading.value = false
          modalVisible.value = false

          //重新加载
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          })
        }
      })
    }

    //编辑
    const edit = (record: any) => {
      modalVisible.value = true
      ebook.value = record
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

    //后端获得分页参数
    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      })
    })

    return {
      ebooks,
      pagination,
      columns,
      loading,
      handleTableChange,

      edit,
      add,
      handleDelete,

      modalVisible,
      modalLoading,
      handleModalOk,

      ebook
    };
  },
});
</script>

