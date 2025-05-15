import { Client } from "@stomp/stompjs";

let socketClient = null;

export const getSocketClient = () => {
  return socketClient;
};

export const createSocketClient = (accessToken) => {
  socketClient = new Client({
    brokerURL: "ws://localhost:9200/ws/canvas",

    connectHeaders: {
      Authorization: `Bearer ${accessToken}`,
    },

    debug: (str) => console.log(str),
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });
};
