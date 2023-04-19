import {$API, $secureAPI} from "../http";
import {showMessage} from "./InfoService.js";
import {setUser} from "../store/slice/userSlice.js";
import store from "../store/index.js";

export const signUp = async (email, name, password) => {
    return $API.post('auth/sign-up', {json: {email, name, password}});
}

export const signIn = async (email, password, token) => {
    return $API.post('auth/sign-in', {json: {email, password, code: token}})
}

export const refresh = async () => {
    return $API.post('auth/refresh')
}

export const logout = async () => {
    const dispatch = store.dispatch;
    await $secureAPI().post('auth/logout')
        .finally(() => {
            showMessage('Вы вышли из аккаунта')
            localStorage.removeItem('accessToken')
            dispatch(setUser(null))
        })
}