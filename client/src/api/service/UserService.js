import {$secureAPI} from "../http.js";
import store from "../store/index.js";
import {setUser} from "../store/slice/userSlice.js";
import {showMessage} from "./InfoService.js";

export const changeName = async (name) => {
    return $secureAPI().post('user/change-name', {json: {name}})
        .json().then(user => store.dispatch(setUser(user)))
}

export const changePassword = async (oldPassword, newPassword) => {
    return $secureAPI().post('user/change-password', {json: {oldPassword, newPassword}})
        .then(response => {
            showMessage('Пароль обновлен')
            response.json().then(result => {
                localStorage.setItem('accessToken', result.token)
                store.dispatch(setUser(result.user))
            })
        }).catch(error => {

            switch (error.response.status) {
                case 400:
                    showMessage('Текущий пароль указан неверно', 'error')
                    throw "Uncorrected password!"
                default:
                    showMessage('Что-то пошло не так', 'warning')
                    throw "Error!"
            }
        });
}

export const getQrCode = async () => {
    return $secureAPI().get('user/mfa-code')
        .json().catch(error => {

            switch (error.response.status) {
                case 400:
                    showMessage('Текущий пароль указан неверно', 'error')
                    throw "Uncorrected password!"
                default:
                    showMessage('Что-то пошло не так', 'warning')
                    throw "Error!"
            }
        });
}

export const switchMFA = async (code = null) => {
    return $secureAPI().post('user/switch-mfa', {json: {code}})
        .json().catch(error => {
            switch (error.response.status) {
                case 400:
                    showMessage('Код указан неверно', 'error')
                    throw "Uncorrected token!"
                default:
                    showMessage('Что-то пошло не так', 'warning')
                    throw "Error!"
            }
        });
}