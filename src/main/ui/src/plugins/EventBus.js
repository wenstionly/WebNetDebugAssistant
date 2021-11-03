import Vue from 'vue'

if(!window.Bus) {
    window.Bus = new Vue();
    window.EVT_NEW_SERVER = "ns";
    window.EVT_SERVER_GONE = "sg";
    window.EVT_NEW_CLIENT = "nc";
    window.EVT_CLIENT_GONE = "cg";
    window.EVT_NEW_DATA = "nd";
}
