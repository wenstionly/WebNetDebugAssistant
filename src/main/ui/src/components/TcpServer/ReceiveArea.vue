<template>
  <el-card class="stretch">
    <template slot="header">接收区</template>
    <div class="flex column h100p">
      <div class="flex row both-ends">
        <el-checkbox size="mini" v-model="hexMode">按16进制显示</el-checkbox>
        <div>
          <el-checkbox size="mini" v-model="showAddress">显示地址</el-checkbox>
          <el-checkbox size="mini" v-model="showTime">显示时间</el-checkbox>
        </div>
        <div>
          <el-button size="mini" @click="handleClear">清空</el-button>
        </div>
      </div>
      <div class="flex-stretch">
        <data-view :value="dataList" :hex="hexMode" :showTime="showTime" :showAddress="showAddress"></data-view>
      </div>
    </div>
  </el-card>
</template>

<script>
import {mapState} from "vuex";
import DataView from "@/components/common/DataView";
export default {
  name: "ReceiveArea",
  components: {DataView},
  props: {
    port: {
      type: String|Number,
      default: '',
    },
    clientId: {
      type: String|Number,
      default: '',
    }
  },
  data() {
    return {
      showAddress: true,
      showTime: true,
      hexMode: false
    };
  },
  computed: {
    ...mapState(['serverData']),
    dataList() {
      try {
        if (this.port && this.clientId) {
          return this.serverData[this.port] && this.serverData[this.port].clients[this.clientId] || [];
        } else {
          return this.serverData[this.port].data || [];
        }
      }
      catch(e) {
        return [];
      }
    }
  },
  methods: {
    handleClear() {
      this.$store.commit('clearData', this.$store.state.currentServer);
    }
  }
}
</script>

<style scoped>

</style>