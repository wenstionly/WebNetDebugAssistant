<template>
  <el-card class="stretch scroll">
    <div slot="header">
      <span>服务列表</span>
    </div>
    <span v-if="!servers || !servers.length">暂无服务</span>
    <el-collapse accordion v-else :value="currentServer" @change="handleChangeServer">
      <template v-for="s in servers">
        <client-info :server="s"></client-info>
      </template>
    </el-collapse>
  </el-card>
</template>

<script>
import ClientInfo from "@/components/TcpServer/ClientInfo";
import {mapState} from "vuex";

export default {
  name: "ClientList",
  components: {ClientInfo},
  data() {
    return {
      selectedServer: '',
    };
  },
  computed: {
    ...mapState(['servers', 'currentServer']),
  },
  methods: {
    handleChangeServer(server) {
      this.$store.commit('changeCurrentServer', server);
    }
  }
}
</script>

<style scoped>

</style>