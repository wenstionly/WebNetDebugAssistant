<template>
  <el-collapse-item class="condensed" v-bind="$attrs" :name="server.port">
    <div slot="title" class="flex row both-ends middle flex-stretch">
      <div style="margin-left: 10px;">
<!--        <span>端口：</span>-->
        <span>{{server.address}}:{{server.port}}</span>
      </div>
      <el-button type="warning" size="mini" @click.stop="handleCloseServer">关闭</el-button>
    </div>
    <div class="flex column top" v-if="server.clients && server.clients.length > 0">
      <div class="client" v-for="c in server.clients" :index="c.id">
        <el-checkbox :value="isClientSelected(c.id)" @change="handleChangeSelection(c)">
          <span>{{c.address}}:{{c.port}}</span>
        </el-checkbox>
        <el-button size="mini" type="warning" @click="handleCloseClient(c)">下线</el-button>
      </div>
      <el-form inline size="mini">
        <el-form-item>
          <el-button size="mini" @click="handleSelectAllClient">全选</el-button>
          <el-button size="mini" @click="handleDeselectAllClient">全不选</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="flex row center middle no-client" v-else>
      <span>暂无连接</span>
    </div>
  </el-collapse-item>
</template>

<script>
import {mapGetters} from 'vuex';

import tcp from "@/api/tcp";
export default {
  name: "ClientInfo",
  props: {
    server: Object,
  },
  data() {
    return {
      selectedClients: {}
    };
  },
  computed: {
    ...mapGetters(['isClientSelected']),
  },
  methods: {
    handleCloseServer() {
      tcp.closeServer(this.server.port).then(
          res => {
            this.$message({
              message: "服务已关闭",
              type: 'success',
            });
          },
          err => {
            this.$message({
              message: '失败:' + err.response.data.meessage,
              type: 'warning'
            });
          }
      )
    },
    handleCloseClient(client) {
      tcp.offline(client.id).then(
          res => {
            this.$message({
              message: "已将客户端强制下线",
              type: 'success'
            });
          },
          err => {
            this.$message({
              message: '失败:' + err.response.data.meessage,
              type: 'warning'
            });
          }
      )
    },

    handleChangeSelection(client) {
      this.$store.commit('toggleSelectClient', client.id);
    },
    handleSelectAllClient() {
      const selected = {};
      this.server.clients.forEach(client => {
        selected[client.id] = true;
      });
      this.$store.commit('setClientSelection', selected);
    },
    handleDeselectAllClient() {
      this.$store.commit('setClientSelection', {});
    }
  }
}
</script>

<style scoped>
  .no-client {
    width: 100%;
    height: 100%;
    color: #909399;
    line-height: 60px;
    font-size: 14px;
  }
  .client {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 10px 10px;
    border-bottom: solid 1px #eee;
  }
</style>