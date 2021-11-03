<template>
  <div class="data-view">
    <div class="data-block" v-for="(pkg,idx) in value" :key="idx">
      <div class="title" v-if="showTime || showAddress">
        <span v-if="showAddress">{{pkg.address}}:{{pkg.port}}</span>
        <span v-if="showTime">{{pkg.time}}</span>
      </div>
      <div class="content" v-html="displayData(pkg)"></div>
    </div>
  </div>
</template>

<script>
function formatHex(hexStr) {
  const arr = [];
  for(let i = 0, l = hexStr.length; i < l; i += 2) {
    arr.push(hexStr.substr(i, 2));
  }
  return arr.join(' ');
}
function fromHex(hexStr) {
  const out = [];
  for(let i = 0, l = hexStr.length; i < l; i += 2) {
    out.push(String.fromCharCode('0x' + hexStr.substr(i, 2)));
  }
  return out.join('');
}
export default {
  name: "DataView",
  props: {
    value: {
      type: Array,
      default: () => [],
    },
    hex: {
      type: Boolean,
      default: false,
    },
    showTime: {
      type: Boolean,
      default: true,
    },
    showAddress: {
      type: Boolean,
      default: true,
    }
  },
  computed: {
    displayData() {
      return pkg => {
        if(this.hex)
          return formatHex(pkg.data);
        else {
          return fromHex(pkg.data);
        }
      };
    }
  }
}
</script>

<style scoped lang="scss">
  .data-view {
    height: 100%;
    max-height: 100%;
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    padding: 5px 15px;
    color: #606266;
    background-color: #fff;
    border: 1px solid #C0C4CC;
    border-radius: 4px;
    transition: border-color .2s cubic-bezier(.645,.045,.355,1);

    .data-block {
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
      align-items: flex-start;
      .title {
        font-weight: bold;
      }
      .title > span:not(:last-child) {
        margin-right: 2em;
      }
    }
  }
</style>