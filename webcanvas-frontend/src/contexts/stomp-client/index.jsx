import {useEffect, useRef} from "react";
import {Client} from "@stomp/stompjs";

export const useStompClient = ({ onConnect, onMessage, onError, topic }) => {
  const clientRef = useRef(null);

  useEffect(() => {
    const client = new Client({
      brokerURL: "ws://localhost:9200/ws/canvas",
      reconnectDelay: 5000,
      debug: (msg) => console.log("[STOMP]", msg),
      onConnect: (frame) => {
        onConnect?.(frame);
        if (topic && onMessage) {
          client.subscribe(topic, (message) => {
            onMessage(JSON.parse(message.body));
          });
        }
      },
      onStompError: (frame) => {
        console.error("[STOMP Error]", frame);
        onError?.(frame);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [topic, onConnect, onMessage, onError]);

  return clientRef;
};
