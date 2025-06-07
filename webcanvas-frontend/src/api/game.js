import { mockGameRoomData } from "@/api/mocks/game.js";

const BASE = "/game/canvas";

export const game = {
  // 입장 가능한 방 목록 조회
  getEnterableRooms: `${BASE}/room`,
  // 게임 방 생성
  createGameRoom: `${BASE}/room`,
  // 게임 방 입장
  enterGameRoom: (gameRoomId) => `${BASE}/room/${gameRoomId}/entrance`,
  // 입장 코드로 게임 방 입장
  enterGameRoomWithJoinCode: (joinCode) => `${BASE}/room/${joinCode}/entrance`,
  // 현재 입장한 게임 방 조회
  getCurrentEnteredGameRoom: `${BASE}/room/entrance`,
  // 현재 입장한 게임 방 퇴장
  exitFromGameRoom: (gameRoomEntranceId) => `${BASE}/room/entrance/${gameRoomEntranceId}`,
  // 레디 상태 변경
  updateReady: (gameRoomEntranceId) => `${BASE}/room/entrance/${gameRoomEntranceId}/ready`,
  // 게임 시작
  startGame: `${BASE}/session`,
  // 현재 세션 조회
  getCurrentGameSession: (gameRoomId) => `${BASE}/room/${gameRoomId}/session`,
  // 현재 턴 조회
  getCurrentGameTurn: (gameSessionId) => `${BASE}/session/${gameSessionId}/turn`,
};
