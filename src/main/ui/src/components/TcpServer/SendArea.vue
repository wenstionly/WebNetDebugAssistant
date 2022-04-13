<template>
  <el-card>
    <template slot="header">发送区</template>
    <div class="flex column">
      <div class="flex row both-ends">
        <el-checkbox size="mini" v-model="hexMode">按16进制发送</el-checkbox>
        <el-checkbox size="mini" v-model="multiMode">按行拆分发送</el-checkbox>
        <div>
          <el-button size="mini" type="primary" @click="handleSend">发送</el-button>
          <el-button size="mini" @click="handleClear">清空</el-button>
        </div>
      </div>
      <el-input type="textarea" v-model="data"></el-input>
    </div>
  </el-card>
</template>

<script>
import {mapState} from "vuex";
import tcp from "@/api/tcp";

function checkHex(hexStr) {
  const trimmed = hexStr.replaceAll(' ', '');
  if(!/^[0-9A-Fa-f]*$/.exec(trimmed))
    return null;
  if(trimmed.length & 0x01)
    return null;
  return trimmed;
}
function toHex(rawStr) {
  const arr = [];
  for(let i = 0, l = rawStr.length; i < l; i++) {
    arr.push(('0'+rawStr.charCodeAt(i).toString(16)).substr(-2));
  }
  return arr.join('');
}
export default {
  name: "SendArea",
  data() {
    return {
      hexMode: false,
      multiMode: false,
      data: '',
    };
  },
  computed: {
    ...mapState(['selectedClients']),
    selectedClientIdList() {
      const selectedClients = this.selectedClients;
      const list = [];
      for(let id in selectedClients) {
        if(selectedClients.hasOwnProperty(id) && selectedClients[id] === true)
          list.push(id);
      }
      return list;
    }
  },
  methods: {
    proceedData() {
      let data = [];
      const lines = this.multiMode ? this.data.split('\n') : [this.data];
      lines.forEach(line => {
        line = this.hexMode ? checkHex(line) : toHex(line);
        if(line)
          data.push(line);
      });
      return data;
    },
    handleSend() {
      if(!this.data || !this.selectedClientIdList.length) {
        this.$message({
          message: '请输入要发送的内容并至少选择一个客户端',
          type: 'warning'
        });
        return;
      }
      const lines = this.proceedData();
      if(lines.length <= 0) {
        this.$message({
          message: '发送内容不正确，请检查',
          type: 'warning'
        });
        return;
      }
      tcp.sendLines(this.selectedClientIdList, lines).then(
          res => {
            this.$message({
              message: '发送完成',
              type: 'success'
            });
          },
          err => {
            this.$message({
              message: '失败: ' + err.response.data.message,
              type: 'warning'
            });
          }
      )
    },
    handleClear() {
      this.data = '';
    },
  }
}
</script>

<style scoped>

</style>