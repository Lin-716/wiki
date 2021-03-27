<template>
  <a-layout class="admin-doc">
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-button type="primary" html-type="submit" @click="handleQuery()">
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
               :data-source="level1"
               :loading="Loading"
               :pagination="false"
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
      title="doc form"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
     <a-form :model="doc" :label-col="{ span: 6 }">
       <a-form-item label="名称">
         <a-input v-model:value="doc.name" />
       </a-form-item>
       <a-form-item label="父文档">
         <a-input v-model:value="doc.parent" />
       </a-form-item>
       <a-form-item label="顺序">
         <a-input v-model:value="doc.sort" />
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
  name:'AdminDoc',
  setup() {
    const param= ref()
    param.value = {}
    const docs = ref()
    const level1 = ref()
    const loading = ref(false)

    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '父文档',
        key: 'parent',
        dataIndex: 'parent'
      },
      {
        title: '顺序',
        dataIndex: 'sort'
      },
      {
        title: 'Action',
        dataIndex: 'action',
        slots: { customRender: 'action' }
      },]
    // 数据查询,只在方法内部调用不需要被return
    const handleQuery = () => {
      loading.value = true
      axios.get("/doc/all").then((response) => {
        loading.value = false
        const data = response.data //commomResp
        if(data.success){
          docs.value = data.content
          console.log('docs',docs)
          level1.value = []
          level1.value = Tool.array2Tree(docs.value,0)//一级文档parent为0
          console.log('level',level1.value)
        }else{
          message.error(data.message)
        }
      })
    }


    //表单
    const doc = ref()
    const modalVisible = ref(false)
    const modalLoading = ref(false)

    const handleModalOk = () => {
      modalLoading.value = true
      axios.post("/doc/save",doc.value).then((response) => {
        modalLoading.value = false
        const data = response.data
        if (data.success){
          modalVisible.value = false
          //重新加载
          handleQuery()
        }else{
          message.error(data.message)
        }
      })
    }

    //编辑
    const edit = (record: any) => {
      modalVisible.value = true
      doc.value = Tool.copy(record)//复制值显示到表单中，修改表单值未确定时，不会修改到值
    }

    //新增
    const add = () => {
      modalVisible.value = true
      doc.value = {}
    }

    //删除
    const handleDelete = (id: number) => {
      modalLoading.value = true
      axios.delete("/doc/delete/" + id,doc.value).then((response) => {
        const data = response.data // commonResp
        if (data.success){
          //重新加载
          handleQuery()
        }
      })
    }

    //后端获得分页参数
    onMounted(() => {
      handleQuery()
    })

    return {
      docs,
      columns,
      loading,

      edit,
      add,
      param,
      handleDelete,
      handleQuery,

      modalVisible,
      modalLoading,
      handleModalOk,

      doc,
      level1
    };
  },
});
</script>

