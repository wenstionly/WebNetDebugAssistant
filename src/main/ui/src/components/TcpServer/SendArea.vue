<template>
  <el-card>
    <div slot="header" class="flex row center middle">
      <span>发送区</span>
      <el-radio-group v-model="mode" size="mini" :disabled="sending">
        <el-radio-button label="basic">简洁</el-radio-button>
        <el-radio-button label="advance">高级</el-radio-button>
      </el-radio-group>
    </div>
    <basic-send-area v-show="mode === 'basic'" @sendingStatusChanged="handleSendingStatusChanged"></basic-send-area>
    <advance-send-area v-show="mode === 'advance'" @sendingStatusChanged="handleSendingStatusChanged"></advance-send-area>
  </el-card>
</template>

<script>
import BasicSendArea from "@/components/TcpServer/BasicSendArea";
import AdvanceSendArea from "@/components/TcpServer/AdvanceSendArea";
export default {
  name: "SendArea",
  components: {AdvanceSendArea, BasicSendArea},
  data() {
    return {
      mode: 'basic',
      sending: false,
    };
  },
  methods: {
    handleSendingStatusChanged(status) {
      this.sending = status;
    }
  }
}
</script>

<style scoped>

</style>