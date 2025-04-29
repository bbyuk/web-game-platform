import { mockGameRoomData } from "@/api/mocks/game.js";

const BASE = "/game/canvas";

export const game = {
  // 입장 가능한 방 목록 조회
  getEnterableRooms: {
    url: `${BASE}/room`,
    mock: mockGameRoomData.enterableRooms,
  },
  // 게임 방 생성
  createGameRoom: {
    url: `${BASE}/room`,
    mock: mockGameRoomData.createdRoom,
  },
  // 게임 방 입장
  enterGameRoom: {
    url: (gameRoomId) => `${BASE}/room/${gameRoomId}/entrance`,
    mock: mockGameRoomData.enteredRoom,
  },
  // 입장 코드로 게임 방 입장
  enterGameRoomWithJoinCode: {
    url: (joinCode) => `${BASE}/room/${joinCode}/entrance`,
    mock: mockGameRoomData.enteredRoom,
  },
  // 현재 입장한 게임 방 조회
  getCurrentEnteredGameRoom: {
    url: `${BASE}/room/entrance`,
    mock: mockGameRoomData.enteredRoom,
  },
};
