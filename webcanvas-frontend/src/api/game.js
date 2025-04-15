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
  getCurrentEnteredGameRoom: `${BASE}/room/entrance`
};
