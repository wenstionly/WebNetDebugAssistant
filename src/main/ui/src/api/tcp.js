import common from "@/api/common";
import iconv from 'iconv-lite';

let _ = {
    checkHex(hexStr) {
        const trimmed = hexStr.replaceAll(' ', '');
        if (!/^[0-9A-Fa-f]*$/.exec(trimmed))
            return null;
        if (trimmed.length & 0x01)
            return null;
        return trimmed;
    },
    toHex(rawStr) {
        const arr = [];
        for (let i = 0, l = rawStr.length; i < l; i++) {
            arr.push(('0' + rawStr.charCodeAt(i).toString(16)).substr(-2));
        }
        return arr.join('');
    },
    formatHex(hexStr) {
        const arr = [];
        for (let i = 0, l = hexStr.length; i < l; i += 2) {
            arr.push(hexStr.substr(i, 2));
        }
        return arr.join(' ');
    },
    fromHex(hexStr, encoding) {
        const out = [];
        for (let i = 0, l = hexStr.length; i < l; i += 2) {
            out.push(Number.parseInt('0x' + hexStr.substr(i, 2)));
        }
        return iconv.decode(out, encoding);
    },
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
    },
    sendLines(clients, lines) {
        return common.post(_.baseUrl() + "send/lines", {
            clients: clients,
            lines: lines
        })
    }
};

export default _;
