# 📖 API Specification

MyFin 서비스의 API 명세서입니다.  
API의 자세한 정보를 보고 싶다면, 각 API 좌측에 위치한 토글 버튼을 클릭해주세요.

최근 수정일시: 2023-07-13

<!--
<br>
<details>
<summary style="font-size: large; font-weight: bold">Name API</summary>

### 검토한 정보
```

```

### 상세 검토
- 저장이 필요한 정보
    -
- 요청/응답 구조
    - 요청
    ```json
    ```
    - 응답
    ```json
    ```
</details>
-->

## 유저 관련 API
<!-- 아이디 중복확인 API -->
<details>
<summary style="font-size: large; font-weight: bold">아이디 중복확인 API</summary>

### 검토한 정보
```
- GET /sign-up/check-id?key={userId}
- 파라미터: 아이디
- 정책
  - 실패 응답 정책
    - 파라미터(key)가 없을 경우 -> 400 BadRequest
- 성공 응답: 사용가능여부(if true, 사용가능) -> 200 OK
```

### 상세 검토
- 저장이 필요한 정보
  - 없음
- 요청/응답 구조
  - 요청
  ```
  GET /api/v1/sign-up/check-id?key={userId}
  "userId": "testerid"
    ```
  - 응답
  ```json
  {
    "result": true
  }
    ```
</details>

<!-- 휴대폰 본인인증 문자 요청 API -->
<br>
<details>
<summary style="font-size: large; font-weight: bold">휴대폰 본인인증 문자발신요청 API</summary>

### 검토한 정보
```
- POST /api/v1/sign-up/verify/request
- 파라미터: 휴대폰번호
- 정책
  - 실패 응답 정책
    - 휴대폰번호가 없거나 비어있는 경우 -> 400 BadRequest
    - 휴대폰번호가 올바른 형식이 아닌 경우 -> 400 BadRequest
- 성공 응답: 요청시간 -> 200 OK
```

### 상세 검토
- 저장이 필요한 정보
  - 캐시 저장소 -> <휴대폰번호, 인증코드>
- 요청/응답 구조
    - 요청
    ```json
  {
      "phoneNum": "01012341234"
  }
    ```
    - 응답
    ```json
  {
      "requestedAt": "2023-07-01T12:12:00.123132"
  }
    ```
</details>

<!-- 휴대폰 본인인증 인증요청 API -->
<br>
<details>
<summary style="font-size: large; font-weight: bold">휴대폰 본인인증 인증요청 API</summary>

### 검토한 정보
```
- POST /api/v1/sign-up/verify
- 파라미터: 휴대폰번호, 인증코드
- 정책
  - 실패 응답 정책
    - 파라미터(휴대폰번호, 인증코드) 둘 중에 하나라도 없는 경우 -> 400 BadRequest
- 성공 응답: 인증결과<인증결과여부, 메시지> -> 200 OK
```

### 상세 검토
- 저장이 필요한 정보
  - 캐시저장소 => 휴대폰번호에 대한 인증코드 삭제
- 요청/응답 구조
    - 요청
    ```json
    {
      "phoneNum": "01012341234",
      "code": "123456"
    }
    ```
    - 응답
    ```json
    // 성공 시
    {
      "result": true,
      "message": "인증되었습니다." 
    }
    // 코드 불일치 실패 시
    {
      "result": false,
      "message": "인증번호가 일치하지 않습니다."
    }
    // 코드의 유효시간이 만료되어 실패 시
    {
      "result": false,
      "message": "인증 기간이 만료되었습니다."
    }
    ```
</details>

<br>
<!-- 회원가입 API -->
<details>
<summary style="font-size: large; font-weight: bold">회원가입 API</summary>

### 검토한 정보
```
- POST /api/v1/sign-up
- 파라미터: 아이디, 패스워드, 성명, 생년월일(yyyy-MM-dd), 성별, 도로명주소, 상세주소, 휴대폰번호, 이메일주소
- 정책
  - 실패 응답 정책
    - 아이디가 이미 존재하는 경우 -> 400 BadRequest
    - 패스워드가 올바른 형식이 아닌 경우 (영문자+숫자+특수문자 조합 8자리 이상) -> 400 BadRequest
    - 생년월일이 올바른 형식이 아닌 경우 -> 400 BadRequest
    - 생년월일의 일자가 올바른 일자가 아닌 경우 -> 400 BadRequest
    - 생년월일이 서버 현재 일자보다 이후인 경우 -> 400 BadRequest
    - 휴대폰번호가 올바른 형식이 아닌 경우 (010-xxxx-xxxx) -> 400 BadRequest
    - 휴대폰번호가 이미 존재하는 경우 -> 400 BadRequest
    - 이메일주소가 올바른 형식이 아닌 경우 (...@xxx.xx) -> 400 BadRequest
- 성공 응답: 유저정보(아이디, 성명, 생성일시) -> 201 Created
```

### 상세 검토
- 저장이 필요한 정보
  - USER 엔티티 (아이디, 패스워드, 성명, 생년월일, 성별, 도로명주소, 상세주소, 휴대폰번호, 이메일주소)
  - USER.생성일시 -> 서버 현재시간
  - USER.휴대폰번호 -> encrypt
