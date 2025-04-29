export const mockGameRoomData = {
  // 게임 방에 입장한 유저 목록
  enteredUsers: [
    { label: "Alice", color: "#FF5733", isButton: false },
    { label: "Bob", color: "#33A1FF", isButton: false },
    { label: "Charlie", color: "#8D33FF", isButton: false },
  ],
  // 캔버스 컬러 목록
  canvasColors: [
    { label: "black" },
    { label: "blue" },
    { label: "green" },
    { label: "red" },
    { label: "yellow" },
  ],
  // 입장 가능한 방 목록
  enterableRooms: [
    { label: "ABCD123456", current: 2, capacity: 5, isButton: true },
    { label: "WXYZ987654", current: 1, capacity: 4, isButton: true },
    { label: "QWER112233", current: 5, capacity: 5, isButton: false },
  ],
  // 생성된 방 정보
  createdRoom: {},
  // 입장한 방 정보
  enteredRoom: {},
};
