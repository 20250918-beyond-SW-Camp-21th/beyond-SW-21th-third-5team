import { h, defineComponent, ref, onMounted } from 'vue';

export default defineComponent({
  name: 'ImageWithFallback',
  props: {
    src: { type: String, required: true },
    alt: { type: String, default: '' },
  },
  setup(props, { attrs }) {
    const error = ref(false);

    const handleError = () => {
      if (!error.value) {
        error.value = true;
      }
    };

    onMounted(() => {
      if (!props.src) {
        error.value = true;
      }
    });

    return () =>
      h('img', {
        ...attrs,
        src: error.value ? undefined : props.src,
        alt: props.alt,
        onError: handleError,
      });
  },
});
