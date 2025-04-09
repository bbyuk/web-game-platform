import React from "react";

const gameRooms = [
    { id: 1, name: "SDW12D", players: 3, host: "hyuk" },
    { id: 2, name: "DS2DS1", players: 4, host: "mirae" },
    { id: 3, name: "DSI21O", players: 2, host: "hoon" },
];

export default function GameRoomList() {
    const handleEnter = (roomId) => {
        console.log(`방 ${roomId} 입장!`);
    };

    return (
        <div className="min-h-screen bg-gray-100 p-6">
            <h2 className="text-2xl font-bold mb-4 text-gray-800">목록</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                {gameRooms.map((room) => (
                    <div
                        key={room.id}
                        className="bg-white rounded-xl shadow hover:shadow-lg p-5 flex flex-col justify-between transition"
                    >
                        <div>
                            <h3 className="text-lg font-semibold text-gray-900">{room.name}</h3>
                            <p className="text-sm text-gray-500 mt-1">방장: {room.host}</p>
                            <p className="text-sm text-gray-500">인원: {room.players}명</p>
                        </div>
                        <button
                            onClick={() => handleEnter(room.id)}
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
