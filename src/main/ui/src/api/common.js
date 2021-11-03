import axios from 'axios';
import {nanoid} from "nanoid";

const SID_KEY = "sid";
const API_BASE = "/api/";
let _ = {
    getSid() {
        let sid = localStorage.getItem(SID_KEY);
        if(!sid) {
            sid = nanoid();
            localStorage.setItem(SID_KEY, sid);
        }
        return sid;
    },
    resetSid() {
        localStorage.removeItem(SID_KEY);
        return _.getSid();
    },
    setSid(sid) {
        if(sid)
            localStorage.setItem(SID_KEY, sid);
        return _.getSid();
    },

    request(_method, _url, _data, _param) {
        if(!_param)
            _param = {};
        return axios({
            method: _method,
            url: API_BASE + _url,
            headers: _data ? {'Content-Type': 'application/json'} : undefined,
            params: _param,
            data: _data,
            withCredentials: true,
            responseType: 'json'
        });
    },
    get(_url, _param) {
        return _.request('GET', _url, null, _param);
    },
    post(_url, _data, _param) {
        return _.request('POST', _url, _data, _param);
    },
    put(_url, _data, _param) {
        return _.request('PUT', _url, _data, _param);
    },
    del(_url, _params) {
        return _.request('DELETE', _url, null, _params);
    },

};

export default _;