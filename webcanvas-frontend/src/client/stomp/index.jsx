import { Client } from "@stomp/stompjs";
import { STORAGE_KEY } from "@/constants/storage-key.js";

export const getWebSocketClient = ({ onConnect, onError }) => {
  const clientWrapper = {
    client: new Client({
      webSocketFactory: () => new WebSocket("ws://localhost:9200/ws/canvas"),
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem(STORAGE_KEY.ACCESS_TOKEN)}`,
      },
      reconnectDelay: 5000,
      debug: (msg) => {},
      onConnect: (frame) => {
        clientWrapper.isConnected = true;

        if (clientWrapper.subscribeTopicQueue.length > 0) {
          clientWrapper.subscribe(clientWrapper.subscribeTopicQueue);
          clientWrapper.subscribeTopicQueue.length = 0;
        }

        onConnect?.(frame);
      },
      onStompError: (frame) => {
        onError?.(frame);
      },
    }),
    isConnected: false,
    deactivate: () => {
      clientWrapper.client.deactivate();
    },
    subscribeTopicQueue: [],
    subscribe: (topics) => {
      // 연결되어 있다면 바로 구독 처리 / 연결되어 있지 않다면 토픽 큐에 담아두었다가 onConnect 시점에 lazy subscribe
      if (clientWrapper.isConnected) {
        topics.forEach((topic) => {
          clientWrapper.client.subscribe(topic.destination, (message) => {
            try {
              const payload = JSON.parse(message.body);
              topic.messageHandler?.(payload);
            } catch (e) {
              console.error("Failed to parse STOMP message", e);
            }
          });
        });
      } else {
        clientWrapper.subscribeTopicQueue.push(...topics);
      }
    },
    send: (destination, data) => {
      clientWrapper.client.publish({
        destination: destination,
        body: JSON.stringify(data)
      })
    }
  };

  clientWrapper.client.activate();
  return clientWrapper;
};
