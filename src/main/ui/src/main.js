import Vue from 'vue'
import App from './App.vue'
import store from './store'
import ElementUI from 'element-ui';
import './assets/main.css';
import './assets/elementui-addon.css';
import 'element-ui/lib/theme-chalk/index.css';
import './plugins/EventBus';

Vue.config.productionTip = false

Vue.use(ElementUI);

new Vue({
  store,
  render: h => h(App)
}).$mount('#app')