- 요청/응답 구조
  - 요청
  ```json
  {
    "userId": "testerid",
    "password": "test1234!",
    "userName": "tester",
    "birthDate": "1997-01-01",
    "sex": false, // false: male, true: female
    "address1": "서울특별시 강남구 도산대로 18길",
    "address2": "10001호",
    "phoneNum": "01012341234",
    "email": "test@gmail.com"
  }
    ```
  - 응답
  ```json
  {
    "userId": "testerid",
    "userName": "tester",
    "createdAt": "2023-07-01T12:21:12.1232132"
  }
    ```
</details>

<!-- 로그인 API -->
<br>
<details>
<summary style="font-size: large; font-weight: bold">로그인 API</summary>

### 검토한 정보
```
- POST /api/v1/login
- 파라미터: 아이디, 패스워드
- 정책
  - 실패 응답 정책
    - 파라미터(아이디, 패스워드) 둘 중에 하나라도 없는 경우 -> 400 BadRequest
    - 아이디가 존재하지 않는 경우 -> 404 NotFound
    - 패스워드가 일치하지 않는 경우 -> 401 Unauthorized
    - 이미 탈퇴한 유저인 경우 -> 404 NotFound
- 성공 응답: 토큰정보(액세스토큰, 리프레시토큰) -> 200 OK
```

### 상세 검토
- 저장이 필요한 정보
  - USER.마지막 로그인 일시 => 서버 현재시간
- 요청/응답 구조
    - 요청
    ```json
    {
      "userId": "testerid",
      "password": "test12341"
    }
    ```
    - 응답
    ```json
    {
      "accessToken": "Bearer token",
      "refreshToken": "Bearer token",
      "lastLoggedInAt": "2023-07-01T12:12:00.123123"
    }
    ```
</details>

## 계좌 관련 API

<!-- 계좌 생성 API -->
<br>
<details>
<summary style="font-size: large; font-weight: bold">계좌 생성 API</summary>

### 검토한 정보
```
- POST /api/v1/accounts
- 파라미터: 액세스 토큰, 계좌 비밀번호, 초기 예치금 
- 정책
  - 실패 응답 정책
    - 액세스토큰이 없거나 유효하지 않은 경우 -> 401 Unauthorized
    - 탈퇴된 유저인 경우 -> 401 Unauthorized
    - 파라미터(계좌 비밀번호)가 없는 경우 -> 400 BadRequest
    - 계좌 비밀번호가 4자리의 숫자가 아닌 경우 -> 400 BadRequest
    - 계좌 비밀번호가 중복된 수가 나열되어 있는 경우 -> 400 BadRequest
    - 해당 유저가 이미 계좌를 보유하고 있는 경우 -> 400 BadRequest
- 성공 응답: 유저정보(성명, 휴대폰번호, 이메일, 생성일시), 계좌정보(계좌번호, 잔액, 생성일시)
```

### 상세 검토
- 저장이 필요한 정보
  - 계좌번호 생성 (14자리 랜덤 수)
  - 계좌비밀번호 암호화
  - ACCOUNT 엔티티 (계좌번호, 계좌비밀번호, 잔액, 생성일시, 최근수정일시)
- 요청/응답 구조
  - 요청
    ```json
    // HEADER
    {
      "Authorization": "Bearer access_token"
    }
    // BODY
    {
      "accountPassword": "1234",
      "initialBalance": 10000
    }
    ```
  - 응답
    ```json
    {
      "user": {
        "name": "테스터",
        "phoneNum": "01012341234",
        "email": "tester@test.com",
        "createdAt": "2023-07-01T12:12:00.123123"
      },
      "account": {
        "number": "12345678901234",
        "balance": 10000,
        "createdAt": "2023-07-01T12:12:00.123123"
      }
    }
    ```
</details>

<!-- 계좌 삭제 API -->
<br>
<details>
<summary style="font-size: large; font-weight: bold">계좌 삭제 API</summary>

### 검토한 정보
```
- DELETE /api/v1/accounts
- 파라미터: 액세스토큰, 계좌번호, 계좌비밀번호
- 정책
  - 실패 응답 정책
    - 액세스토큰이 없거나 유효하지 않은 경우 -> 401 Unauthorized
    - 이미 탈퇴된 유저인 경우 -> 401 Unauthorized
    - 파라미터(계좌번호, 계좌비밀번호)가 없는 경우 -> 400 BadRequest
    - 해당 유저의 계좌의 계좌번호와 요청 계좌번호가 일치하지 않는 경우 -> 403 Forbidden
    - 해당 유저의 계좌의 계좌비밀번호와 요청 계좌비밀번호가 일치하지 않는 경우 -> 403 Forbidden
    - 해당 유저의 계좌의 잔액이 0원이 초과할 경우 -> 400 BadRequest
    - 이미 삭제된 계좌인 경우 -> 404 NotFound
- 성공 응답: 계좌정보(계좌번호, 생성일시, 삭제일시) 
```

