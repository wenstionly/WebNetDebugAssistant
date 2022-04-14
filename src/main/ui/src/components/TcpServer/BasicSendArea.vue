<template>
  <div class="flex column">
    <div class="flex row both-ends">
      <el-checkbox size="mini" v-model="hexMode">按16进制发送</el-checkbox>
      <div class="flex row middle">
        <el-checkbox size="mini" v-model="multiMode">按行拆分发送</el-checkbox>
        <el-input v-model="duration" size="mini" style="width:200px;" :disabled="!multiMode">
          <template slot="prepend">间隔</template>
          <template slot="append">毫秒</template>
        </el-input>
      </div>
      <div>
        <el-button size="mini" type="primary" @click="handleSend" :disabled="sending">发送</el-button>
        <el-button size="mini" @click="handleClear">清空</el-button>
      </div>
    </div>
    <el-input type="textarea" v-model="data"></el-input>
  </div>
</template>

<script>
import {mapState} from "vuex";
import tcp from "@/api/tcp";
export default {
  name: "BasicSendArea",
  data() {
    return {
      hexMode: false,
      multiMode: false,
      duration: 0,
      data: '',
      sending: false,
    };
  },
  watch: {
    sending() {
      this.$emit('sendingStatusChanged', this.sending);
    }
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
        line = this.hexMode ? tcp.checkHex(line) : tcp.toHex(line);
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
      if(lines.length === 1) {
        // 只有一行时，直接发送
        tcp.send(this.selectedClientIdList, lines[0]).then(
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
        );
      }
      else {
        console.log('逐行发送');
        ((clients, lines) => {
          const max = lines.length;
          let index = 0;
          this.sending = true;

          const sendLine = () => {
            console.log('sendline', lines[index], this.duration);
            tcp.send(clients, lines[index++]).then(
                res => {
                  if (index >= max) {
                    this.sending = false;
                    this.$message({
                      message: '发送完成',
                      type: 'success'
                    });
                  } else {
                    setTimeout(() => {
                      sendLine();
                    }, this.duration);
                  }
                },
                err => {
                  this.$message({
                    message: '发送第' + index + '行数据时失败: ' + err.response.data.message,
                    type: 'warning'
                  });
                }
            )
          };
          sendLine();
        })(this.selectedClientIdList, lines);
      }
    },
    handleClear() {
      this.data = '';
    },
  }
}
</script>

<style scoped>

</style>