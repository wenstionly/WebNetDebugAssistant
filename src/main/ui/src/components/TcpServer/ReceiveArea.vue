<template>
  <el-card class="stretch">
    <template slot="header">接收区</template>
    <div class="flex column h100p">
      <div class="flex row both-ends">
        <div>
          <el-checkbox size="mini" v-model="reverse">倒序显示</el-checkbox>
          <el-checkbox size="mini" v-model="hexMode">按16进制显示</el-checkbox>
          <el-select v-model="encoding" size="mini" style="width:100px;">
            <el-option value="utf8" label="UTF-8"></el-option>
            <el-option value="gbk" label="GBK"></el-option>
          </el-select>
          <el-checkbox size="mini" v-model="showAddress">显示地址</el-checkbox>
          <el-checkbox size="mini" v-model="showTime">显示时间</el-checkbox>
        </div>
        <div>
          <el-button size="mini" @click="handleClear">清空</el-button>
        </div>
      </div>
      <div class="flex-stretch">
        <data-view :value="dataList"
                   :encoding="encoding"
                   :hex="hexMode"
                   :showTime="showTime"
                   :showAddress="showAddress"
                   :reverse="reverse"></data-view>
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
      reverse: false,
      hexMode: false,

      encodingList: [
        {name: 'utf8', label: 'UTF-8'},
        {name: 'gbk', label: 'GBK'},
        {name: 'gb2312', label: 'GB2312'},
        {name: 'gb18030', label: 'GB18030'},
      ],
      encoding: 'utf8',
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