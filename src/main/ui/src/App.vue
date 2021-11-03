<template>
  <div id="app">
    <el-container class="main-container">
      <el-header class="main-header">
        <h3 style="margin-right: 16px;">网络调试助手</h3>
        <el-menu mode="horizontal" :default-active="menuIndex" @select="handleMenuChange">
          <el-menu-item index="TcpServer">TCP Server</el-menu-item>
<!--          <el-menu-item index="TcpClient">TCP Client</el-menu-item>-->
        </el-menu>
        <div>
          <el-form class="condensed" inline size="mini" v-if="editingSid">
            <el-form-item>
              <el-input size="mini" v-model="sidForEdit"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button size="mini" type="text" icon="el-icon-check" @click="saveSid"></el-button>
              <el-button size="mini" type="text" icon="el-icon-close" @click="cancelSaveSid"></el-button>
            </el-form-item>
          </el-form>
          <template v-else>
            <span>身份标识:</span>
            <span>{{sid}}</span>
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEditSid"></el-button>
          </template>
        </div>
      </el-header>
      <el-main>
        <component :is="menuIndex"></component>
      </el-main>
    </el-container>
    <div class="disabled-wrapper" v-if="!isAlive"></div>
  </div>
</template>

<script>
import TcpServer from "@/components/TcpServer";
import TcpClient from "@/components/TcpClient";
import {mapState} from 'vuex';

export default {
  name: 'App',
  components: {
    TcpServer, TcpClient,
  },
  data() {
    this.ws = null;
    return {
      menuIndex: 'TcpServer',
      isAlive: false,

      editingSid: false,
      sidForEdit: this.sid,
    };
  },
  computed: {
    ...mapState(['sid']),
  },
  watch: {
    sid() {
      this.initWebSocket();
    }
  },
  methods: {
    handleEditSid() {
      this.sidForEdit = this.sid;
      this.editingSid = true;
    },
    saveSid() {
      this.$store.commit('setSid', this.sidForEdit);
      this.editingSid = false;
    },
    cancelSaveSid() {
      this.sidForEdit = this.sid;
      this.editingSid = false;
    },
    handleMenuChange(key, keyPath) {
      this.menuIndex = key;
    },

    initWebSocket() {
      if(this.ws)
        this.ws.close();
      this.ws = new WebSocket('ws://' + location.host + '/ws/' + this.sid);
      this.ws.onerror = (evt) => {
        console.log('ws.error', evt, arguments);
        this.ws.close();
        this.ws = null;
        this.$nextTick(() => {
          this.isAlive = false;
          this.$message({
            message: '通信异常，请刷新页面重试',
            type: 'warning'
          });
        });
      };
      this.ws.onopen = (evt) => {
        console.log('ws.open', evt);
        this.$store.dispatch('updateServer');
        this.$nextTick(() => {
          this.isAlive = true;
          this.$message({
            message: '通信通道已建立',
            type: 'success'
          });
        });
      };
      this.ws.onmessage = (evt) => {
        console.log('ws.message', evt);
        try {
          const data = JSON.parse(evt.data || {});
          console.log("message data", data);
          switch(data.event) {
            case EVT_NEW_SERVER:
            case EVT_SERVER_GONE:
            case EVT_NEW_CLIENT:
            case EVT_CLIENT_GONE:
              this.$store.dispatch('updateServer');
              break;
            case EVT_NEW_DATA:
              this.$store.commit('appendData', data);
              break;
          }
        }
        catch (e) {}
      };
      this.ws.onclose = (evt) => {
        console.log('ws.close', evt);
        this.ws = null;
        this.$nextTick(() => {
          this.isAlive = false;
          this.$message({
            message: '通信通道关闭，请刷新页面重试',
            type: 'warning'
          });
        });
      };
    }
  },
  beforeMount() {
    this.initWebSocket();
  },
  beforeDestroy() {
    if(this.ws)
      this.ws.close();
  }
}
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  padding: 0;
  margin: 0;
  height: 100vh;
  width: 100vw;
}
.main-container {
  width: 100vw;
  height: 100vh;
}
.main-container[disabled] {
  opacity: 0.6;
}
.main-header {
  /*background-color: #eee;*/
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
}
.main-header > .el-menu {
  flex: 1;
}
.disabled-wrapper {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  cursor: not-allowed;
  background-color: #FFF;
  opacity: 0.3;
  z-index: 9999;
}
</style>
