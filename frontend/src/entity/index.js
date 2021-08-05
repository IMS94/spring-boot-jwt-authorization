
export class LoginResponse {
    jwt;

    constructor(obj) {
        this.jwt = obj.jwt;
    }
}

export class User {
    username;

    constructor(obj) {
        this.username = obj.username;
    }
}
