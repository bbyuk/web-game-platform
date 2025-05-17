import { Client } from '@stomp/stompjs';

export const getStompClient = ({ topic, onConnect, onMessage, onError }) => {
  const client = new Client({
    webSocketFactory: () => new WebSocket("ws://localhost:9200/ws/canvas"),
    connectHeaders: {
      Authorization: `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDIiLCJmaW5nZXJwcmludCI6IjFiNWFkMTM5ZTZmMzRhMTZhMDI4OTY1YTg1MDNkYTVlIiwiaWF0IjoxNzQ3NDQ5NjY3LCJleHAiOjE3NDc0NTA1Njd9.KTY3HnCC1oVQodws7QeC_CUuYug5D_TVxieOV2f8yAg`
    },
    reconnectDelay: 5000,
    debug: (msg) => console.log("[STOMP]", msg),
    onConnect: frame => {
      console.log("STOMP Connected:", frame);

      // 구독 및 onMessage 처리
      client.subscribe(topic, (message) => {
        try {
          const payload = JSON.parse(message.body);
          console.log("STOMP Message:", payload);

          // 실제로 처리할 메시지 핸들러 (여기에 넣기)
          onMessage?.(payload); // <-- 요게 핵심
        } catch (e) {
          console.error("Failed to parse STOMP message", e);
        }
      });

      onConnect?.(frame);
    },
    onStompError: frame => {
      console.error("STOMP Error:", frame);
      onError?.(frame);
    }
  });

  client.activate();
  return client;
};
