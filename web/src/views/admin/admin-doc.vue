<template>
  <a-layout class="admin-doc">
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :gutter="24">
        <a-col :span="8">
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
                   size="small"
                   :defaultExpandAllRows="true"
          >
            <template #name="{ text, record }">
              {{record.sort}} {{text}}
            </template>
            <!--        record代表一整行的数据-->
            <template v-slot:action="{ text, record }">
              <!--          空格组件-->
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                    title="确认删除吗?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
                >
                  <a-button type="danger" size="small">
                    删除
                  </a-button>
                </a-popconfirm>

              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称" />
            </a-form-item>
            <a-form-item>
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
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent()">
                <EyeOutline/>内容预览
              </a-button>
            </a-form-item>
            <a-form-item>
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>

    </a-layout-content>
  </a-layout>

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
    const treeSelectData = ref()//level1要显示在表格，所以用treeSelectData来显示父文档节点，可以添加无
    treeSelectData.value = []

    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
        slots: { customRender: 'name' }
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }]

    // 数据查询
    const handleQuery = () => {
      loading.value = true
      level1.value = []
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
        loading.value = false
        const data = response.data //commomResp
        if(data.success){
          docs.value = data.content
          level1.value = []
          level1.value = Tool.array2Tree(docs.value,0)//一级文档parent为0

          //给父文档选择框赋值
          treeSelectData.value = Tool.copy(level1.value)
          treeSelectData.value.unshift({id:0, name:'无'})

        }else{
          message.error(data.message)
        }
      })
    }

    //表单
    const doc = ref()
    doc.value = {
      ebookId: route.query.ebookId
    };
    const modalVisible = ref(false)
    const modalLoading = ref(false)
    const editor = new E('#content')//wangeditor
    editor.config.zIndex = 0;

    const handleSave = () => {
      modalLoading.value = true
      doc.value.content = editor.txt.html();//获取富文本的内容
      console.log("content",doc.value)
      axios.post("/doc/save",doc.value).then((response) => {
        modalLoading.value = false
        const data = response.data
        if (data.success){
          modalVisible.value = false
          //重新加载
          handleQuery()
          message.success("保存成功")
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


    // 内容查询
    const handleQueryContent = () => {
      axios.get("/doc/find-content/" + doc.value.id).then((response) => {
        const data = response.data //commomResp
        if(data.success){
          editor.txt.html(data.content)
        }else{
          message.error(data.message)
        }
      })
    }


    //编辑
    const edit = (record: any) => {
      editor.txt.html("");
      modalVisible.value = true
      doc.value = Tool.copy(record)//复制值显示到表单中，修改表单值未确定时，不会修改到值
      handleQueryContent()

      // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value, record.id);

      // 为选择树添加一个"无"
      treeSelectData.value.unshift({id: 0, name: '无'});//unshift往数组前面添加一个元素
    }

    //新增
    const add = () => {
      editor.txt.html("");
      modalVisible.value = true
      console.log(route.query.ebookId)
      doc.value = {
        ebookId: route.query.ebookId
      }

      treeSelectData.value = Tool.copy(level1.value) || []

      treeSelectData.value.unshift({id: 0, name: '无'});//unshift往数组前面添加一个元素
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

    //富文本的预览
    const drawerVisible = ref(false)
    const previewHtml = ref()
    const handlePreviewContent = () => {
      const html = editor.txt.html()
      previewHtml.value = html
      drawerVisible.value = true
    }
    const onDrawerClose = () => {
      drawerVisible.value = false
    }

    //后端获得分页参数
    //ebookId为admin-category中拼接的record.id（'/admin/doc/ebookid=?' + record.id
    onMounted(() => {
      handleQuery()
      editor.create()

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
      handleSave,

      doc,
      level1,
      treeSelectData,

      drawerVisible,
      previewHtml,
      handlePreviewContent,
      onDrawerClose
    };
  },
});
</script>

