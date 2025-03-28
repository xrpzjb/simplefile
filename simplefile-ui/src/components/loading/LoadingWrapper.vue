<template>
  <div>
    <template v-if="isLoading">
      <slot name="loading">
        <p>正在加载文档，请稍候...</p>
      </slot>
    </template>
    <template v-else>
      <slot :onRendered="handleRendered" :onError="handleError" />
    </template>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue';

const props = defineProps({
  src: {
    type: String,
    required: true
  }
});

const emits = defineEmits(['rendered', 'error']);

const isLoading = ref(true);

watch(() => props.src, () => {
  isLoading.value = true;
});

const handleRendered = () => {
  isLoading.value = false;
  emits('rendered');
};

const handleError = () => {
  isLoading.value = false;
  emits('error');
};
</script>