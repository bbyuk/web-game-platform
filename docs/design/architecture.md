## 아키텍처 개요

사용자 처리 및 방 입장 등 게임 핵심 기능 외적인 기능 처리는 모두 플랫폼 서비스에 위임한다.
게임 세션이 종료되어 게임 상태가 변경되는 부분에 대해서는 Amazon SQS로 비동기 메세지 처리.

https://app.diagrams.net/?dark=auto#G1zikalSnMyA7gAxC4hby9F3gtfFi5S9Rn


## Inner Architecture 개요

#### 서비스 레이어 메소드 Argument 
- Entity를 파라미터로 전달해야 할 시 Entity의 id를 파라미터로 전달한다. 
#### ex)
```
UserService.findUser(Long id); // (O)
UserService.findUser(User user); // (X)
``` 

#### 레포지토리 레이어 메소드 Argument
- 저장(CUD) 로직일 경우 서비스 레이어에서 파라미터를 Entity로 전달한다.
- 조회 로직인 경우 필드 조건 값 또는 Entity를 파라미터를 전달할 수 있다. 
단, Entity와 필드 조건 값을 섞어쓰기 보다는 최대한 한 쪽으로 통일할 것.

#### ex)
```
UserRepository.save(User user); (O)
UserRepository.save(Long id); (X)

GameRoomRepository.findByGameRoomIdAndUser(Long gameRoomId, User user); (X)
GameRoomRepository.findByGameRoomAndUser(GameRoom gameRoom, User user); (O)
GameRoomRepository.findByGameRoomIdAndUserId(Long gameRoomId, Long userId); (O)
```