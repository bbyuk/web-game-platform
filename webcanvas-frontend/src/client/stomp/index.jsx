import { Client } from "@stomp/stompjs";
import { STORAGE_KEY } from "@/constants/storage-key.js";

export const getWebSocketClient = ({ onConnect, onError }) => {
  const client = new Client({
    webSocketFactory: () => new WebSocket("ws://localhost:9200/ws/canvas"),
    connectHeaders: {
      Authorization: `Bearer ${localStorage.getItem(STORAGE_KEY.ACCESS_TOKEN)}`,
    },
    reconnectDelay: 5000,
    debug: (msg) => console.log("[STOMP]", msg),
    onConnect: (frame) => {
      console.log("STOMP Connected:", frame);
      onConnect?.(frame);
    },
    onStompError: (frame) => {
      console.error("STOMP Error:", frame);
      onError?.(frame);
    },
  });

  client.activate();
  return client;
};

export const subscribe = (client, topics) => {
  topics.forEach((topic) => {
    client.subscribe(topic.destination, (message) => {
      try {
        const payload = JSON.parse(message.body);
        console.log("STOMP Message:", payload);

        topic.messageHandler?.(payload);
      } catch (e) {
        console.error("Failed to parse STOMP message", e);
      }
    });
  });
};
