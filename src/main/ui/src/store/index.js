import Vue from 'vue'
import Vuex from 'vuex'
import common from "@/api/common";
import tcp from "@/api/tcp";

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    sid: common.getSid(),
    currentServer: '',
    selectedClients: {},
    servers: [
      /**
       * {
       *   port: xxxx,
       *   clients: [
       *     {
       *       id: xxxxx,
       *       address: xxxxx,
       *       port: xxxx
       *     }
       *   ]
       * }
       */
    ],
    clients: {
      /**
       * id: {
       *     address: xxxx,
       *     port: xxxx
       * }
       */
    },
    serverData: {
      /**
       * port: {
       *     data: [
       *         {
       *             id: "xxx",
       *             address: xxx,
       *             port: xxx,
       *             time: xxx,
       *             data: xxx
       *         }
       *     ],
       *     clients: {
       *         id: []
       *     }
       * }
       */
    },
  },
  mutations: {
    resetSid(state) {
      state.sid = common.resetSid();
    },
    setSid(state, sid) {
      state.sid = common.setSid(sid);
    },
    updateServer(state, servers) {
      const clients = {};
      const lastServer = state.currentServer;
      const selectedClients = {};
      state.currentServer = '';
      servers.forEach(server => {
        server.port = '' + server.port;
        if(lastServer && server.port == lastServer)
          state.currentServer = server.port;
        server.clients.forEach(client => {
          client.serverPort = server.port;
          clients[client.id] = client;
          if(state.selectedClients[client.id])
            selectedClients[client.id] = true;
        });
      });
      Vue.set(state, 'servers', servers);
      Vue.set(state, 'clients', clients);
      Vue.set(state, 'selectedClients', selectedClients);
    },
    changeCurrentServer(state, server) {
      const s = server;
      server = '';
      state.servers.forEach(ss => {
        if(ss.port == s) {
          server = ss.port;
        }
      });
      state.currentServer = server;
    },
    toggleSelectClient(state, id) {
      Vue.set(state.selectedClients, id, !state.selectedClients[id]);
    },
    selectClient(state, id, selection) {
      Vue.set(state.selectedClients, id, !!selection);
    },
    setClientSelection(state, selection) {
      Vue.set(state, 'selectedClients', selection || {});
    },
    appendData(state, pkgData) {
      /**
       * pkgData: {
       *     port: xxx,
       *     id: xxx,
       *     ts: xxx,
       *     data: xxx
       * }
       */
      console.log("appendData", pkgData);
      if(!pkgData || !pkgData.id || !pkgData.data || !state.clients[pkgData.id])
        return;
      console.log("proceed data appending");
      const client = state.clients[pkgData.id];
      const dataItem = {
        id: client.id,
        address: client.address,
        port: client.port,
        time: pkgData.ts,
        data: pkgData.data,
      }
      const portItem = state.serverData[state.clients[pkgData.id].serverPort] || {data: [], clients: {}};
      portItem.data.push(dataItem);
      if(!portItem.clients[client.id])
        portItem.clients[client.id] = [];
      portItem.clients[client.id].push(dataItem);
      Vue.set(state.serverData, state.clients[pkgData.id].serverPort, portItem);
      console.log("data appended", state.serverData);
    },
    clearData(state, port) {
      if(typeof port !== 'string' || !port)
        return;
      Vue.set(state.serverData, port, {data: [], clients: {}});
    }
  },
  getters: {
    isClientSelected(state) {
      return clientId => {
        return state.selectedClients[clientId] || false;
      }
    }
  },
  actions: {
    updateServer({commit}) {
      tcp.getServerList().then(
          res => {
            commit('updateServer', res.data);
          },
          err => {
            commit('updateServer', []);
          }
      ).catch(e => {
        console.error("update server list error!", e);
      });
    }
  },
  modules: {
  }
})
