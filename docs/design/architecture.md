## 아키텍처 개요

사용자 처리 및 방 입장 등 게임 핵심 기능 외적인 기능 처리는 모두 플랫폼 서비스에 위임한다.
게임 세션이 종료되어 게임 상태가 변경되는 부분에 대해서는 Amazon SQS로 비동기 메세지 처리.

https://app.diagrams.net/?dark=auto#G1zikalSnMyA7gAxC4hby9F3gtfFi5S9Rn
