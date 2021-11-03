<template>
  <el-container style="height: 100%;">
    <el-aside class="flex column">
      <el-card>
        <div slot="header">
          <span>新建服务</span>
        </div>
        <el-input size="mini" v-model="newPort" placeholder="留空由服务器分配">
          <template slot="prepend">端口</template>
          <el-button slot="append" type="primary" @click="createServer">启动</el-button>
        </el-input>
      </el-card>
      <client-list class="flex-stretch"></client-list>
    </el-aside>
    <el-main>
      <el-container>
        <el-main class="main-area">
          <send-area></send-area>
          <receive-area :port="$store.state.currentServer"></receive-area>
        </el-main>
      </el-container>
    </el-main>
  </el-container>
</template>

<script>
import ClientList from "@/components/TcpServer/ClientList";
import SendArea from "@/components/TcpServer/SendArea";
import ReceiveArea from "@/components/TcpServer/ReceiveArea";
import tcp from "@/api/tcp";

export default {
  name: "TcpServer",
  components: {ReceiveArea, SendArea, ClientList},
  data() {
    return {
      newPort: '',
    };
  },
  methods: {
    createServer() {
      tcp.openServer(this.newPort).then(
          res => {
            this.$message({
              message: '成功',
              type: 'success'
            });
          },
          err => {
            this.$message({
              message: '失败: ' + err.response.data.message,
              type: 'warning'
            });
          }
      );
    }
  }
}
</script>

<style scoped>
  .el-main {
    padding-top: 0;
    padding-bottom: 0;
  }
  .el-main > .el-container {
    height: 100%;
  }
  .main-area {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: stretch;
  }
  .main-area > .el-card,
  .el-aside > .el-card {
    margin-bottom: 10px;
  }
  .main-area > div:last-child {
    flex: 1;
  }
</style>