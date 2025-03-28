import { createApp } from 'vue'

import Cookies from 'js-cookie'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import locale from 'element-plus/es/locale/lang/zh-cn'

import '@/assets/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'
import directive from './directive' // directive

// 注册指令
import plugins from './plugins' // plugins
import { download } from '@/utils/request'

// import Menus from 'vue3-menus';


// svg图标
import 'virtual:svg-icons-register'
import SvgIcon from '@/components/SvgIcon'
import elementIcons from '@/components/SvgIcon/svgicon'
import { library } from '@fortawesome/fontawesome-svg-core'
import { fas } from '@fortawesome/free-solid-svg-icons'
// import { frs } from '@fortawesome/free-regular-svg-icons'

import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
library.add(fas)
// library.add(frs)

import './permission' // permission control

import { useDict } from '@/utils/dict'
import { parseTime, resetForm, addDateRange, handleTree, selectDictLabel, selectDictLabels } from '@/utils/ruoyi'

// 分页组件`
import Pagination from '@/components/Pagination'
// 自定义表格工具组件
import RightToolbar from '@/components/RightToolbar'
// 富文本组件
import Editor from "@/components/Editor"
// 文件上传组件
import FileUpload from "@/components/FileUpload"
// 图片上传组件
import ImageUpload from "@/components/ImageUpload"
// 图片预览组件
import ImagePreview from "@/components/ImagePreview"
// 自定义树选择组件
import TreeSelect from '@/components/TreeSelect'
// 字典标签组件
import DictTag from '@/components/DictTag'
// audio组件
import VueAudioPlayer from '@liripeng/vue-audio-player'
import LoadingWrapper from '@/components/loading/LoadingWrapper.vue';

// import hljs  from 'highlight.js';
// import 'highlight.js/styles/monokai-sublime.css'

/*黑色主题*/
// import 'highlight.js/styles/atom-one-dark.css';
/*白色主题*/
// import 'highlight.js/styles/stackoverflow-light.css';
// import hljs from 'highlight.js/lib/core';
// // import javascript from 'highlight.js/lib/languages/javascript';
// import hljsVuePlugin from "@highlightjs/vue-plugin";
// // 批量引入常用语言库
// import 'highlight.js/lib/common';


const app = createApp(App)
app.component('font-awesome-icon', FontAwesomeIcon).component('LoadingWrapper', LoadingWrapper)



// 全局方法挂载
app.config.globalProperties.useDict = useDict
app.config.globalProperties.download = download
app.config.globalProperties.parseTime = parseTime
app.config.globalProperties.resetForm = resetForm
app.config.globalProperties.handleTree = handleTree
app.config.globalProperties.addDateRange = addDateRange
app.config.globalProperties.selectDictLabel = selectDictLabel
app.config.globalProperties.selectDictLabels = selectDictLabels

// 全局组件挂载
app.component('DictTag', DictTag)
app.component('Pagination', Pagination)
app.component('TreeSelect', TreeSelect)
app.component('FileUpload', FileUpload)
app.component('ImageUpload', ImageUpload)
app.component('ImagePreview', ImagePreview)
app.component('RightToolbar', RightToolbar)
app.component('Editor', Editor)
app.component('VueAudioPlayer', VueAudioPlayer)
// app.component('Prism ', Prism )

// app.directive('highlight', function (el) {
//   let highlight = el.querySelectorAll('pre code');
//   highlight.forEach((block) => {
//     hljs.highlightElement(block);
//   })
// });
//
// app.use(hljsVuePlugin)


app.use(router)
app.use(store)
// app.use(Menus);
app.use(plugins)
app.use(elementIcons)
app.component('svg-icon', SvgIcon)

directive(app)

// 使用element-plus 并且设置全局的大小
app.use(ElementPlus, {
  locale: locale,
  // 支持 large、default、small
  size: Cookies.get('size') || 'default'
})

app.mount('#app')
