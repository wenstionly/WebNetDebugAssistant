<template>
  <div class="flex column">
    <div class="flex row right middle">
      <el-checkbox size="mini" :disabled="sending" v-model="repeat">循环发送</el-checkbox>
      <el-button size="mini" type="primary" :disabled="sending" @click="handleSend">发送</el-button>
      <el-button size="mini" type="warning" :disabled="!sending" @click="handleStop">停止</el-button>
    </div>
    <div class="data-list">
      <el-table max-height="300" :data="dataList" row-key="id" highlight-current-row :current-row-key="sendingRowKey">
        <el-table-column width="40" :render-header="selectionColumnHeaderRenderer">
          <template slot-scope="scope">
            <el-checkbox size="mini" v-model="scope.row.isSelected" @change="handleItemSelectionChanged"></el-checkbox>
          </template>
        </el-table-column>
        <el-table-column label="Hex" width="55">
          <template slot-scope="scope">
            <el-checkbox size="mini" v-model="scope.row.isHex"></el-checkbox>
          </template>
        </el-table-column>
        <el-table-column label="发送前延迟" width="90">
          <template slot-scope="scope">
            <el-input size="mini" v-model="scope.row.delay" placeholder="毫秒"></el-input>
          </template>
        </el-table-column>
        <el-table-column label="内容">
          <template slot-scope="scope">
            <el-input size="mini" v-model="scope.row.data"></el-input>
          </template>
        </el-table-column>
        <el-table-column width="60">
          <template slot="header">
            <el-tooltip content="在最前面增加" placement="top">
              <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAddHead"></el-button>
            </el-tooltip>
            <el-tooltip content="全部清空" placement="top">
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleClearAll"></el-button>
            </el-tooltip>
          </template>
          <template slot-scope="scope">
            <el-tooltip content="在后面增加" placement="top">
              <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAddAfter(scope.$index)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除本行">
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRemoveItem(scope.$index)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
      <div class="disabled-wrapper" v-if="sending"></div>
    </div>
  </div>
</template>

<script>
import {mapState} from "vuex";
import {nanoid} from "nanoid";
import tcp from "@/api/tcp";

function DataItem() {
  return {
    id: nanoid(),
    isSelected: true,
    isHex: false,
    delay: 0,
    data: ''
  };
}
const STORAGE_NAME = 'AdvanceSendArea.storage';
export default {
  name: "AdvanceSendArea",
  data() {
    this.sendingTimer = null;
    return {
      isAllSelected: true,
      isIndeterminate: false,

      dataList: [],

      repeat: false,
      sending: false,
      sendingRowKey: '', // 正在发送的数据在原始列表中的序号
      sendingIndex: -1, // 正在发送的数据在待发列表中的序号
      sendingDataList: [],
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
  watch: {
    dataList: {
      deep: true,
      handler() {
        localStorage.setItem(STORAGE_NAME, JSON.stringify(this.dataList));
      }
    },
    sending() {
      this.$emit('sendingStatusChanged', this.sending);
    }
  },
  methods: {
    selectionColumnHeaderRenderer(h, data) {
      return h('span', [
          h('el-checkbox', {
            on: {
              change: this.handleSelectAllChanged,
            },
            props: {
              value: this.isAllSelected,
              indeterminate: this.isIndeterminate,
            }
          })
      ]);
    },
    handleAddHead() {
      if(this.sending)
        return;
      let newArray = [DataItem()];
      newArray.push.apply(newArray, this.dataList);
      this.$set(this, 'dataList', newArray);
    },
    handleClearAll() {
      if(this.sending)
        return;
      this.$set(this, 'dataList', []);
    },
    handleAddAfter(index) {
      if(this.sending)
        return;
      if(index >= this.dataList.length - 1)
        this.dataList.push(DataItem());
      else {
        let itemsAfter = this.dataList.splice(index + 1);
        this.dataList.push(DataItem());
        this.dataList.push.apply(this.dataList, itemsAfter);
      }
    },
    handleRemoveItem(index) {
      if(this.sending)
        return;
      this.dataList.splice(index, 1);
    },
    handleSelectAllChanged() {
      this.isAllSelected = !this.isAllSelected;
      this.isIndeterminate = false;
      this.dataList.forEach(item => {
        this.$set(item, 'isSelected', this.isAllSelected);
      });
    },
    handleItemSelectionChanged() {
      let selectedCount = 0;
      this.dataList.forEach(item => {
        if(item.isSelected)
          selectedCount++;
      });
      this.isAllSelected = (selectedCount === this.dataList.length);
      this.isIndeterminate = !this.isAllSelected && selectedCount > 0;
    },

    prepareSendingData() {
      this.sendingDataList = [];
      this.dataList.forEach((item, index) => {
        if(item.isSelected) {
          const data = item.isHex ? tcp.checkHex(item.data) : tcp.toHex(item.data);
          if(data) {
            this.sendingDataList.push({
              rowIndex: index + 1,
              id: item.id,
              delay: item.delay,
              data: data,
            });
          }
        }
      });
    },
    handleSend() {
      if(this.sending)
        return;
      this.prepareSendingData();
      if(this.sendingDataList.length <= 0) {
        this.$message({
          message: '请至少选择一条有效数据',
          type: 'warning'
        });
        return;
      }
      if(!this.selectedClientIdList.length) {
        this.$message({
          message: '请至少选择一个客户端',
          type: 'warning'
        });
        return;
      }
      // 找到第一条被选中的内容
      this.sendingIndex = 0;
      this.sendingRowKey = this.sendingDataList[0].id;
      this.sending = true;
      this.doSend();
    },
    handleStop() {
      this.sending = false;
      if(this.sendingTimer)
        clearTimeout(this.sendingTimer);
      this.sendingTimer = null;
    },

    doSend() {
      if(!this.sending) {
        return;
      }
      this.sendingTimer = setTimeout(() => {
        this.$nextTick(() => {
          this.sendingTimer = null;
          tcp.send(this.selectedClientIdList, this.sendingDataList[this.sendingIndex].data).then(
              res => {
                // 发送成功，移动到下一条数据
                this.sendingIndex++;
                if(this.sendingIndex >= this.sendingDataList.length) {
                  if(this.repeat)
                    this.sendingIndex = 0;
                  else {
                    this.handleStop();
                    return;
                  }
                }
                this.$set(this, 'sendingRowKey', this.sendingDataList[this.sendingIndex].id);
                this.doSend();
              },
              err => {
                this.$message({
                  message: '发送第' + this.sendingDataList[this.sendingIndex].rowIndex + '行数据时失败: ' + err.response.data.message,
                  type: 'warning'
                });
                this.handleStop();
              }
          )
        });
      }, this.sendingDataList[this.sendingIndex].delay);
    }
  },
  beforeMount() {
    try {
      this.dataList = JSON.parse(localStorage.getItem(STORAGE_NAME) || '[]');
    }
    catch(e) {
      this.dataList = [];
    }
    this.handleItemSelectionChanged();
  },
  beforeDestroy() {
    this.sending = false;
    if(this.sendingTimer)
      clearTimeout(this.sendingTimer);
  }
}
</script>

<style scoped>
  .data-list {
    position: relative;
  }
  .disabled-wrapper {
    position: absolute;
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