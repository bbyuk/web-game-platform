import { mockGameRoomData } from "@/api/mocks/game.js";

const BASE = "/game/canvas";

export const game = {
  // 입장 가능한 방 목록 조회
  getJoinableRooms: `${BASE}/room`,
  // 게임 방 생성
  createGameRoom: `${BASE}/room`,
  // 게임 방 입장
  joinGameRoom: (gameRoomId) => `${BASE}/room/id/${gameRoomId}/participant`,
  // 입장 코드로 게임 방 입장
  joinGameRoomWithJoinCode: (joinCode) => `${BASE}/room/code/${joinCode}/participant`,
  // 현재 입장한 게임 방 조회
  getCurrentJoinedGameRoom: `${BASE}/room/detail`,
  // 현재 입장한 게임 방 퇴장
  exitFromGameRoom: (gameRoomParticipantId) => `${BASE}/room/participant/${gameRoomParticipantId}`,
  // 레디 상태 변경
  updateReady: (gameRoomParticipantId) => `${BASE}/room/participant/${gameRoomParticipantId}/ready`,
  // 게임 시작
  startGame: `${BASE}/session`,
  // 현재 세션 조회
  getCurrentGameSession: (gameRoomId) => `${BASE}/room/${gameRoomId}/session`,
  // 현재 턴 조회
  getCurrentGameTurn: (gameSessionId) => `${BASE}/session/${gameSessionId}/turn`,
};
