import { User } from "../entity";
import { secureClient } from "./client";

function getUserInfo() {
    return new Promise((resolve, reject) => {
        secureClient.get("user")
            .then(response => resolve(new User(response.data)))
            .catch(e => reject("Unable to fetch user"));
    });
}

export {
    getUserInfo
}
