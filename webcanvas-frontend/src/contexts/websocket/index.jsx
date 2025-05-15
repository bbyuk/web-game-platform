import { createContext, useContext, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";

const SocketContext = createContext(null);
export const useSocket = () => useContext(SocketContext);

export const SocketProvider = ({ children }) => {
  const clientRef = useRef(null);

  useEffect(() => {
    const connect = async () => {
      const client = new Client({
        brokerURL: "ws://localhost:9200/ws/canvas",
        reconnectDelay: 5000,
        debug: (msg) => console.log(msg),
      });

      client.activate();
      clientRef.current = client;
    };

    connect();

    return () => {
      clientRef.current?.deactivate();
    };
  }, []);

  return <SocketContext.Provider value={clientRef.current}>{children}</SocketContext.Provider>;
};
