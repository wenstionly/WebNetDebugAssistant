import common from "@/api/common";

let _ = {
    baseUrl() {
        return common.getSid() + "/tcp/server/";
    },
    getServerList() {
        return common.get(_.baseUrl() + "list");
    },
    openServer(port) {
        return common.post(_.baseUrl() + "open" + (port ? `/${port}` : ''));
    },
    closeServer(port) {
        return common.del(_.baseUrl() + "close/" + port);
    },

    offline(clientId) {
        return common.del(_.baseUrl() + "offline/" + clientId);
    },
    send(clients, data) {
        return common.post(_.baseUrl() + "send", {
            clients: clients,
            data: data
        });
    }
};

export default _;
