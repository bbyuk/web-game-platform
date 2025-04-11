import React from 'react';

export default function GameRoomList({
                                       rooms = []
                                       , onEnterButtonClick = (id) => {
  }
                                     }) {
  const handleEnter = (roomId) => {
    console.log(`방 ${roomId} 입장!`);
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <h2 className="text-2xl font-bold mb-4 text-gray-800">목록</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {rooms.map((room) => (
          <div
            key={room.id}
            className="bg-white rounded-xl shadow hover:shadow-lg p-5 flex flex-col justify-between transition"
          >
            <div>
              <h3 className="text-lg font-semibold text-gray-900">{room.joinCode}</h3>
              <p className="text-sm text-gray-500">({room.enterCount} / {room.capacity})</p>
            </div>
            <button
              onClick={() => onEnterButtonClick(room.gameRoomId)}
              className="mt-4 bg-blue-500 hover:bg-blue-600 text-white py-2 px-4 rounded-md text-sm"
            >
              입장하기
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
