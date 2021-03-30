<template>
  <a-layout-footer style="text-align: center">
    {{user.name}}
  </a-layout-footer>
</template>
<script lang="ts">
import { defineComponent, computed } from 'vue';
import store from "@/store";

export default defineComponent({
  name: 'the-footer',
  setup() {
    const user = computed(() => store.state.user)//computed表示user不是写死的，是通过变化计算得来的
    return {
      user
    }
  }
});
</script>