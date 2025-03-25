## MySQL 접속 확인

```aiignore
docker exec -it wgp-mysql mysql -u webcanvas_app -p
```
비밀번호 입력 후 MySQL 쉘 진입

이후 DB 추가시 init-scripts/init.sql에 추가 및 볼륨 삭제 후 재실행

```aiignore
docker-compose down -v mysql
```