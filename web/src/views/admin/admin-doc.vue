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
         <a-tree-select
             v-model:value="doc.parent"
             style="width: 100%"
             :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
             :tree-data="treeSelectData"
             placeholder="选择父文档"
             tree-default-expand-all
             :replaceFields="{title: 'name', key: 'id', value: 'id'}"
         >
         </a-tree-select>
       </a-form-item>
       <a-form-item label="顺序">
         <a-input v-model:value="doc.sort" />
       </a-form-item>
       <a-form-item label="内容">
         <div id="content"></div>
       </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, createVNode } from 'vue'
import axios from 'axios'
import { message,Modal } from 'ant-design-vue'
import { Tool } from '@/util/tool'
import { useRoute } from "vue-router"
import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined"
import E from 'wangeditor'

export default defineComponent({
  name:'AdminDoc',
  setup() {
    const route = useRoute()
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
      level1.value = []
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
    const treeSelectData = ref()//level1要显示在表格，所以用treeSelectData来显示父文档节点，可以添加无
    treeSelectData.value = []
    const editor = new E('#content')//wangeditor


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

    /**
     * 将某节点及其子孙节点全部置为disabled
     */
    const setDisable = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          console.log("disabled", node);
          // 将目标节点设置为disabled
          node.disabled = true;

          // 遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id);
          }
        }
      }
    };

    //编辑
    const edit = (record: any) => {
      modalVisible.value = true
      doc.value = Tool.copy(record)//复制值显示到表单中，修改表单值未确定时，不会修改到值

      // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value, record.id);

      // 为选择树添加一个"无"
      treeSelectData.value.unshift({id: 0, name: '无'});//unshift往数组前面添加一个元素
      setTimeout(function() {
        editor.create()//页面渲染完，model出现了再去create
      },100)
    }

    //新增
    const add = () => {
      modalVisible.value = true
      doc.value = {}
      doc.value = {
        ebookId: route.query.ebookId
      }

      treeSelectData.value = Tool.copy(level1.value)

      treeSelectData.value.unshift({id: 0, name: '无'});//unshift往数组前面添加一个元素
      setTimeout(function() {
        editor.create()//页面渲染完，model出现了再去create
      },100)
    }

    const deleteNames: Array<string> = [];
    /**
     * 删除某节点及其子孙节点
     */
    const ids:Array<string> = []
    const getDeleteIds = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          deleteNames.push(node.name);
          ids.push(id)
          // 遍历所有子节点，将所有子节点都加入ids
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteIds(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getDeleteIds(children, id);
          }
        }
      }
    };

    //删除
    const handleDelete = (id: number) => {
      deleteNames.length = 0;
      getDeleteIds(level1.value, id)
      modalLoading.value = true
      console.log(deleteNames,ids)
      Modal.confirm({
        title: '确认删除',
        content: "将删除：【" + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
        icon: createVNode(ExclamationCircleOutlined),
        onOk() {
          axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
            const data = response.data // commonResp
            if (data.success){
              //重新加载
              handleQuery()
            }
          })
        },
        onCancel() {
          console.log('Cancel');
        },
      });

    }

    //后端获得分页参数
    //ebookId为admin-category中拼接的record.id（'/admin/doc/ebookid=?' + record.id
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
      level1,
      treeSelectData
    };
  },
});
</script>

