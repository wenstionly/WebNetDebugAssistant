<template>
  <div class="data-container">
    <div class="data-view" :class="reverse ? 'is-reverse' : ''">
      <div class="data-block" v-for="(pkg,idx) in value" :key="idx">
        <div class="title" v-if="showTime || showAddress">
          <span v-if="showAddress">{{pkg.address}}:{{pkg.port}}</span>
          <span v-if="showTime">{{pkg.time}}</span>
        </div>
        <div class="content" v-html="displayData(pkg)"></div>
      </div>
    </div>
  </div>
</template>

<script>
import tcp from '@/api/tcp';
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
    },
    reverse: {
      type: Boolean,
      default: false,
    },
    encoding: {
      type: String,
      default: 'utf8',
    }
  },
  computed: {
    displayData() {
      return pkg => {
        if(this.hex)
          return tcp.formatHex(pkg.data);
        else {
          return tcp.fromHex(pkg.data, this.encoding).replace(/(\r\n|\n)/, '<br/>');
        }
      };
    }
  }
}
</script>

<style scoped lang="scss">
  .data-container {
    height: 100%;
    box-sizing: border-box;
    overflow-y: auto;
    padding: 5px 15px;
    color: #606266;
    background-color: #fff;
    border: 1px solid #C0C4CC;
    border-radius: 4px;
    transition: border-color .2s cubic-bezier(.645, .045, .355, 1);

    .data-view {
      display: flex;
      flex-direction: column;

      &.is-reverse {
        flex-direction: column-reverse;
      }

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
  }
</style>