### 상세 검토
- 저장이 필요한 정보
  - ACCOUNT.삭제일시 -> 서버 현재시간
- 요청/응답 구조
  - 요청
    ```json
    // HEADER
    {
      "Authorization": "Bearer access_token"
    }
    // BODY
    {
      "accountNumber": "12345678901234",
      "accountPassword": "1234"
    }
    ```
  - 응답
    ```json
    {
      "accountNumber": "12345678901234",
      "createdAt": "2023-07-01T12:12:00.123123",
      "deletedAt": "2023-07-01T12:12:00.123123"
    }
    ```
</details>

<!-- 계좌 입금 API -->
<br>
<details>
<summary style="font-size: large; font-weight: bold">계좌 입금 API</summary>

### 검토한 정보
```
- POST /api/v1/deposit
- 파라미터: 액세스토큰, 계좌번호, 입금액
- 정책
  - 실패 응답 정책
    - 액세스토큰이 없거나 유효하지 않은 경우 -> 401 Unauthorized
    - 이미 탈퇴된 유저인 경우 -> 401 Unauthorized
    - 파라미터(계좌번호, 입금액)을 입력하지 않은 경우 -> 400 BadRequest
    - 입금액이 0원 이하인 경우 -> 400 BadRequest
    - 해당 유저가 계좌를 보유하고 있지 않은 경우 -> 404 NotFound
    - 해당 유저의 계좌의 계좌번호와 요청 계좌번호가 일치하지 않는 경우 -> 403 Forbidden
- 성공 응답: 계좌정보(계좌번호, 잔액, 생성일시, 수정일시), 거래정보(거래번호, 거래금액, 거래타입, 거래일시)
```

### 상세 검토
- 저장이 필요한 정보
  - 거래번호 생성 -> 영어+숫자 조합 랜덤 20자리
  - TRANSACTION 엔티티 (거래번호, 거래금액, 거래타입(DEPOSIT), 수취자 계좌번호(요청자), 거래일시)
  - ACCOUNT.잔액 -> + 입금액
- 요청/응답 구조
  - 요청
    ```json
    // HEADER
    {
      "Authorization": "Bearer access_token"
    }
    // BODY
    {
      "accountNumber": "12345678901234",
      "amount": 10000
    }
    ```
  - 응답
    ```json
    {
      "account": {
        "number": "12345678901234",
        "balance": 10000,
        "createdAt": "2023-07-01T12:12:00.123123",
        "updatedAt": "2023-07-01T12:12:00.123123"
      },
      "transaction": {
        "number": "ABCDEFGHIJ1234567890",
        "amount": 10000,
        "type": "DEPOSIT",
        "tradedAt": "2023-07-01T12:12:00.123123"
      }
    }
    ```
</details>

<!-- 계좌 출금 API -->
<br>
<details>
<summary style="font-size: large; font-weight: bold">계좌 출금 API</summary>

### 검토한 정보
```
- POST /api/v1/withdrawal
- 파라미터: 액세스토큰, 계좌번호, 계좌비밀번호, 출금액
- 정책
  - 실패 응답 정책
    - 액세스토큰이 없거나 유효하지 않은 경우 -> 401 Unauthorized
    - 이미 탈퇴된 유저인 경우 -> 401 Unauthorized
    - 파라미터(계좌번호, 계좌비밀번호, 출금액)을 입력하지 않은 경우 -> 400 BadRequest
    - 출금액이 0원 이하인 경우 -> 400 BadRequest
    - 해당 유저가 계좌를 보유하고 있지 않은 경우 -> 404 NotFound
    - 해당 유저의 계좌의 계좌번호와 요청 계좌번호가 일치하지 않는 경우 -> 403 Forbidden
    - 해당 유저의 계좌의 계좌비밀번호와 요청 계좌비밀번호가 일치하지 않는 경우 -> 403 Forbidden
- 성공 응답: 계좌정보(계좌번호, 잔액, 생성일시, 수정일시), 거래정보(거래번호, 거래금액, 거래타입, 거래일시)
```

### 상세 검토
- 저장이 필요한 정보
  - 거래번호 생성 -> 영어+숫자 조합 랜덤 20자리
  - TRANSACTION 엔티티 (거래번호, 거래금액, 거래타입(WITHDRAWAL), 수취자 계좌번호(요청자), 거래일시)
  - ACCOUNT.잔액 -> - 출금액
- 요청/응답 구조
  - 요청
    ```json
    // HEADER
    {
      "Authorization": "Bearer access_token"
    }
    // BODY
    {
      "accountNumber": "12345678901234",
      "accountPassword": "1234",
      "amount": 10000
    }
    ```
  - 응답
    ```json
    {
      "account": {
        "number": "12345678901234",
        "balance": 0,
        "createdAt": "2023-07-01T12:12:00.123123",
        "updatedAt": "2023-07-01T12:12:00.123123"
      },
      "transaction": {
        "number": "ABCDEFGHIJ1234567890",
        "amount": 10000,
        "type": "WITHDRAWAL",
        "tradedAt": "2023-07-01T12:12:00.123123"
      }
    }
    ```
</details>