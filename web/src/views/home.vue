
<template>
  <a-layout class="home">
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          v-model:selectedKeys="selectedKeys2"
          v-model:openKeys="openKeys"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
      >
        <a-menu-item>
          <MailOutlined />
          <span>welcome</span>
        </a-menu-item>
        <a-sub-menu :key="item.id" v-for="item in level1">
          <template v-slot:title>
              <span>
                <user-outlined />
                {{ item.name }}
              </span>
          </template>
          <a-menu-item :key="child.id" v-for="child in item.children">{{ child.name }}</a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div class="welcome" v-show="isShowWelcome">welcome</div>
<!--content-->
      <a-list item-layout="vertical" v-show="!isShowWelcome" size="large" :pagination="pagination" :grid="{ gutter: 20, column: 3 }" :data-source="ebooks">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px" />
            {{ text }}
          </span>
            </template>

            <a-list-item-meta :description="item.description">
              <template #title>
                <a :href="item.href">{{ item.name }}</a>
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>

          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent,onMounted,ref } from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue'
import { Tool } from '@/util/tool.ts'

const listData: Record<string, string>[] = [];

for (let i = 0; i < 23; i++) {
  listData.push({
    href: 'https://www.antdv.com/',
    title: `ant design vue part ${i}`,
    avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
    description:
        'Ant Design, a design language for background applications, is refined by Ant UED Team.',
    content:
        'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
  });
}

export default defineComponent({
  name: 'Home',
  // components: {
  //   // StarOutlined,
  //   // LikeOutlined,
  //   // MessageOutlined
  // },
  setup(){
    console.log("set")
    //ref响应式数据
    const ebooks = ref();
    onMounted(() => {
      console.log("mounted22")
      //initial method 写进 mouted钩子里
      // 配置过了process.env.VUE_APP_SERVER为baseURL
      axios.get("/ebook/list", {
        params:{
          page:1,
          size:1000
        }
          }
      ).then(
          (response) => {
            const data = response.data
            ebooks.value = data.content.list
            // console.log(response)
          })
    })

    const pagination = {
      onChange: (page: number) => {
        console.log(page);
      },
      //nums of item each page
      pageSize: 9,
    };
    const actions: Record<string, string>[] = [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ];

    //查询分类
    const level1 = ref()
    let categorys: any
    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data //commomResp
        if(data.success){
          categorys= data.content
          console.log('categorys',categorys)
          level1.value = []
          level1.value = Tool.array2Tree(categorys,0)//一级分类parent为0
          console.log('level',level1.value)
        }else{
          message.error(data.message)
        }
      })
    }

    const isShowWelcome = ref(true)

    const handleClick = (value:any) => {
      //console.log(value)
      if(value.key === "welcome"){
        isShowWelcome.value = true
      }else{
        isShowWelcome.value = false
      }
    }

    onMounted(()=>{
      handleQueryCategory()
    })
    return {
      ebooks,
      //listData,
      pagination,
      actions,
      handleClick,
      isShowWelcome,

      level1
    }

  },
  components: {

  },
});
</script>
